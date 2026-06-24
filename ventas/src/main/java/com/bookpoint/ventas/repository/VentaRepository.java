package com.bookpoint.ventas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookpoint.ventas.model.EstadoVenta;
import com.bookpoint.ventas.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    Page<Venta> findByUsuarioId(Long usuarioId, Pageable pageable);
    Page<Venta> findByEstado(EstadoVenta estado, Pageable pageable);
}