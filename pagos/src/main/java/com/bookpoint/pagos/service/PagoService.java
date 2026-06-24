package com.bookpoint.pagos.service;

import com.bookpoint.pagos.dto.PagoRequestDTO;
import com.bookpoint.pagos.dto.PagoResponseDTO;

public interface PagoService {
    PagoResponseDTO procesarPago(PagoRequestDTO requestDTO);
    PagoResponseDTO obtenerPorId(Long id);
    PagoResponseDTO obtenerPorFacturaId(Long facturaId);
}