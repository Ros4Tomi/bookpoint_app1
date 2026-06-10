package com.bookpoint.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequestDTO {

    @NotNull(message = "El ID del libro es obligatorio")
    private Long libroId;

    @NotNull(message = "La cantidad no puede estar vacía")
    @Min(value = 0, message = "El stock inicial no puede ser negativo")
    private Integer cantidad;

    @NotBlank(message = "La ubicación en bodega es obligatoria")
    @Size(max = 50, message = "La ubicación no puede exceder los 50 caracteres")
    private String ubicacionBodega;
}