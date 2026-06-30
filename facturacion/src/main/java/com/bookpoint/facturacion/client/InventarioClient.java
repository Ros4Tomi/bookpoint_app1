package com.bookpoint.facturacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Este cliente le dice a Feign que apunte al puerto 8084 donde corre tu ms-inventario
@FeignClient(name = "ms-inventario", url = "http://localhost:8084/api/inventarios")
public interface InventarioClient {

    // Llama al método PUT de tu StockController en inventario para restar las unidades de la bodega
    @PutMapping("/libro/{libroId}/descontar")
    void descontarStock(@PathVariable("libroId") Long libroId, @RequestParam("cantidad") Integer cantidad);
}