package com.bookpoint.facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaRequestDTO {
    @NotNull(message = "El ID de venta es obligatorio")
    private Long ventaId;
    
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "La dirección de despacho es obligatoria")
    private String direccionDespacho;

    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    @NotBlank(message = "La región es obligatoria")
    private String region;

    @NotBlank(message = "El método de pago es obligatorio (E.g. WEBPAY)")
    private String metodoPago;

    private String codigoPromocional; 
}