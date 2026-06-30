package com.bookpoint.resenas;

import com.bookpoint.resenas.client.FacturacionClient;
import com.bookpoint.resenas.client.UsuarioClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ResenasApplicationTests {

    // Simulamos los clientes externos para que el contexto cargue sin internet ni otros microservicios
    @MockBean
    private FacturacionClient facturacionClient;

    @MockBean
    private UsuarioClient usuarioClient;

    @Test
    void contextLoads() {
    }
}
