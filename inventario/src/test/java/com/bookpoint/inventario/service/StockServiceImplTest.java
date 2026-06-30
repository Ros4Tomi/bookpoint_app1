package com.bookpoint.inventario.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookpoint.inventario.dto.StockRequestDTO;
import com.bookpoint.inventario.dto.StockResponseDTO;
import com.bookpoint.inventario.factory.TestDataFactory;
import com.bookpoint.inventario.model.Stock;
import com.bookpoint.inventario.repository.StockRepository;
import com.bookpoint.inventario.service.impl.StockServiceImpl;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private Long libroId = 500L;
    private Stock stockExistente;

    @BeforeEach
    void setUp() {
        stockExistente = TestDataFactory.crearStock(libroId, 10); // 10 unidades iniciales
    }

    @Test
    @DisplayName("Debería registrar stock inicial exitosamente")
    void registrarStockInicial_Exito() {
        StockRequestDTO request = TestDataFactory.crearStockRequestDTO(libroId, 50);

        when(stockRepository.existsByLibroId(libroId)).thenReturn(false);
        when(stockRepository.save(any(Stock.class))).thenAnswer(i -> i.getArgument(0));

        StockResponseDTO response = stockService.registrarStockInicial(request);

        assertNotNull(response);
        assertEquals(libroId, response.getLibroId());
        assertEquals(50, response.getCantidad());
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el libro ya tiene stock registrado")
    void registrarStockInicial_YaExiste_LanzaException() {
        StockRequestDTO request = TestDataFactory.crearStockRequestDTO(libroId, 50);
        when(stockRepository.existsByLibroId(libroId)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            stockService.registrarStockInicial(request);
        });

        assertTrue(ex.getMessage().contains("Ya existe un registro de stock"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Debería deducir unidades correctamente cuando hay stock suficiente")
    void deducirUnidades_Exito() {
        when(stockRepository.findByLibroId(libroId)).thenReturn(Optional.of(stockExistente));
        when(stockRepository.save(any(Stock.class))).thenAnswer(i -> i.getArgument(0));

        // Teníamos 10, deducimos 3
        StockResponseDTO response = stockService.deducirUnidades(libroId, 3);

        assertEquals(7, response.getCantidad());
        verify(stockRepository).save(stockExistente);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si se intenta descontar más del stock disponible")
    void deducirUnidades_StockInsuficiente_LanzaException() {
        when(stockRepository.findByLibroId(libroId)).thenReturn(Optional.of(stockExistente));

        // Teníamos 10, intentamos deducir 15
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            stockService.deducirUnidades(libroId, 15);
        });

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si la cantidad a deducir es 0 o negativa")
    void deducirUnidades_CantidadInvalida_LanzaException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            stockService.deducirUnidades(libroId, 0);
        });

        assertTrue(ex.getMessage().contains("mayor a cero"));
        verify(stockRepository, never()).findByLibroId(anyLong());
    }

    @Test
    @DisplayName("Verificar disponibilidad debe retornar TRUE si la cantidad requerida es <= al stock")
    void verificarDisponibilidad_ConStock_RetornaTrue() {
        when(stockRepository.findByLibroId(libroId)).thenReturn(Optional.of(stockExistente));

        // Requiere 10 y tenemos 10
        boolean disponible = stockService.verificarDisponibilidad(libroId, 10);

        assertTrue(disponible);
    }

    @Test
    @DisplayName("Verificar disponibilidad debe retornar FALSE si la cantidad requerida es mayor al stock")
    void verificarDisponibilidad_SinStock_RetornaFalse() {
        when(stockRepository.findByLibroId(libroId)).thenReturn(Optional.of(stockExistente));

        // Requiere 11 pero tenemos 10
        boolean disponible = stockService.verificarDisponibilidad(libroId, 11);

        assertFalse(disponible);
    }
}