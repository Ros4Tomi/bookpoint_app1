package com.bookpoint.facturacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookpoint.facturacion.dto.VentaResponseDTO;

@FeignClient(name = "ms-ventas", url = "http://localhost:8090/api/ventas")
public interface VentaClient {

    @PatchMapping("/{id}/estado")
    VentaResponseDTO cambiarEstadoTransaccion(
            @PathVariable("id") Long id,
            @RequestParam("nuevoEstado") String nuevoEstado
    );
}