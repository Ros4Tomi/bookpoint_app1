package com.bookpoint.facturacion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaRequestDTO {

    @NotNull(message = "El ID de la venta es requerido")
    private Long ventaId;

    @NotNull(message = "El monto subtotal es requerido")
    @Positive(message = "El subtotal debe ser un valor positivo")
    private Double subtotal;
}