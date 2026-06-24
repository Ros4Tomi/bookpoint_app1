package com.bookpoint.facturacion.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.facturacion.dto.FacturaRequestDTO;
import com.bookpoint.facturacion.dto.FacturaResponseDTO;
import com.bookpoint.facturacion.exception.ResourceNotFoundException;
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
    private static final double PORCENTAJE_IVA = 0.19; // IVA Chile

    @Override
    public FacturaResponseDTO emitirFactura(FacturaRequestDTO requestDTO) {
        log.info("Generando factura para la venta ID: {}", requestDTO.getVentaId());

        facturaRepository.findByVentaId(requestDTO.getVentaId()).ifPresent(f -> {
            throw new IllegalArgumentException("Ya existe un documento fiscal emitido para la venta: " + requestDTO.getVentaId());
        });

        double subtotal = requestDTO.getSubtotal();
        double impuestos = Math.round((subtotal * PORCENTAJE_IVA) * 100.0) / 100.0;
        double total = subtotal + impuestos;

        Factura factura = new Factura();
        factura.setVentaId(requestDTO.getVentaId());
        factura.setSubtotal(subtotal);
        factura.setImpuestos(impuestos);
        factura.setTotal(total);
        factura.setEstado(EstadoFactura.EMITIDA);
        
        // Generar un número de folio correlativo único aleatorio simulado
        factura.setNumeroFactura("FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Factura guardada = facturaRepository.save(factura);
        return deEntidadADTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaResponseDTO obtenerPorId(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la factura con ID: " + id));
        return deEntidadADTO(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaResponseDTO obtenerPorVentaId(Long ventaId) {
        Factura factura = facturaRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró factura ligada a la venta: " + ventaId));
        return deEntidadADTO(factura);
    }

    @Override
    public FacturaResponseDTO cambiarEstado(Long id, String estado) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));
        try {
            factura.setEstado(EstadoFactura.valueOf(estado.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de factura inválido: " + estado);
        }
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