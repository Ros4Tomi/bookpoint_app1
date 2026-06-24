package com.bookpoint.facturacion.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venta_id", nullable = false, unique = true)
    private Long ventaId;

    @Column(name = "numero_factura", nullable = false, unique = true)
    private String numeroFactura;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double impuestos; // Ejemplo: IVA 19%

    @Column(nullable = false)
    private Double total;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado;

    @PrePersist
    protected void onCreate() {
        this.fechaEmision = LocalDateTime.now();
    }
}