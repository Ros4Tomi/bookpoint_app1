package com.bookpoint.facturacion.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.facturacion.client.CarritoClient;
import com.bookpoint.facturacion.client.InventarioClient;
import com.bookpoint.facturacion.client.LogisticaClient;
import com.bookpoint.facturacion.client.PagoClient;
import com.bookpoint.facturacion.client.PromocionClient;
import com.bookpoint.facturacion.client.VentaClient; // <-- NUEVO IMPORT
import com.bookpoint.facturacion.dto.CarritoResponseDTO;
import com.bookpoint.facturacion.dto.EnvioRequestDTO;
import com.bookpoint.facturacion.dto.FacturaRequestDTO;
import com.bookpoint.facturacion.dto.FacturaResponseDTO;
import com.bookpoint.facturacion.dto.PagoRequestDTO;
import com.bookpoint.facturacion.dto.PagoResponseDTO;
import com.bookpoint.facturacion.dto.PromocionResponseDTO;
import com.bookpoint.facturacion.exception.ResourceNotFoundException;
import com.bookpoint.facturacion.model.DetalleFactura;
import com.bookpoint.facturacion.model.EstadoFactura;
import com.bookpoint.facturacion.model.Factura;
import com.bookpoint.facturacion.repository.FacturaRepository;
import com.bookpoint.facturacion.service.FacturaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final CarritoClient carritoClient;
    private final InventarioClient inventarioClient;
    private final LogisticaClient logisticaClient;
    private final PagoClient pagoClient;
    private final PromocionClient promocionClient;
    private final VentaClient ventaClient; // <-- INYECCIÓN DEL CLIENTE DE VENTAS
    
    private static final double PORCENTAJE_IVA = 0.19; 

    @Override
    public FacturaResponseDTO emitirFactura(FacturaRequestDTO requestDTO) {
        log.info("Iniciando orquestación de compra para Venta ID: {}", requestDTO.getVentaId());

        facturaRepository.findByVentaId(requestDTO.getVentaId()).ifPresent(f -> {
            throw new IllegalArgumentException("Ya existe factura para la venta: " + requestDTO.getVentaId());
        });

        CarritoResponseDTO carrito = carritoClient.obtenerCarrito(requestDTO.getUsuarioId());
        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito del usuario está vacío.");
        }

        double subtotal = carrito.getItems().stream().mapToDouble(item -> item.getSubtotal()).sum();
        
        // --- APLICAR CUPÓN ---
        if (requestDTO.getCodigoPromocional() != null && !requestDTO.getCodigoPromocional().isBlank()) {
            try {
                PromocionResponseDTO promo = promocionClient.aplicarCupón(requestDTO.getCodigoPromocional());
                double descuento = Math.round((subtotal * (promo.getPorcentajeDescuento() / 100.0)) * 100.0) / 100.0;
                subtotal -= descuento;
            } catch (Exception e) {
                log.error("Error al validar el cupón: {}", requestDTO.getCodigoPromocional());
                throw new IllegalArgumentException("El cupón de descuento ingresado no es válido, está inactivo o expiró.");
            }
        }

        double impuestos = Math.round((subtotal * PORCENTAJE_IVA) * 100.0) / 100.0;
        double total = subtotal + impuestos;

        Factura factura = Factura.builder()
                .ventaId(requestDTO.getVentaId())
                .usuarioId(requestDTO.getUsuarioId())
                .subtotal(subtotal)
                .impuestos(impuestos)
                .total(total)
                .estado(EstadoFactura.EMITIDA)
                .numeroFactura("FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        List<DetalleFactura> detalles = carrito.getItems().stream().map(item -> 
            DetalleFactura.builder()
                    .libroId(item.getLibroId())
                    .tituloLibro(item.getTituloLibro())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotal(item.getSubtotal())
                    .factura(factura)
                    .build()
        ).collect(Collectors.toList());

        factura.setDetalles(detalles);
        Factura guardada = facturaRepository.save(factura);

        // --- PASO 0: PROCESAR EL PAGO ---
        log.info("Llamando a ms-pagos para procesar la transacción de la Factura ID: {}", guardada.getId());
        PagoResponseDTO respuestaPago = pagoClient.registrarPago(PagoRequestDTO.builder()
                .facturaId(guardada.getId())
                .monto(guardada.getTotal())
                .metodoPago(requestDTO.getMetodoPago())
                .build());

        if ("RECHAZADO".equalsIgnoreCase(respuestaPago.getEstado())) {
            guardada.setEstado(EstadoFactura.ANULADA);
            facturaRepository.save(guardada);
            
            // Si falla, marcamos la venta original como CANCELADA
            try { ventaClient.cambiarEstadoTransaccion(requestDTO.getVentaId(), "CANCELADA"); } catch (Exception e) {}
            
            throw new IllegalStateException("¡PAGO RECHAZADO POR LA PASARELA! La compra ha sido cancelada.");
        }
        
        log.info("Pago aprobado con éxito. Transacción ID: {}", respuestaPago.getTransaccionId());

        // --- NUEVO PASO: ACTUALIZAR ESTADO DE LA VENTA A 'PAGADA' ---
        try {
            ventaClient.cambiarEstadoTransaccion(requestDTO.getVentaId(), "PAGADA");
            log.info("Estado de la venta {} actualizado a PAGADA", requestDTO.getVentaId());
        } catch (Exception e) {
            log.error("No se pudo actualizar el estado de la venta a PAGADA en ms-ventas", e);
        }

        // --- PASO 1: Descontar Inventario ---
        carrito.getItems().forEach(item -> {
            try { inventarioClient.descontarStock(item.getLibroId(), item.getCantidad()); } 
            catch (Exception e) { log.error("Error al descontar stock", e); }
        });

        // --- PASO 2: Generar Orden de Logística ---
        try {
            EnvioRequestDTO envioPayload = EnvioRequestDTO.builder()
                    .ventaId(requestDTO.getVentaId())
                    .direccionDespacho(requestDTO.getDireccionDespacho())
                    .comuna(requestDTO.getComuna())
                    .region(requestDTO.getRegion())
                    .build();
            logisticaClient.generarOrdenDespacho(envioPayload);
        } catch (Exception e) {
            log.error("Error al generar despacho en ms-logistica", e);
        }

        // --- PASO 3: Limpiar Carrito ---
        try { carritoClient.limpiarCarrito(requestDTO.getUsuarioId()); } 
        catch (Exception e) { log.error("Error al limpiar carrito", e); }

        return deEntidadADTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaResponseDTO obtenerPorId(Long id) {
        Factura factura = facturaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));
        return deEntidadADTO(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaResponseDTO obtenerPorVentaId(Long ventaId) {
        Factura factura = facturaRepository.findByVentaId(ventaId).orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));
        return deEntidadADTO(factura);
    }

    @Override
    public FacturaResponseDTO cambiarEstado(Long id, String estado) {
        Factura factura = facturaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));
        factura.setEstado(EstadoFactura.valueOf(estado.toUpperCase()));
        return deEntidadADTO(facturaRepository.save(factura));
    }

    private FacturaResponseDTO deEntidadADTO(Factura factura) {
        return FacturaResponseDTO.builder()
                .id(factura.getId())
                .ventaId(factura.getVentaId())
                .numeroFactura(factura.getNumeroFactura())
                .subtotal(factura.getSubtotal())
                .impuestos(factura.getImpuestos())
                .total(factura.getTotal())
                .fechaEmision(factura.getFechaEmision())
                .estado(factura.getEstado())
                .build();
    }
}