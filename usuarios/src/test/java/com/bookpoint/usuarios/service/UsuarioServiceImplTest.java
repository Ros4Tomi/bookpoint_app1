package com.bookpoint.usuarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookpoint.usuarios.dto.UsuarioRequestDTO;
import com.bookpoint.usuarios.dto.UsuarioResponseDTO;
import com.bookpoint.usuarios.factory.TestDataFactory;
import com.bookpoint.usuarios.model.Usuario;
import com.bookpoint.usuarios.repository.UsuarioRepository;
import com.bookpoint.usuarios.service.impl.UsuarioServiceImpl;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    @DisplayName("Debería crear un usuario exitosamente cuando email y username son nuevos")
    void crearUsuario_Exito() {
        // Arrange
        UsuarioRequestDTO requestDTO = TestDataFactory.crearUsuarioRequestDTOAleatorio();
        Usuario usuarioGuardado = TestDataFactory.crearUsuarioAleatorio();
        
        // Sincronizamos los datos
        usuarioGuardado.setUsername(requestDTO.getUsername());
        usuarioGuardado.setEmail(requestDTO.getEmail());
        usuarioGuardado.setNombreCompleto(requestDTO.getNombreCompleto());

        // Simulamos que ni el username ni el email existen en la base de datos
        when(usuarioRepository.existsByUsername(requestDTO.getUsername())).thenReturn(false);
        when(usuarioRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        // Act
        UsuarioResponseDTO response = usuarioService.crearUsuario(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(requestDTO.getUsername(), response.getUsername());
        assertEquals(requestDTO.getEmail(), response.getEmail());
        
        // Verificamos que se llamó a las validaciones y luego al save
        verify(usuarioRepository, times(1)).existsByUsername(requestDTO.getUsername());
        verify(usuarioRepository, times(1)).existsByEmail(requestDTO.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el username ya está registrado")
    void crearUsuario_UsernameDuplicado_LanzaExcepcion() {
        // Arrange
        UsuarioRequestDTO requestDTO = TestDataFactory.crearUsuarioRequestDTOAleatorio();
        
        // Simulamos que la base de datos detecta que el username SÍ existe
        when(usuarioRepository.existsByUsername(requestDTO.getUsername())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario(requestDTO);
        });

        // Verificamos que el flujo se corta en el username y nunca llega a consultar el email ni a guardar
        verify(usuarioRepository, times(1)).existsByUsername(requestDTO.getUsername());
        verify(usuarioRepository, never()).existsByEmail(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el email ya está registrado")
    void crearUsuario_EmailDuplicado_LanzaExcepcion() {
        // Arrange
        UsuarioRequestDTO requestDTO = TestDataFactory.crearUsuarioRequestDTOAleatorio();
        
        // Simulamos que el username NO existe (pasa el primer filtro)
        when(usuarioRepository.existsByUsername(requestDTO.getUsername())).thenReturn(false);
        // Simulamos que el email SÍ existe (cae en el segundo filtro)
        when(usuarioRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario(requestDTO);
        });

        // Tu propio código lanza: "El correo electrónico '[email]' ya está en uso"
        assertTrue(exception.getMessage().contains("ya está en uso"));
        
        verify(usuarioRepository, times(1)).existsByUsername(requestDTO.getUsername());
        verify(usuarioRepository, times(1)).existsByEmail(requestDTO.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}