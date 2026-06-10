package com.bookpoint.inventario.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.inventario.dto.StockRequestDTO;
import com.bookpoint.inventario.dto.StockResponseDTO;
import com.bookpoint.inventario.exception.ResourceNotFoundException;
import com.bookpoint.inventario.model.Stock;
import com.bookpoint.inventario.repository.StockRepository;
import com.bookpoint.inventario.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public StockResponseDTO registrarStockInicial(StockRequestDTO requestDTO) {
        log.info("Estableciendo inventario inicial para el libro ID: {}", requestDTO.getLibroId());
        if (stockRepository.existsByLibroId(requestDTO.getLibroId())) {
            throw new IllegalArgumentException("Ya existe un registro de stock para el libro con ID: " + requestDTO.getLibroId());
        }
        Stock stock = new Stock();
        stock.setLibroId(requestDTO.getLibroId());
        stock.setCantidad(requestDTO.getCantidad());
        stock.setUbicacionBodega(requestDTO.getUbicacionBodega());
        return deEntidadADTO(stockRepository.save(stock));
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponseDTO obtenerStockPorLibro(Long libroId) {
        Stock stock = stockRepository.findByLibroId(libroId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró inventario para el libro ID: " + libroId));
        return deEntidadADTO(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockResponseDTO> obtenerTodoElInventario(Pageable pageable) {
        return stockRepository.findAll(pageable).map(this::deEntidadADTO);
    }

    @Override
    public StockResponseDTO actualizarUbicacion(Long libroId, String nuevaUbicacion) {
        Stock stock = stockRepository.findByLibroId(libroId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede mudar, libro ID no existe: " + libroId));
        stock.setUbicacionBodega(nuevaUbicacion);
        return deEntidadADTO(stockRepository.save(stock));
    }

    @Override
    public StockResponseDTO adicionarUnidades(Long libroId, Integer cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a sumar debe ser mayor a cero");
        Stock stock = stockRepository.findByLibroId(libroId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede incrementar stock, libro ID no existe: " + libroId));
        stock.setCantidad(stock.getCantidad() + cantidad);
        return deEntidadADTO(stockRepository.save(stock));
    }

    @Override
    public StockResponseDTO deducirUnidades(Long libroId, Integer cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a restar debe ser mayor a cero");
        Stock stock = stockRepository.findByLibroId(libroId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede restar stock, libro ID no existe: " + libroId));
        if (stock.getCantidad() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente en bodega. Unidades disponibles: " + stock.getCantidad());
        }
        stock.setCantidad(stock.getCantidad() - cantidad);
        return deEntidadADTO(stockRepository.save(stock));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(Long libroId, Integer cantidadRequerida) {
        return stockRepository.findByLibroId(libroId)
                .map(stock -> stock.getCantidad() >= cantidadRequerida)
                .orElse(false);
    }

    private StockResponseDTO deEntidadADTO(Stock stock) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .libroId(stock.getLibroId())
                .cantidad(stock.getCantidad())
                .ubicacionBodega(stock.getUbicacionBodega())
                .disponible(stock.getCantidad() > 0)
                .fechaActualizacion(stock.getFechaActualizacion())
                .build();
    }
}