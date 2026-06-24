package com.bookpoint.facturacion.service;

import com.bookpoint.facturacion.dto.FacturaRequestDTO;
import com.bookpoint.facturacion.dto.FacturaResponseDTO;

public interface FacturaService {
    FacturaResponseDTO emitirFactura(FacturaRequestDTO requestDTO);
    FacturaResponseDTO obtenerPorId(Long id);
    FacturaResponseDTO obtenerPorVentaId(Long ventaId);
    FacturaResponseDTO cambiarEstado(Long id, String estado);
}