package com.bookpoint.logistica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookpoint.logistica.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    Optional<Envio> findByVentaId(Long ventaId);
    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);
}