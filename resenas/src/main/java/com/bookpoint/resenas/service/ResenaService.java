package com.bookpoint.resenas.service;

import java.util.List;

import com.bookpoint.resenas.dto.ResenaRequestDTO;
import com.bookpoint.resenas.dto.ResenaResponseDTO;

public interface ResenaService {
    ResenaResponseDTO publicarResena(ResenaRequestDTO requestDTO);
    List<ResenaResponseDTO> obtenerResenasPorLibro(Long libroId);
    Double obtenerPromedioCalificacion(Long libroId);
    void eliminarResena(Long id);
}