package com.bookpoint.facturacion.dto;

import java.time.LocalDateTime;

import com.bookpoint.facturacion.model.EstadoFactura;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaResponseDTO {
    private Long id;
    private Long ventaId;
    private String numeroFactura;
    private Double subtotal;
    private Double impuestos;
    private Double total;
    private LocalDateTime fechaEmision;
    private EstadoFactura estado;
}