package com.bookpoint.facturacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioRequestDTO {
    private Long ventaId;
    private String direccionDespacho;
    private String comuna;
    private String region;
}