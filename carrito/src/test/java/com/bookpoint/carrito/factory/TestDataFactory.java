package com.bookpoint.carrito.factory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

import com.bookpoint.carrito.dto.ItemRequestDTO;
import com.bookpoint.carrito.dto.LibroResponseDTO;
import com.bookpoint.carrito.dto.UsuarioResponseDTO;
import com.bookpoint.carrito.model.Carrito;
import com.bookpoint.carrito.model.ItemCarrito;

import net.datafaker.Faker;

public class TestDataFactory {

    private static final Faker faker = new Faker(new Locale("es"));

    public static Carrito crearCarritoVacio(Long usuarioId) {
        Carrito carrito = new Carrito();
        carrito.setId(faker.number().randomNumber());
        carrito.setUsuarioId(usuarioId);
        carrito.setFechaActualizacion(LocalDateTime.now());
        carrito.setItems(new ArrayList<>());
        return carrito;
    }

    public static ItemCarrito crearItemCarrito(Long libroId, Integer cantidad, Carrito carrito) {
        ItemCarrito item = new ItemCarrito();
        item.setId(faker.number().randomNumber());
        item.setLibroId(libroId);
        item.setCantidad(cantidad);
        item.setCarrito(carrito);
        return item;
    }

    public static ItemRequestDTO crearItemRequestDTO(Long libroId, Integer cantidad) {
        return new ItemRequestDTO(libroId, cantidad);
    }

    public static LibroResponseDTO crearLibroMock(Long libroId) {
        return new LibroResponseDTO(
                libroId,
                faker.book().title(),
                faker.number().randomDouble(2, 10, 50),
                faker.number().numberBetween(10, 100) // Stock simulado
        );
    }

    public static UsuarioResponseDTO crearUsuarioMock(Long usuarioId) {
        return UsuarioResponseDTO.builder()
                .id(usuarioId)
                .nombreCompleto(faker.name().fullName())
                .username(faker.name().username().replaceAll("[^a-zA-Z0-9_]", ""))
                .email(faker.internet().emailAddress())
                .rol("USER")
                .activo(true)
                .build();
    }
}