package com.bookpoint.catalogo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bookpoint.catalogo.dto.LibroRequestDTO;
import com.bookpoint.catalogo.dto.LibroResponseDTO;
import com.bookpoint.catalogo.model.Categoria;

public interface LibroService {
    LibroResponseDTO guardarLibro(LibroRequestDTO requestDTO);
    LibroResponseDTO obtenerPorId(Long id);
    LibroResponseDTO obtenerPorIsbn(String isbn);
    Page<LibroResponseDTO> obtenerTodos(Pageable pageable);
    Page<LibroResponseDTO> obtenerDisponibles(Pageable pageable);
    Page<LibroResponseDTO> obtenerPorCategoria(Categoria categoria, Pageable pageable);
    Page<LibroResponseDTO> buscarPorTitulo(String titulo, Pageable pageable);
    LibroResponseDTO actualizarLibro(Long id, LibroRequestDTO requestDTO);
    void eliminarLibro(Long id);
    void cambiarDisponibilidad(Long id, Boolean estado);
}