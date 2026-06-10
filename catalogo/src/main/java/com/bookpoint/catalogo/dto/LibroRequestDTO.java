package com.bookpoint.catalogo.dto;

import com.bookpoint.catalogo.model.Categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibroRequestDTO {

    @NotBlank(message = "El ISBN es obligatorio")
    @Pattern(regexp = "^(97[89])?\\d{9}(\\d|X)$", message = "El formato de ISBN no es válido")
    private String isbn;

    @NotBlank(message = "El título del libro es obligatorio")
    @Size(max = 150, message = "El título no puede exceder los 150 caracteres")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(max = 100, message = "El nombre del autor no puede exceder los 100 caracteres")
    private String autor;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un número mayor a cero")
    private Double precio;

    @NotNull(message = "La categoría es obligatoria")
    private Categoria categoria;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    private Boolean disponible;
}