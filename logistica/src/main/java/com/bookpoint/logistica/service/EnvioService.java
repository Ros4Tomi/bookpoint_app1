package com.bookpoint.logistica.service;

import com.bookpoint.logistica.dto.EnvioRequestDTO;
import com.bookpoint.logistica.dto.EnvioResponseDTO;

public interface EnvioService {
    EnvioResponseDTO registrarDespacho(EnvioRequestDTO requestDTO);
    EnvioResponseDTO obtenerPorId(Long id);
    EnvioResponseDTO obtenerPorVentaId(Long ventaId);
    EnvioResponseDTO actualizarEstado(Long id, String nuevoEstado);
}