package com.bookpoint.resenas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookpoint.resenas.model.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByLibroId(Long libroId);
    List<Resena> findByUsuarioId(Long usuarioId);
    boolean existsByLibroIdAndUsuarioId(Long libroId, Long usuarioId);
}