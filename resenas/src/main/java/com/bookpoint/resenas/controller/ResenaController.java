package com.bookpoint.resenas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookpoint.resenas.dto.ResenaRequestDTO;
import com.bookpoint.resenas.dto.ResenaResponseDTO;
import com.bookpoint.resenas.service.ResenaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping
    public ResponseEntity<ResenaResponseDTO> crearComentario(@Valid @RequestBody ResenaRequestDTO requestDTO) {
        return new ResponseEntity<>(resenaService.publicarResena(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/libro/{libroId}")
    public ResponseEntity<List<ResenaResponseDTO>> listarPorLibro(@PathVariable Long libroId) {
        return ResponseEntity.ok(resenaService.obtenerResenasPorLibro(libroId));
    }

    @GetMapping("/libro/{libroId}/promedio")
    public ResponseEntity<Double> verRatingPromedio(@PathVariable Long libroId) {
        return ResponseEntity.ok(resenaService.obtenerPromedioCalificacion(libroId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarResena(@PathVariable Long id) {
        resenaService.eliminarResena(id);
        return ResponseEntity.noContent().build();
    }
}