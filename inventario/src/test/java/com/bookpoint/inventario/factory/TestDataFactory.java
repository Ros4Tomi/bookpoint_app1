package com.bookpoint.inventario.factory;

import java.time.LocalDateTime;
import java.util.Locale;

import com.bookpoint.inventario.dto.StockRequestDTO;
import com.bookpoint.inventario.model.Stock;

import net.datafaker.Faker;

public class TestDataFactory {

    private static final Faker faker = new Faker(new Locale("es"));

    public static Stock crearStock(Long libroId, Integer cantidad) {
        Stock stock = new Stock();
        stock.setId(faker.number().randomNumber());
        stock.setLibroId(libroId);
        stock.setCantidad(cantidad);
        stock.setUbicacionBodega("Pasillo " + faker.letterify("?") + " - Estante " + faker.number().numberBetween(1, 10));
        stock.setFechaActualizacion(LocalDateTime.now());
        return stock;
    }

    public static StockRequestDTO crearStockRequestDTO(Long libroId, Integer cantidad) {
        StockRequestDTO dto = new StockRequestDTO();
        dto.setLibroId(libroId);
        dto.setCantidad(cantidad);
        dto.setUbicacionBodega("Pasillo " + faker.letterify("?") + " - Estante " + faker.number().numberBetween(1, 10));
        return dto;
    }
}