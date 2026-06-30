package com.bookpoint.facturacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bookpoint.facturacion.dto.PromocionResponseDTO;

@FeignClient(name = "ms-promociones", url = "http://localhost:8087/api/promociones")
public interface PromocionClient {

    @GetMapping("/validar/{codigo}")
    PromocionResponseDTO aplicarCupón(@PathVariable("codigo") String codigo);
}