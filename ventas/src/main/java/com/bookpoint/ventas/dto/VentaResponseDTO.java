package com.bookpoint.ventas.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bookpoint.ventas.model.EstadoVenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaResponseDTO {
    private Long id;
    private Long usuarioId;
    private LocalDateTime fechaVenta;
    private EstadoVenta estado;
    private Double total;
    private List<LineaVentaResponseDTO> lineas;
}