package com.bookpoint.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineaVentaResponseDTO {
    private Long id;
    private Long libroId;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}