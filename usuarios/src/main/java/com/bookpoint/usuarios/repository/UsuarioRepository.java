package com.bookpoint.usuarios.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bookpoint.usuarios.model.Rol;
import com.bookpoint.usuarios.model.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Usuario> findByActivoTrue();

    Page<Usuario> findByActivoTrue(Pageable pageable);

    List<Usuario> findByRol(Rol rol);

    Page<Usuario> findByRol(Rol rol, Pageable pageable);

    Page<Usuario> findByActivoAndRol(Boolean activo, Rol rol, Pageable pageable);

    Page<Usuario> findByNombreCompletoContainingIgnoreCase(String nombre, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.fechaRegistro >= :fecha")
    List<Usuario> findUsuariosRegistradosDespuesDe(@Param("fecha") LocalDateTime fecha);

    Long countByRol(Rol rol);

    Long countByActivoTrue();
}