package com.bookpoint.ventas.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.bookpoint.ventas.dto.VentaRequestDTO;
import com.bookpoint.ventas.dto.VentaResponseDTO;
import com.bookpoint.ventas.model.EstadoVenta;
import com.bookpoint.ventas.service.VentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Slf4j
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearNuevaVenta(@Valid @RequestBody VentaRequestDTO requestDTO) {
        log.info("POST /api/ventas");
        return new ResponseEntity<>(ventaService.registrarVenta(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerVentaPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<VentaResponseDTO>> listarTodasLasVentas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ventaService.obtenerTodasLasVentas(pageable));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<VentaResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ventaService.obtenerVentasPorUsuario(usuarioId, pageable));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<Page<VentaResponseDTO>> listarPorEstado(
            @PathVariable EstadoVenta estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ventaService.obtenerVentasPorEstado(estado, pageable));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<VentaResponseDTO> cambiarEstadoTransaccion(
            @PathVariable Long id,
            @RequestParam EstadoVenta nuevoEstado) {
        return ResponseEntity.ok(ventaService.actualizarEstadoVenta(id, nuevoEstado));
    }
}