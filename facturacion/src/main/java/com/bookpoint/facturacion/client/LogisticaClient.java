package com.bookpoint.facturacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bookpoint.facturacion.dto.EnvioRequestDTO;

@FeignClient(name = "ms-logistica", url = "http://localhost:8085/api/envios")
public interface LogisticaClient {

    @PostMapping
    void generarOrdenDespacho(@RequestBody EnvioRequestDTO requestDTO);
}