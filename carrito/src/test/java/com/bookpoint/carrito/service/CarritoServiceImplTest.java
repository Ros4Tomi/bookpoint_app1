package com.bookpoint.carrito.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookpoint.carrito.client.CatalogoClient;
import com.bookpoint.carrito.client.UsuarioClient;
import com.bookpoint.carrito.dto.CarritoResponseDTO;
import com.bookpoint.carrito.dto.ItemRequestDTO;
import com.bookpoint.carrito.dto.LibroResponseDTO;
import com.bookpoint.carrito.dto.UsuarioResponseDTO;
import com.bookpoint.carrito.factory.TestDataFactory;
import com.bookpoint.carrito.model.Carrito;
import com.bookpoint.carrito.model.ItemCarrito;
import com.bookpoint.carrito.repository.CarritoRepository;
import com.bookpoint.carrito.service.impl.CarritoServiceImpl;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Long usuarioId = 100L;
    private Long libroId = 200L;
    private Carrito carritoVacio;
    private LibroResponseDTO mockLibro;
    private UsuarioResponseDTO mockUsuario;

    @BeforeEach
    void setUp() {
        // Inicializamos los datos falsos con nuestro Factory
        carritoVacio = TestDataFactory.crearCarritoVacio(usuarioId);
        mockLibro = TestDataFactory.crearLibroMock(libroId);
        mockUsuario = TestDataFactory.crearUsuarioMock(usuarioId);

        // 🔥 GARANTIZAMOS LA SIMULACIÓN 🔥
        // Simulamos la respuesta de Feign Client asegurándonos de usar cualquier ID (anyLong)
        // y de forma específica para nuestro usuarioId por seguridad.
        lenient().when(usuarioClient.obtenerPorId(anyLong())).thenReturn(mockUsuario);
        lenient().when(usuarioClient.obtenerPorId(usuarioId)).thenReturn(mockUsuario);
    }

    @Test
    void obtenerOCrearCarrito_CuandoExiste_DebeRetornarCarritoDTO() {
        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carritoVacio));

        CarritoResponseDTO resultado = carritoService.obtenerOCrearCarrito(usuarioId);

        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertTrue(resultado.getItems().isEmpty());
        verify(carritoRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    void agregarItem_CuandoEsNuevoProducto_DebeAgregarloExitosamente() {
        ItemRequestDTO itemRequest = TestDataFactory.crearItemRequestDTO(libroId, 2);
        
        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carritoVacio));
        when(catalogoClient.obtenerLibroPorId(libroId)).thenReturn(mockLibro);
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarritoResponseDTO resultado = carritoService.agregarItem(usuarioId, itemRequest);

        assertNotNull(resultado);
        assertEquals(1, resultado.getItems().size());
        assertEquals(libroId, resultado.getItems().get(0).getLibroId());
        assertEquals(2, resultado.getItems().get(0).getCantidad());
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void actualizarCantidadItem_CuandoItemExiste_DebeModificarCantidad() {
        ItemCarrito itemExistente = TestDataFactory.crearItemCarrito(libroId, 2, carritoVacio);
        carritoVacio.getItems().add(itemExistente);

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carritoVacio));
        when(catalogoClient.obtenerLibroPorId(libroId)).thenReturn(mockLibro);
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarritoResponseDTO resultado = carritoService.actualizarCantidadItem(usuarioId, libroId, 5);

        assertNotNull(resultado);
        assertEquals(1, resultado.getItems().size());
        assertEquals(5, resultado.getItems().get(0).getCantidad());
    }

    @Test
    void removerItem_CuandoExisteEnElCarrito_DebeEliminarlo() {
        ItemCarrito itemARemover = TestDataFactory.crearItemCarrito(libroId, 3, carritoVacio);
        carritoVacio.getItems().add(itemARemover);

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carritoVacio));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarritoResponseDTO resultado = carritoService.removerItem(usuarioId, libroId);

        assertNotNull(resultado);
        assertTrue(resultado.getItems().isEmpty());
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void limpiarCarrito_CuandoPoseeItems_DebeDejarloVacio() {
        ItemCarrito item = TestDataFactory.crearItemCarrito(libroId, 1, carritoVacio);
        carritoVacio.getItems().add(item);

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carritoVacio));

        carritoService.limpiarCarrito(usuarioId);

        assertTrue(carritoVacio.getItems().isEmpty());
        verify(carritoRepository).save(carritoVacio);
    }
}