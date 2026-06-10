package com.bookpoint.usuarios.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookpoint.usuarios.dto.LoginRequestDTO;
import com.bookpoint.usuarios.dto.UsuarioRequestDTO;
import com.bookpoint.usuarios.dto.UsuarioResponseDTO;
import com.bookpoint.usuarios.model.Rol;
import com.bookpoint.usuarios.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * controlador REST del microservicio de usuarios
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        log.info("POST /api/usuarios - Registrando usuario: {}", requestDTO.getUsername());
        UsuarioResponseDTO usuarioCreado = usuarioService.crearUsuario(requestDTO);
        return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        log.info("POST /api/usuarios/login - Solicitud de acceso de: {}", loginDTO.getUsername());
        UsuarioResponseDTO response = usuarioService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/usuarios/{} - Consultando perfil", id);
        UsuarioResponseDTO response = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * ENDPOINT ENTRE MICROSERVICIOS (OpenFeign)
     * Permite a ventas o carrito validar de inmediato si un ID de usuario existe y esta activo
     */
    @GetMapping("/{id}/validar-feign")
    public ResponseEntity<Boolean> validarUsuarioActivoParaFeign(@PathVariable Long id) {
        log.info("Feign-Call -> Validando estado operativo del usuario ID: {}", id);
        try {
            UsuarioResponseDTO usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario.getActivo());
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        log.info("GET /api/usuarios - Listando paginación");
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios(pageable));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<Page<UsuarioResponseDTO>> obtenerPorRol(
            @PathVariable Rol rol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usuarioService.obtenerUsuariosPorRol(rol, pageable));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("GET /api/usuarios/estadisticas - Generando panel de control");
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total_activos", usuarioService.contarUsuariosActivos());
        estadisticas.put("total_administradores", usuarioService.contarUsuariosPorRol(Rol.ADMIN));
        estadisticas.put("total_clientes", usuarioService.contarUsuariosPorRol(Rol.USER));
        
        return ResponseEntity.ok(estadisticas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO requestDTO) {
        
        log.info("PUT /api/usuarios/{} - Modificando datos de perfil", id);
        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(id, requestDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("DELETE /api/usuarios/{} - Eliminando registro físico", id);
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
        log.info("PATCH /api/usuarios/{}/desactivar - Bloqueando usuario temporalmente", id);
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarUsuario(@PathVariable Long id) {
        log.info("PATCH /api/usuarios/{}/activar - Reactivando acceso", id);
        usuarioService.activarUsuario(id);
        return ResponseEntity.ok().build();
    }
}