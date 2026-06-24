package com.bookpoint.promociones.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
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
public class PromocionRequestDTO {

    @NotBlank(message = "El código de cupón es obligatorio")
    @Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres")
    private String codigo;

    @NotBlank(message = "La descripción de la promoción es obligatoria")
    private String descripcion;

    @NotNull(message = "El porcentaje de descuento es requerido")
    @Min(value = 1, message = "El descuento mínimo es 1%")
    @Max(value = 100, message = "El descuento máximo no puede superar el 100%")
    private Double porcentajeDescuento;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de finalización es requerida")
    private LocalDateTime fechaFin;
}