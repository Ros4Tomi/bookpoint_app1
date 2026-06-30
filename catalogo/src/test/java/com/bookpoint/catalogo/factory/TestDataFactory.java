package com.bookpoint.catalogo.factory;

import java.util.Locale;

import com.bookpoint.catalogo.dto.LibroRequestDTO;
import com.bookpoint.catalogo.model.Categoria;
import com.bookpoint.catalogo.model.Libro;

import net.datafaker.Faker;

public class TestDataFactory {

    // Configurado en español para generar nombres y datos más realistas
    private static final Faker faker = new Faker(new Locale("es"));

    public static Libro crearLibroAleatorio() {
        Libro libro = new Libro();
        libro.setId(faker.number().randomNumber());
        libro.setIsbn(faker.number().digits(13)); // Genera 13 números puros, válido para el Regex
        libro.setTitulo(faker.book().title());
        libro.setAutor(faker.book().author());
        libro.setPrecio(faker.number().randomDouble(2, 4000, 60000)); // Tipo Double acorde a tu entidad
        libro.setCategoria(obtenerCategoriaAleatoria());
        libro.setDescripcion(faker.lorem().sentence(10));
        libro.setDisponible(faker.bool().bool());
        return libro;
    }

    public static LibroRequestDTO crearLibroRequestDTOAleatorio() {
        LibroRequestDTO dto = new LibroRequestDTO();
        dto.setIsbn(faker.number().digits(13));
        dto.setTitulo(faker.book().title());
        dto.setAutor(faker.book().author());
        dto.setPrecio(faker.number().randomDouble(2, 4000, 60000));
        dto.setCategoria(obtenerCategoriaAleatoria());
        dto.setDescripcion(faker.lorem().sentence(10));
        dto.setDisponible(true);
        return dto;
    }

    private static Categoria obtenerCategoriaAleatoria() {
        Categoria[] categorias = Categoria.values();
        return categorias[faker.number().numberBetween(0, categorias.length)];
    }
}