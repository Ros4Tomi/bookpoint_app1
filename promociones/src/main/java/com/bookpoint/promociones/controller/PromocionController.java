package com.bookpoint.promociones.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookpoint.promociones.dto.PromocionRequestDTO;
import com.bookpoint.promociones.dto.PromocionResponseDTO;
import com.bookpoint.promociones.service.PromocionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
public class PromocionController {

    private final PromocionService promocionService;

    @PostMapping
    public ResponseEntity<PromocionResponseDTO> registrarPromocion(@Valid @RequestBody PromocionRequestDTO requestDTO) {
        return new ResponseEntity<>(promocionService.crearPromocion(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<PromocionResponseDTO> aplicarCupón(@PathVariable String codigo) {
        return ResponseEntity.ok(promocionService.validarYObtenerCupón(codigo));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<PromocionResponseDTO> darDeBajaPromocion(@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.desactivarPromocion(id));
    }
}