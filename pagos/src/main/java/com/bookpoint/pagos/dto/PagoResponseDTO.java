package com.bookpoint.pagos.dto;

import java.time.LocalDateTime;

import com.bookpoint.pagos.model.EstadoPago;
import com.bookpoint.pagos.model.MetodoPago;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDTO {
    private Long id;
    private Long facturaId;
    private Double monto;
    private MetodoPago metodoPago;
    private EstadoPago estado;
    private String transaccionId;
    private LocalDateTime fechaPago;
}