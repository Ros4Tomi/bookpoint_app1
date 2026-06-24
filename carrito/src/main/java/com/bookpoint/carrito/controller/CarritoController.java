package com.bookpoint.carrito.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookpoint.carrito.dto.CarritoResponseDTO;
import com.bookpoint.carrito.dto.ItemRequestDTO;
import com.bookpoint.carrito.service.CarritoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/carritos")
@RequiredArgsConstructor
@Slf4j
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> obtenerCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerOCrearCarrito(usuarioId));
    }

    @PostMapping("/usuario/{usuarioId}/items")
    public ResponseEntity<CarritoResponseDTO> agregarItemAlCarrito(
            @PathVariable Long usuarioId,
            @Valid @RequestBody ItemRequestDTO itemDTO) {
        log.info("POST /api/carritos/usuario/{}/items", usuarioId);
        return ResponseEntity.ok(carritoService.agregarItem(usuarioId, itemDTO));
    }

    @PutMapping("/usuario/{usuarioId}/items/{libroId}")
    public ResponseEntity<CarritoResponseDTO> modificarCantidadItem(
            @PathVariable Long usuarioId,
            @PathVariable Long libroId,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(carritoService.actualizarCantidadItem(usuarioId, libroId, cantidad));
    }

    @DeleteMapping("/usuario/{usuarioId}/items/{libroId}")
    public ResponseEntity<CarritoResponseDTO> quitarItemDeCarrito(
            @PathVariable Long usuarioId,
            @PathVariable Long libroId) {
        return ResponseEntity.ok(carritoService.removerItem(usuarioId, libroId));
    }

    @DeleteMapping("/usuario/{usuarioId}/limpiar")
    public ResponseEntity<Void> vaciarCarritoCompleto(@PathVariable Long usuarioId) {
        carritoService.limpiarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }
}