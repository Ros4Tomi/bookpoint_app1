package com.bookpoint.catalogo.dto;

import java.time.LocalDateTime;

import com.bookpoint.catalogo.model.Categoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibroResponseDTO {
    private Long id;
    private String isbn;
    private String titulo;
    private String autor;
    private Double precio;
    private Categoria categoria;
    private String descripcion;
    private Boolean disponible;
    private Integer stock;   
    private LocalDateTime fechaCreacion;
}