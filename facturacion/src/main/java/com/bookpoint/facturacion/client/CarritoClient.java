package com.bookpoint.facturacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bookpoint.facturacion.dto.CarritoResponseDTO;

@FeignClient(name = "ms-carrito", url = "http://localhost:8089/api/carrito")
public interface CarritoClient {


    @GetMapping("/usuario/{usuarioId}")
    CarritoResponseDTO obtenerCarrito(@PathVariable("usuarioId") Long usuarioId);


    @DeleteMapping("/usuario/{usuarioId}/limpiar")
    void limpiarCarrito(@PathVariable("usuarioId") Long usuarioId);
}