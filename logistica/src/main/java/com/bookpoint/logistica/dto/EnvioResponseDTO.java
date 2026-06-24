package com.bookpoint.logistica.dto;

import java.time.LocalDateTime;

import com.bookpoint.logistica.model.EstadoEnvio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioResponseDTO {
    private Long id;
    private Long ventaId;
    private String direccionDespacho;
    private String comuna;
    private String region;
    private EstadoEnvio estado;
    private String codigoSeguimiento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntregaEstimada;
}