package com.bookpoint.pagos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookpoint.pagos.dto.PagoRequestDTO;
import com.bookpoint.pagos.dto.PagoResponseDTO;
import com.bookpoint.pagos.service.PagoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> registrarPago(@Valid @RequestBody PagoRequestDTO requestDTO) {
        return new ResponseEntity<>(pagoService.procesarPago(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtenerPagoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<PagoResponseDTO> obtenerPagoPorFactura(@PathVariable Long facturaId) {
        return ResponseEntity.ok(pagoService.obtenerPorFacturaId(facturaId));
    }
}