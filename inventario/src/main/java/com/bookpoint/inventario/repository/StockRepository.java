package com.bookpoint.inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookpoint.inventario.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByLibroId(Long libroId);
    boolean existsByLibroId(Long libroId);
}