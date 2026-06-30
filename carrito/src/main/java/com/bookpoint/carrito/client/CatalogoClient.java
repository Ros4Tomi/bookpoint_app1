package com.bookpoint.carrito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bookpoint.carrito.dto.LibroResponseDTO;

@FeignClient(name = "ms-catalogo", url = "http://localhost:8082/api/libros")
public interface CatalogoClient {

    @GetMapping("/{id}")
    LibroResponseDTO obtenerLibroPorId(@PathVariable("id") Long id);
}