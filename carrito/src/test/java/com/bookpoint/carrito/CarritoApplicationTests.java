package com.bookpoint.carrito;

import com.bookpoint.carrito.client.CatalogoClient;
import com.bookpoint.carrito.client.UsuarioClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class CarritoApplicationTests {

    @MockBean
    private CatalogoClient catalogoClient;

    @MockBean
    private UsuarioClient usuarioClient;

    @Test
    void contextLoads() {
    }
}