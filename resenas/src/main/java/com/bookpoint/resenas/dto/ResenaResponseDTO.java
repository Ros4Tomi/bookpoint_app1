package com.bookpoint.resenas.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResenaResponseDTO {
    private Long id;
    private Long libroId;
    private Long usuarioId;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
}