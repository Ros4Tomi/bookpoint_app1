package com.bookpoint.catalogo.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookpoint.catalogo.dto.LibroRequestDTO;
import com.bookpoint.catalogo.dto.LibroResponseDTO;
import com.bookpoint.catalogo.exception.ResourceNotFoundException;
import com.bookpoint.catalogo.factory.TestDataFactory;
import com.bookpoint.catalogo.model.Libro;
import com.bookpoint.catalogo.repository.LibroRepository;
import com.bookpoint.catalogo.service.impl.LibroServiceImpl;

@ExtendWith(MockitoExtension.class)
class LibroServiceImplTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroServiceImpl libroService;

    @Test
    @DisplayName("Debería guardar un libro exitosamente cuando el ISBN no está duplicado")
    void guardarLibro_Exito() {
        // Arrange
        LibroRequestDTO requestDTO = TestDataFactory.crearLibroRequestDTOAleatorio();
        Libro libroGuardado = TestDataFactory.crearLibroAleatorio();
        
        // Sincronizamos campos para consistencia en las aserciones
        libroGuardado.setIsbn(requestDTO.getIsbn());
        libroGuardado.setTitulo(requestDTO.getTitulo());
        libroGuardado.setAutor(requestDTO.getAutor());
        libroGuardado.setPrecio(requestDTO.getPrecio());
        libroGuardado.setCategoria(requestDTO.getCategoria());

        when(libroRepository.existsByIsbn(requestDTO.getIsbn())).thenReturn(false);
        when(libroRepository.save(any(Libro.class))).thenReturn(libroGuardado);

        // Act
        LibroResponseDTO response = libroService.guardarLibro(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(libroGuardado.getId(), response.getId());
        assertEquals(requestDTO.getIsbn(), response.getIsbn());
        assertEquals(requestDTO.getTitulo(), response.getTitulo());
        verify(libroRepository, times(1)).existsByIsbn(requestDTO.getIsbn());
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException al intentar guardar si el ISBN ya existe")
    void guardarLibro_IsbnYaExiste_LanzaIllegalArgumentException() {
        // Arrange
        LibroRequestDTO requestDTO = TestDataFactory.crearLibroRequestDTOAleatorio();
        when(libroRepository.existsByIsbn(requestDTO.getIsbn())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            libroService.guardarLibro(requestDTO);
        });

        assertTrue(exception.getMessage().contains("El ISBN ya existe"));
        verify(libroRepository, times(1)).existsByIsbn(requestDTO.getIsbn());
        verify(libroRepository, never()).save(any(Libro.class));
    }

    @Test
    @DisplayName("Debería obtener un libro por su ID exitosamente")
    void obtenerPorId_Exito() {
        // Arrange
        Long libroId = 1L;
        Libro libroSimulado = TestDataFactory.crearLibroAleatorio();
        libroSimulado.setId(libroId);

        when(libroRepository.findById(libroId)).thenReturn(Optional.of(libroSimulado));

        // Act
        LibroResponseDTO response = libroService.obtenerPorId(libroId);

        // Assert
        assertNotNull(response);
        assertEquals(libroId, response.getId());
        assertEquals(libroSimulado.getTitulo(), response.getTitulo());
        verify(libroRepository, times(1)).findById(libroId);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar un libro con ID inexistente")
    void obtenerPorId_NoExiste_LanzaResourceNotFoundException() {
        // Arrange
        Long libroId = 99L;
        when(libroRepository.findById(libroId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            libroService.obtenerPorId(libroId);
        });

        assertTrue(exception.getMessage().contains("ID no encontrado"));
        verify(libroRepository, times(1)).findById(libroId);
    }

    @Test
    @DisplayName("Debería modificar la disponibilidad de un libro correctamente")
    void cambiarDisponibilidad_Exito() {
        // Arrange
        Long libroId = 1L;
        Boolean nuevoEstado = false;
        Libro libroSimulado = TestDataFactory.crearLibroAleatorio();
        libroSimulado.setId(libroId);
        libroSimulado.setDisponible(true);

        when(libroRepository.findById(libroId)).thenReturn(Optional.of(libroSimulado));

        // Act
        libroService.cambiarDisponibilidad(libroId, nuevoEstado);

        // Assert
        assertFalse(libroSimulado.getDisponible());
        verify(libroRepository, times(1)).findById(libroId);
        verify(libroRepository, times(1)).save(libroSimulado);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al cambiar disponibilidad de un ID inexistente")
    void cambiarDisponibilidad_NoExiste_LanzaResourceNotFoundException() {
        // Arrange
        Long libroId = 99L;
        when(libroRepository.findById(libroId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            libroService.cambiarDisponibilidad(libroId, true);
        });

        assertTrue(exception.getMessage().contains("ID no encontrado"));
        verify(libroRepository, times(1)).findById(libroId);
        verify(libroRepository, never()).save(any(Libro.class));
    }

    @Test
    @DisplayName("Debería eliminar un libro por su ID exitosamente")
    void eliminarLibro_Exito() {
        // Arrange
        Long libroId = 1L;
        Libro libroAEliminar = TestDataFactory.crearLibroAleatorio();
        libroAEliminar.setId(libroId);

        when(libroRepository.findById(libroId)).thenReturn(Optional.of(libroAEliminar));

        // Act
        libroService.eliminarLibro(libroId);

        // Assert
        verify(libroRepository, times(1)).findById(libroId);
        verify(libroRepository, times(1)).delete(libroAEliminar);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al intentar eliminar un ID inexistente")
    void eliminarLibro_NoExiste_LanzaResourceNotFoundException() {
        // Arrange
        Long libroId = 99L;
        when(libroRepository.findById(libroId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            libroService.eliminarLibro(libroId);
        });

        assertTrue(exception.getMessage().contains("ID inexistente"));
        verify(libroRepository, times(1)).findById(libroId);
        verify(libroRepository, never()).delete(any(Libro.class));
    }
}