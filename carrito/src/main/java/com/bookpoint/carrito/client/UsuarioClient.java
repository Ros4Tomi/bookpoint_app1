package com.bookpoint.carrito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bookpoint.carrito.dto.UsuarioResponseDTO;

@FeignClient(name = "ms-usuarios", url = "http://localhost:8081/api/usuarios")
public interface UsuarioClient {

    @GetMapping("/{id}")
    UsuarioResponseDTO obtenerPorId(@PathVariable("id") Long id);
}