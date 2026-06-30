package com.bookpoint.resenas.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bookpoint.resenas.dto.FacturaResponseDTO;

@FeignClient(name = "ms-facturacion", url = "http://localhost:8083/api/facturas")
public interface FacturacionClient {

    @GetMapping("/usuario/{usuarioId}")
    List<FacturaResponseDTO> obtenerHistorial(@PathVariable("usuarioId") Long usuarioId);
}