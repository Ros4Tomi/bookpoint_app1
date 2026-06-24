package com.bookpoint.logistica.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.logistica.dto.EnvioRequestDTO;
import com.bookpoint.logistica.dto.EnvioResponseDTO;
import com.bookpoint.logistica.exception.ResourceNotFoundException;
import com.bookpoint.logistica.model.Envio;
import com.bookpoint.logistica.model.EstadoEnvio;
import com.bookpoint.logistica.repository.EnvioRepository;
import com.bookpoint.logistica.service.EnvioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;

    @Override
    public EnvioResponseDTO registrarDespacho(EnvioRequestDTO requestDTO) {
        log.info("Iniciando logística para la venta ID: {}", requestDTO.getVentaId());

        envioRepository.findByVentaId(requestDTO.getVentaId()).ifPresent(e -> {
            throw new IllegalArgumentException("La orden de despacho ya existe para la venta: " + requestDTO.getVentaId());
        });

        Envio envio = new Envio();
        envio.setVentaId(requestDTO.getVentaId());
        envio.setDireccionDespacho(requestDTO.getDireccionDespacho());
        envio.setComuna(requestDTO.getComuna());
        envio.setRegion(requestDTO.getRegion());
        envio.setEstado(EstadoEnvio.PREPARANDO);
        
        // Simulación de generación automática de un código de seguimiento de Starken/Chilexpress
        envio.setCodigoSeguimiento("STK-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());

        Envio guardado = envioRepository.save(envio);
        return deEntidadADTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerPorId(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el envío con ID: " + id));
        return deEntidadADTO(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerPorVentaId(Long ventaId) {
        Envio envio = envioRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("No hay registro de despacho para la venta: " + ventaId));
        return deEntidadADTO(envio);
    }

    @Override
    public EnvioResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de despacho no encontrada"));
        try {
            envio.setEstado(EstadoEnvio.valueOf(nuevoEstado.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de despacho inválido: " + nuevoEstado);
        }
        return deEntidadADTO(envioRepository.save(envio));
    }

    private EnvioResponseDTO deEntidadADTO(Envio envio) {
        return EnvioResponseDTO.builder()
                .id(envio.getId())
                .ventaId(envio.getVentaId())
                .direccionDespacho(envio.getDireccionDespacho())
                .comuna(envio.getComuna())
                .region(envio.getRegion())
                .estado(envio.getEstado())
                .codigoSeguimiento(envio.getCodigoSeguimiento())
                .fechaCreacion(envio.getFechaCreacion())
                .fechaEntregaEstimada(envio.getFechaEntregaEstimada())
                .build();
    }
}