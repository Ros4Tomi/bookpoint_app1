package com.bookpoint.pagos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookpoint.pagos.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByFacturaId(Long facturaId);
    Optional<Pago> findByTransaccionId(String transaccionId);
}