package com.bookpoint.facturacion.dto;

import java.time.LocalDateTime;

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
    private String estado;
    private Double total;
}