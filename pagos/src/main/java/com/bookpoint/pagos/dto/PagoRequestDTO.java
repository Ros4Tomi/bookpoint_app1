package com.bookpoint.pagos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO {

    @NotNull(message = "El ID de la factura es requerido")
    private Long facturaId;

    @NotNull(message = "El monto a pagar es requerido")
    @Positive(message = "El monto debe ser un valor positivo")
    private Double monto;

    @NotBlank(message = "El método de pago es requerido (TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, EFECTIVO)")
    private String metodoPago;
}