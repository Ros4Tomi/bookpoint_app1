package com.bookpoint.logistica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioRequestDTO {

    @NotNull(message = "El ID de la venta es requerido")
    private Long ventaId;

    @NotBlank(message = "La dirección de despacho es obligatoria")
    private String direccionDespacho;

    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    @NotBlank(message = "La región es obligatoria")
    private String region;
}