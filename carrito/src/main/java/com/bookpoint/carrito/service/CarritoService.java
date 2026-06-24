package com.bookpoint.carrito.service;

import com.bookpoint.carrito.dto.CarritoResponseDTO;
import com.bookpoint.carrito.dto.ItemRequestDTO;

public interface CarritoService {
    CarritoResponseDTO obtenerOCrearCarrito(Long usuarioId);
    CarritoResponseDTO agregarItem(Long usuarioId, ItemRequestDTO itemDTO);
    CarritoResponseDTO actualizarCantidadItem(Long usuarioId, Long libroId, Integer cantidad);
    CarritoResponseDTO removerItem(Long usuarioId, Long libroId);
    void limpiarCarrito(Long usuarioId);
}