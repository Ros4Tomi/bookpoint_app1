package com.bookpoint.catalogo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookpoint.catalogo.dto.LibroRequestDTO;
import com.bookpoint.catalogo.dto.LibroResponseDTO;
import com.bookpoint.catalogo.model.Categoria;
import com.bookpoint.catalogo.service.LibroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
@Slf4j
public class LibroController {

    private final LibroService libroService;

    @PostMapping
    public ResponseEntity<LibroResponseDTO> crearLibro(@Valid @RequestBody LibroRequestDTO requestDTO) {
        log.info("POST /api/libros");
        return new ResponseEntity<>(libroService.guardarLibro(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerPorId(id));
    }

    @GetMapping("/{id}/validar-precio-feign")
    public ResponseEntity<Double> obtenerPrecioParaFeign(@PathVariable Long id) {
        log.info("Feign-Call -> Validar precio ID: {}", id);
        try {
            LibroResponseDTO libro = libroService.obtenerPorId(id);
            if (libro.getDisponible()) {
                return ResponseEntity.ok(libro.getPrecio());
            }
            return ResponseEntity.ok(-1.0);
        } catch (Exception e) {
            return ResponseEntity.ok(0.0);
        }
    }

    @GetMapping
    public ResponseEntity<Page<LibroResponseDTO>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "titulo") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(libroService.obtenerTodos(pageable));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<Page<LibroResponseDTO>> listarDisponibles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(libroService.obtenerDisponibles(pageable));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<Page<LibroResponseDTO>> listarPorCategoria(
            @PathVariable Categoria categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(libroService.obtenerPorCategoria(categoria, pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<LibroResponseDTO>> buscarPorTitulo(
            @RequestParam String titulo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(libroService.buscarPorTitulo(titulo, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequestDTO requestDTO) {
        return ResponseEntity.ok(libroService.actualizarLibro(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        libroService.cambiarDisponibilidad(id, false);
        return ResponseEntity.ok().build();
    }
}