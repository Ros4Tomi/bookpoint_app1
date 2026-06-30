package com.bookpoint.usuarios.factory;

import java.time.ZoneId;
import java.util.Locale;

import com.bookpoint.usuarios.dto.UsuarioRequestDTO;
import com.bookpoint.usuarios.model.Rol;
import com.bookpoint.usuarios.model.Usuario;

import net.datafaker.Faker;

public class TestDataFactory {

    private static final Faker faker = new Faker(new Locale("es"));

    public static Usuario crearUsuarioAleatorio() {
        Usuario usuario = new Usuario();
        usuario.setId(faker.number().randomNumber());
        usuario.setNombreCompleto(faker.name().fullName());
        
        // Limpiamos el username para que cumpla tu Regex (solo letras y números)
        String usernameLimpio = faker.name().username().replaceAll("[^a-zA-Z0-9_]", "") + faker.number().digits(3);
        usuario.setUsername(usernameLimpio);
        
        usuario.setEmail(faker.internet().emailAddress());
        usuario.setPassword(faker.internet().password(8, 15, true, true, true));
        usuario.setRol(Rol.USER);
        usuario.setActivo(true);
        usuario.setFechaNacimiento(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        usuario.setTelefono(faker.phoneNumber().cellPhone());
        return usuario;
    }

    public static UsuarioRequestDTO crearUsuarioRequestDTOAleatorio() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombreCompleto(faker.name().fullName());
        
        String usernameLimpio = faker.name().username().replaceAll("[^a-zA-Z0-9_]", "") + faker.number().digits(3);
        dto.setUsername(usernameLimpio);
        
        dto.setEmail(faker.internet().emailAddress());
        dto.setPassword(faker.internet().password(8, 15, true, true, true));
        dto.setRol(Rol.USER);
        dto.setActivo(true);
        dto.setFechaNacimiento(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        dto.setTelefono(faker.phoneNumber().cellPhone());
        return dto;
    }
}