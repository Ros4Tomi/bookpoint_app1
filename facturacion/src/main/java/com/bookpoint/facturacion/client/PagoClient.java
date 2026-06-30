package com.bookpoint.facturacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bookpoint.facturacion.dto.PagoRequestDTO;
import com.bookpoint.facturacion.dto.PagoResponseDTO;

@FeignClient(name = "ms-pagos", url = "http://localhost:8086/api/pagos")
public interface PagoClient {

    @PostMapping
    PagoResponseDTO registrarPago(@RequestBody PagoRequestDTO requestDTO);
}