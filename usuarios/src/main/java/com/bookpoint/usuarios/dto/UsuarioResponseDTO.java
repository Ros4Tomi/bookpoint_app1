package com.bookpoint.usuarios.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bookpoint.usuarios.model.Rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto seguro para responder datos de usuarios sin exponer credenciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String nombreCompleto;
    private String username;
    private String email;
    private Rol rol;
    private Boolean activo;
    private LocalDate fechaNacimiento;
    private String telefono;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimaActualizacion;
    private LocalDateTime ultimoAcceso;
}