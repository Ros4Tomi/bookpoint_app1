package com.bookpoint.facturacion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookpoint.facturacion.dto.FacturaRequestDTO;
import com.bookpoint.facturacion.dto.FacturaResponseDTO;
import com.bookpoint.facturacion.service.FacturaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    @PostMapping
    public ResponseEntity<FacturaResponseDTO> crearFactura(@Valid @RequestBody FacturaRequestDTO requestDTO) {
        return new ResponseEntity<>(facturaService.emitirFactura(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerPorId(id));
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<FacturaResponseDTO> obtenerPorVentaId(@PathVariable Long ventaId) {
        return ResponseEntity.ok(facturaService.obtenerPorVentaId(ventaId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<FacturaResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(facturaService.cambiarEstado(id, estado));
    }
}