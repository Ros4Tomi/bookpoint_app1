package com.bookpoint.pagos.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.pagos.dto.PagoRequestDTO;
import com.bookpoint.pagos.dto.PagoResponseDTO;
import com.bookpoint.pagos.exception.ResourceNotFoundException;
import com.bookpoint.pagos.model.EstadoPago;
import com.bookpoint.pagos.model.MetodoPago;
import com.bookpoint.pagos.model.Pago;
import com.bookpoint.pagos.repository.PagoRepository;
import com.bookpoint.pagos.service.PagoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;

    @Override
    public PagoResponseDTO procesarPago(PagoRequestDTO requestDTO) {
        log.info("Procesando pago para la factura ID: {} por un monto de: {}", requestDTO.getFacturaId(), requestDTO.getMonto());

        // Validar si la factura ya tiene un pago registrado y aprobado
        pagoRepository.findByFacturaId(requestDTO.getFacturaId()).ifPresent(p -> {
            if (p.getEstado() == EstadoPago.APROBADO) {
                throw new IllegalArgumentException("La factura " + requestDTO.getFacturaId() + " ya se encuentra completamente pagada.");
            }
        });

        MetodoPago metodo;
        try {
            metodo = MetodoPago.valueOf(requestDTO.getMetodoPago().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Método de pago no soportado: " + requestDTO.getMetodoPago());
        }

        Pago pago = new Pago();
        pago.setFacturaId(requestDTO.getFacturaId());
        pago.setMonto(requestDTO.getMonto());
        pago.setMetodoPago(metodo);
        
        // Simulación de la pasarela de pago (Ej: Webpay)
        // En un caso real, aquí conectarías con una API externa. 
        // Si el monto es válido, aprobamos la transacción de forma automática.
        if (requestDTO.getMonto() > 0) {
            pago.setEstado(EstadoPago.APROBADO);
            pago.setTransaccionId("TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        } else {
            pago.setEstado(EstadoPago.RECHAZADO);
            pago.setTransaccionId("TXN-FAILED");
        }

        Pago guardado = pagoRepository.save(pago);
        return deEntidadADTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el registro de pago con ID: " + id));
        return deEntidadADTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPorFacturaId(Long facturaId) {
        Pago pago = pagoRepository.findByFacturaId(facturaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró historial de pago para la factura: " + facturaId));
        return deEntidadADTO(pago);
    }

    private PagoResponseDTO deEntidadADTO(Pago pago) {
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .facturaId(pago.getFacturaId())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estado(pago.getEstado())
                .transaccionId(pago.getTransaccionId())
                .fechaPago(pago.getFechaPago())
                .build();
    }
}