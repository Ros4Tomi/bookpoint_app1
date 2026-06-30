package com.bookpoint.inventario.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookpoint.inventario.dto.StockRequestDTO;
import com.bookpoint.inventario.dto.StockResponseDTO;
import com.bookpoint.inventario.service.StockService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/inventarios")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockResponseDTO> crearRegistroStock(@Valid @RequestBody StockRequestDTO requestDTO) {
        log.info("POST /api/inventarios");
        return new ResponseEntity<>(stockService.registrarStockInicial(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/libro/{libroId}")
    public ResponseEntity<StockResponseDTO> obtenerPorLibro(@PathVariable Long libroId) {
        return ResponseEntity.ok(stockService.obtenerStockPorLibro(libroId));
    }

    @GetMapping
    public ResponseEntity<Page<StockResponseDTO>> listarInventarioCompleto(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(stockService.obtenerTodoElInventario(pageable));
    }

    @GetMapping("/libro/{libroId}/validar-stock-feign")
    public ResponseEntity<Boolean> verificarDisponibilidadParaFeign(
            @PathVariable Long libroId,
            @RequestParam Integer cantidad) {
        log.info("Feign-Call -> Validando unidades de stock para Libro ID: {}, Cantidad: {}", libroId, cantidad);
        return ResponseEntity.ok(stockService.verificarDisponibilidad(libroId, cantidad));
    }

    @PatchMapping("/libro/{libroId}/adicionar")
    public ResponseEntity<StockResponseDTO> agregarUnidades(@PathVariable Long libroId, @RequestParam Integer cantidad) {
        return ResponseEntity.ok(stockService.adicionarUnidades(libroId, cantidad));
    }

    // Cambiado a @PutMapping para alinearse al cliente Feign y retornar solo el estatus OK esperado
    @PutMapping("/libro/{libroId}/descontar")
    public ResponseEntity<Void> sustraerUnidades(@PathVariable Long libroId, @RequestParam Integer cantidad) {
        log.info("Feign-Call -> Solicitud de descuento en Inventario para Libro ID: {}, Cantidad: {}", libroId, cantidad);
        stockService.deducirUnidades(libroId, cantidad);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/libro/{libroId}/ubicacion")
    public ResponseEntity<StockResponseDTO> cambiarUbicacionBodega(@PathVariable Long libroId, @RequestParam String nuevaUbicacion) {
        return ResponseEntity.ok(stockService.actualizarUbicacion(libroId, nuevaUbicacion));
    }
}