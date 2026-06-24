package com.bookpoint.facturacion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookpoint.facturacion.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByVentaId(Long ventaId);
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    boolean existsByNumeroFactura(String numeroFactura);
}