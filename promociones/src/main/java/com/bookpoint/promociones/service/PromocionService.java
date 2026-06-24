package com.bookpoint.promociones.service;

import com.bookpoint.promociones.dto.PromocionRequestDTO;
import com.bookpoint.promociones.dto.PromocionResponseDTO;

public interface PromocionService {
    PromocionResponseDTO crearPromocion(PromocionRequestDTO requestDTO);
    PromocionResponseDTO validarYObtenerCupón(String codigo);
    PromocionResponseDTO desactivarPromocion(Long id);
}