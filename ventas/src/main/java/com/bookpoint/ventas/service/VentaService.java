package com.bookpoint.ventas.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bookpoint.ventas.dto.VentaRequestDTO;
import com.bookpoint.ventas.dto.VentaResponseDTO;
import com.bookpoint.ventas.model.EstadoVenta;

public interface VentaService {
    VentaResponseDTO registrarVenta(VentaRequestDTO requestDTO);
    VentaResponseDTO obtenerVentaPorId(Long id);
    Page<VentaResponseDTO> obtenerTodasLasVentas(Pageable pageable);
    Page<VentaResponseDTO> obtenerVentasPorUsuario(Long usuarioId, Pageable pageable);
    Page<VentaResponseDTO> obtenerVentasPorEstado(EstadoVenta estado, Pageable pageable);
    VentaResponseDTO actualizarEstadoVenta(Long id, EstadoVenta nuevoEstado);
}