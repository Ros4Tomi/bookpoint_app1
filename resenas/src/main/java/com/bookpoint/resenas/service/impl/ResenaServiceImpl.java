package com.bookpoint.resenas.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.resenas.client.FacturacionClient;
import com.bookpoint.resenas.client.UsuarioClient; // <-- NUEVO IMPORT
import com.bookpoint.resenas.dto.FacturaResponseDTO;
import com.bookpoint.resenas.dto.ResenaRequestDTO;
import com.bookpoint.resenas.dto.ResenaResponseDTO;
import com.bookpoint.resenas.dto.UsuarioResponseDTO; // <-- NUEVO IMPORT
import com.bookpoint.resenas.exception.ResourceNotFoundException;
import com.bookpoint.resenas.model.Resena;
import com.bookpoint.resenas.repository.ResenaRepository;
import com.bookpoint.resenas.service.ResenaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository resenaRepository;
    private final FacturacionClient facturacionClient;
    private final UsuarioClient usuarioClient; // <-- INYECTAMOS EL CLIENTE DE USUARIOS

    @Override
    public ResenaResponseDTO publicarResena(ResenaRequestDTO requestDTO) {
        log.info("Usuario ID: {} está intentando calificar el libro ID: {}", requestDTO.getUsuarioId(), requestDTO.getLibroId());

        // ==========================================
        // 1. VALIDACIÓN INTEGRAL DEL USUARIO (VÍA FEIGN)
        // ==========================================
        try {
            UsuarioResponseDTO usuario = usuarioClient.obtenerPorId(requestDTO.getUsuarioId());
            if (!usuario.getActivo()) {
                log.warn("Intento de reseña rechazado: El usuario {} está INACTIVO", requestDTO.getUsuarioId());
                throw new IllegalArgumentException("Validación de usuario fallida: La cuenta de usuario se encuentra suspendida o inactiva.");
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) throw e;
            log.error("Error al validar existencia del usuario en ms-usuarios", e);
            throw new ResourceNotFoundException("Validación de usuario fallida: El usuario con ID " + requestDTO.getUsuarioId() + " no existe en los registros.");
        }

        // ==========================================
        // 2. VERIFICACIÓN DE COMPRA VÍA FEIGN
        // ==========================================
        try {
            List<FacturaResponseDTO> historial = facturacionClient.obtenerHistorial(requestDTO.getUsuarioId());
            
            boolean comproElLibro = historial.stream()
                    .filter(factura -> !"ANULADA".equalsIgnoreCase(factura.getEstado())) 
                    .flatMap(factura -> factura.getDetalles().stream())
                    .anyMatch(detalle -> detalle.getLibroId().equals(requestDTO.getLibroId()));

            if (!comproElLibro) {
                log.warn("Intento de reseña rechazado: El usuario {} no ha comprado el libro {}", requestDTO.getUsuarioId(), requestDTO.getLibroId());
                throw new IllegalArgumentException("Compra verificada fallida: Solo los usuarios que han adquirido este libro pueden dejar una reseña.");
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) throw e;
            log.error("Error al verificar el historial de compras en ms-facturacion", e);
            throw new IllegalStateException("El servicio de verificación de compras no está disponible en este momento.");
        }

        // ==========================================
        // 3. REGLA DE NEGOCIO: Evitar duplicados
        // ==========================================
        if (resenaRepository.existsByLibroIdAndUsuarioId(requestDTO.getLibroId(), requestDTO.getUsuarioId())) {
            throw new IllegalArgumentException("Ya has emitido una valoración para este libro previamente.");
        }

        Resena resena = new Resena();
        resena.setLibroId(requestDTO.getLibroId());
        resena.setUsuarioId(requestDTO.getUsuarioId());
        resena.setCalificacion(requestDTO.getCalificacion());
        resena.setComentario(requestDTO.getComentario());

        Resena guardada = resenaRepository.save(resena);
        log.info("Reseña publicada con éxito. ID: {}", guardada.getId());
        
        return deEntidadADTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponseDTO> obtenerResenasPorLibro(Long libroId) {
        return resenaRepository.findByLibroId(libroId).stream()
                .map(this::deEntidadADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPromedioCalificacion(Long libroId) {
        List<Resena> resenas = resenaRepository.findByLibroId(libroId);
        if (resenas.isEmpty()) {
            return 0.0;
        }
        double suma = resenas.stream().mapToDouble(Resena::getCalificacion).sum();
        return Math.round((suma / resenas.size()) * 10.0) / 10.0; 
    }

    @Override
    public void eliminarResena(Long id) {
        if (!resenaRepository.existsById(id)) {
            throw new ResourceNotFoundException("La reseña a eliminar no existe.");
        }
        resenaRepository.deleteById(id);
    }

    private ResenaResponseDTO deEntidadADTO(Resena resena) {
        return ResenaResponseDTO.builder()
                .id(resena.getId())
                .libroId(resena.getLibroId())
                .usuarioId(resena.getUsuarioId())
                .calificacion(resena.getCalificacion())
                .comentario(resena.getComentario())
                .fechaCreacion(resena.getFechaCreacion())
                .build();
    }
}