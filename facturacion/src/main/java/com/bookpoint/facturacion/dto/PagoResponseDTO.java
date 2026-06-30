package com.bookpoint.facturacion.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Long id;
    private Long facturaId;
    private Double monto;
    private String metodoPago;
    private String estado; // Capturará APROBADO o RECHAZADO
    private String transaccionId;
    private LocalDateTime fechaPago;
}