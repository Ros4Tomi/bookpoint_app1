package com.bookpoint.resenas.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookpoint.resenas.client.FacturacionClient;
import com.bookpoint.resenas.client.UsuarioClient;
import com.bookpoint.resenas.dto.ResenaRequestDTO;
import com.bookpoint.resenas.dto.UsuarioResponseDTO;
import com.bookpoint.resenas.exception.ResourceNotFoundException;
import com.bookpoint.resenas.factory.TestDataFactory;
import com.bookpoint.resenas.model.Resena;
import com.bookpoint.resenas.repository.ResenaRepository;
import com.bookpoint.resenas.service.impl.ResenaServiceImpl;

@ExtendWith(MockitoExtension.class)
class ResenaServiceImplTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private FacturacionClient facturacionClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ResenaServiceImpl resenaService;

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si el usuario NO existe en ms-usuarios")
    void publicarResena_UsuarioNoExiste_LanzaException() {
        // Arrange
        ResenaRequestDTO requestDTO = TestDataFactory.crearResenaRequestDTOAleatorio();
        when(usuarioClient.obtenerPorId(requestDTO.getUsuarioId())).thenThrow(new RuntimeException("Error Feign"));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            resenaService.publicarResena(requestDTO);
        });

        assertTrue(exception.getMessage().contains("no existe en los registros"));
        verify(facturacionClient, never()).obtenerHistorial(anyLong()); // Nunca debe llegar a verificar facturas
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el usuario está inactivo")
    void publicarResena_UsuarioInactivo_LanzaException() {
        // Arrange
        ResenaRequestDTO requestDTO = TestDataFactory.crearResenaRequestDTOAleatorio();
        UsuarioResponseDTO mockUsuario = new UsuarioResponseDTO();
        mockUsuario.setActivo(false); // Inactivo

        when(usuarioClient.obtenerPorId(requestDTO.getUsuarioId())).thenReturn(mockUsuario);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.publicarResena(requestDTO);
        });

        assertTrue(exception.getMessage().contains("cuenta de usuario se encuentra suspendida"));
        verify(facturacionClient, never()).obtenerHistorial(anyLong());
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el usuario NO compró el libro")
    void publicarResena_NoComproElLibro_LanzaException() {
        // Arrange
        ResenaRequestDTO requestDTO = TestDataFactory.crearResenaRequestDTOAleatorio();
        
        UsuarioResponseDTO mockUsuario = new UsuarioResponseDTO();
        mockUsuario.setActivo(true);
        when(usuarioClient.obtenerPorId(requestDTO.getUsuarioId())).thenReturn(mockUsuario);

        // Simulamos que ms-facturacion devuelve un historial vacío (no compró nada)
        when(facturacionClient.obtenerHistorial(requestDTO.getUsuarioId())).thenReturn(List.of());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            resenaService.publicarResena(requestDTO);
        });

        assertTrue(exception.getMessage().contains("Solo los usuarios que han adquirido este libro pueden dejar una reseña"));
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    @DisplayName("Debería calcular el promedio de calificación correctamente")
    void obtenerPromedioCalificacion_Exito() {
        // Arrange
        Long libroId = 1L;
        Resena r1 = new Resena(); r1.setCalificacion(4);
        Resena r2 = new Resena(); r2.setCalificacion(5);
        Resena r3 = new Resena(); r3.setCalificacion(4);
        // Promedio: (4+5+4) / 3 = 13 / 3 = 4.333 -> redondeado a 4.3

        when(resenaRepository.findByLibroId(libroId)).thenReturn(List.of(r1, r2, r3));

        // Act
        Double promedio = resenaService.obtenerPromedioCalificacion(libroId);

        // Assert
        assertEquals(4.3, promedio);
    }

    @Test
    @DisplayName("Debería devolver 0.0 si el libro no tiene reseñas al calcular el promedio")
    void obtenerPromedioCalificacion_SinResenas() {
        // Arrange
        Long libroId = 1L;
        when(resenaRepository.findByLibroId(libroId)).thenReturn(List.of()); // Lista vacía

        // Act
        Double promedio = resenaService.obtenerPromedioCalificacion(libroId);

        // Assert
        assertEquals(0.0, promedio);
    }

    @Test
    @DisplayName("Debería eliminar una reseña existente exitosamente")
    void eliminarResena_Exito() {
        // Arrange
        Long resenaId = 1L;
        when(resenaRepository.existsById(resenaId)).thenReturn(true);

        // Act
        resenaService.eliminarResena(resenaId);

        // Assert
        verify(resenaRepository, times(1)).deleteById(resenaId);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al intentar eliminar una reseña inexistente")
    void eliminarResena_NoExiste_LanzaException() {
        // Arrange
        Long resenaId = 99L;
        when(resenaRepository.existsById(resenaId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            resenaService.eliminarResena(resenaId);
        });

        verify(resenaRepository, never()).deleteById(anyLong());
    }
}