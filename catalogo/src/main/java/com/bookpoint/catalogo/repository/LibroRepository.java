package com.bookpoint.catalogo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookpoint.catalogo.model.Categoria;
import com.bookpoint.catalogo.model.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Libro> findByDisponibleTrue();
    Page<Libro> findByDisponibleTrue(Pageable pageable);
    Page<Libro> findByCategoria(Categoria categoria, Pageable pageable);
    Page<Libro> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    Page<Libro> findByAutorContainingIgnoreCase(String autor, Pageable pageable);
}