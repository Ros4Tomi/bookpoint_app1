package com.bookpoint.usuarios.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.usuarios.dto.LoginRequestDTO;
import com.bookpoint.usuarios.dto.UsuarioRequestDTO;
import com.bookpoint.usuarios.dto.UsuarioResponseDTO;
import com.bookpoint.usuarios.exception.ResourceNotFoundException;
import com.bookpoint.usuarios.model.Rol;
import com.bookpoint.usuarios.model.Usuario;
import com.bookpoint.usuarios.repository.UsuarioRepository;
import com.bookpoint.usuarios.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de lógica de negocio para usuarios de BookPoint
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO) {
        log.info("Procesando creación de usuario: {}", requestDTO.getUsername());

        validarUsername(requestDTO.getUsername());
        validarEmail(requestDTO.getEmail());

        Usuario usuario = convertirDTOAEntidad(requestDTO);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        log.info("Usuario guardado con éxito bajo el ID: {}", usuarioGuardado.getId());
        return convertirEntidadADTO(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {
        log.info("Buscando usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con ID: " + id));
        return convertirEntidadADTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> obtenerTodosLosUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::convertirEntidadADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> obtenerUsuariosActivos(Pageable pageable) {
        return usuarioRepository.findByActivoTrue(pageable).map(this::convertirEntidadADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> obtenerUsuariosPorRol(Rol rol, Pageable pageable) {
        return usuarioRepository.findByRol(rol, pageable).map(this::convertirEntidadADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> buscarPorNombre(String nombre, Pageable pageable) {
        return usuarioRepository.findByNombreCompletoContainingIgnoreCase(nombre, pageable).map(this::convertirEntidadADTO);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        log.info("Actualizando datos del usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar. ID no encontrado: " + id));

        // Validar cambios de credenciales únicas
        if (!usuario.getUsername().equals(requestDTO.getUsername())) {
            validarUsername(requestDTO.getUsername());
            usuario.setUsername(requestDTO.getUsername());
        }
        if (!usuario.getEmail().equals(requestDTO.getEmail())) {
            validarEmail(requestDTO.getEmail());
            usuario.setEmail(requestDTO.getEmail());
        }

        usuario.setNombreCompleto(requestDTO.getNombreCompleto());
        usuario.setPassword(requestDTO.getPassword()); // En producción usar encoder
        usuario.setFechaNacimiento(requestDTO.getFechaNacimiento());
        usuario.setTelefono(requestDTO.getTelefono());
        
        if (requestDTO.getRol() != null) usuario.setRol(requestDTO.getRol());
        if (requestDTO.getActivo() != null) usuario.setActivo(requestDTO.getActivo());

        return convertirEntidadADTO(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminarUsuario(Long id) {
        log.info("Eliminación física del usuario ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. ID inexistente: " + id));
        usuarioRepository.delete(usuario);
    }

    @Override
    public void desactivarUsuario(Long id) {
        log.info("Desactivación lógica del usuario ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID no encontrado para desactivar: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public void activarUsuario(Long id) {
        log.info("Activación del usuario ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID no encontrado para activar: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioResponseDTO login(LoginRequestDTO loginDTO) {
        log.info("Intento de login para username: {}", loginDTO.getUsername());
        Usuario usuario = usuarioRepository.findByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas (Username/Password incorrectos)"));

        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("La cuenta de usuario se encuentra desactivada");
        }

        registrarAcceso(usuario.getUsername());
        return convertirEntidadADTO(usuario);
    }

    @Override
    public void registrarAcceso(String username) {
        usuarioRepository.findByUsername(username).ifPresent(u -> {
            u.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(u);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarUsuariosPorRol(Rol rol) { return usuarioRepository.countByRol(rol); }

    @Override
    @Transactional(readOnly = true)
    public Long contarUsuariosActivos() { return usuarioRepository.countByActivoTrue(); }

    // Métodos de validación privados auxiliares
    private void validarUsername(String username) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario '" + username + "' ya está registrado");
        }
    }

    private void validarEmail(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El correo electrónico '" + email + "' ya está en uso");
        }
    }

    private Usuario convertirDTOAEntidad(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setRol(dto.getRol() != null ? dto.getRol() : Rol.USER);
        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setTelefono(dto.getTelefono());
        return usuario;
    }

    private UsuarioResponseDTO convertirEntidadADTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombreCompleto(usuario.getNombreCompleto())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .telefono(usuario.getTelefono())
                .fechaRegistro(usuario.getFechaRegistro())
                .ultimaActualizacion(usuario.getUltimaActualizacion())
                .ultimoAcceso(usuario.getUltimoAcceso())
                .build();
    }
}