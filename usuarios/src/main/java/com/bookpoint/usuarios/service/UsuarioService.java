package com.bookpoint.usuarios.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bookpoint.usuarios.dto.LoginRequestDTO;
import com.bookpoint.usuarios.dto.UsuarioRequestDTO;
import com.bookpoint.usuarios.dto.UsuarioResponseDTO;
import com.bookpoint.usuarios.model.Rol;

/**
 * Interfaz de negocio para la gestión de usuarios de BookPoint
 */
public interface UsuarioService {

    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO);

    UsuarioResponseDTO obtenerUsuarioPorId(Long id);

    Page<UsuarioResponseDTO> obtenerTodosLosUsuarios(Pageable pageable);

    Page<UsuarioResponseDTO> obtenerUsuariosActivos(Pageable pageable);

    Page<UsuarioResponseDTO> obtenerUsuariosPorRol(Rol rol, Pageable pageable);

    Page<UsuarioResponseDTO> buscarPorNombre(String nombre, Pageable pageable);

    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO requestDTO);

    void eliminarUsuario(Long id);

    void desactivarUsuario(Long id);

    void activarUsuario(Long id);

    UsuarioResponseDTO login(LoginRequestDTO loginDTO);

    void registrarAcceso(String username);

    Long contarUsuariosPorRol(Rol rol);

    Long contarUsuariosActivos();
}