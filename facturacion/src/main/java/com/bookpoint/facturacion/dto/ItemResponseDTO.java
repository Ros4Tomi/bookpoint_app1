package com.bookpoint.facturacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDTO {
    private Long id;
    private Long libroId;
    private Integer cantidad;
    private String tituloLibro;
    private Double precioUnitario;
    private Double subtotal;
}