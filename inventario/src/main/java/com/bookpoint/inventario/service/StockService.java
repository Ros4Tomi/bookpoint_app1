package com.bookpoint.inventario.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bookpoint.inventario.dto.StockRequestDTO;
import com.bookpoint.inventario.dto.StockResponseDTO;

public interface StockService {
    StockResponseDTO registrarStockInicial(StockRequestDTO requestDTO);
    StockResponseDTO obtenerStockPorLibro(Long libroId);
    Page<StockResponseDTO> obtenerTodoElInventario(Pageable pageable);
    StockResponseDTO actualizarUbicacion(Long libroId, String nuevaUbicacion);
    StockResponseDTO adicionarUnidades(Long libroId, Integer cantidad);
    StockResponseDTO deducirUnidades(Long libroId, Integer cantidad);
    boolean verificarDisponibilidad(Long libroId, Integer cantidadRequerida);
}