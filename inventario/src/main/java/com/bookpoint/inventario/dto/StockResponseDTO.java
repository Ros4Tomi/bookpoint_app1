package com.bookpoint.inventario.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponseDTO {
    private Long id;
    private Long libroId;
    private Integer cantidad;
    private String ubicacionBodega;
    private Boolean disponible;
    private LocalDateTime fechaActualizacion;
}