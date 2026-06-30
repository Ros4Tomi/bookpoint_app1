package com.bookpoint.resenas.factory;

import java.util.Locale;

import com.bookpoint.resenas.dto.ResenaRequestDTO;
import com.bookpoint.resenas.model.Resena;

import net.datafaker.Faker;

public class TestDataFactory {

    private static final Faker faker = new Faker(new Locale("es"));

    public static Resena crearResenaAleatoria() {
        Resena resena = new Resena();
        resena.setId(faker.number().randomNumber());
        resena.setLibroId(faker.number().randomNumber());
        resena.setUsuarioId(faker.number().randomNumber());
        resena.setCalificacion(faker.number().numberBetween(1, 6)); // Genera de 1 a 5 estrellas
        resena.setComentario(faker.lorem().sentence(15)); // Un comentario realista de 15 palabras
        return resena;
    }

    public static ResenaRequestDTO crearResenaRequestDTOAleatorio() {
        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setLibroId(faker.number().randomNumber());
        dto.setUsuarioId(faker.number().randomNumber());
        dto.setCalificacion(faker.number().numberBetween(1, 6));
        dto.setComentario(faker.lorem().sentence(15));
        return dto;
    }
}