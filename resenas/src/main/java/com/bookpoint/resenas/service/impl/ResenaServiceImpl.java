package com.bookpoint.resenas.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.resenas.dto.ResenaRequestDTO;
import com.bookpoint.resenas.dto.ResenaResponseDTO;
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

    @Override
    public ResenaResponseDTO publicarResena(ResenaRequestDTO requestDTO) {
        log.info("Usuario ID: {} está calificando el libro ID: {}", requestDTO.getUsuarioId(), requestDTO.getLibroId());

        // Regla de Negocio: Evitar duplicados (un usuario solo opina una vez por libro)
        if (resenaRepository.existsByLibroIdAndUsuarioId(requestDTO.getLibroId(), requestDTO.getUsuarioId())) {
            throw new IllegalArgumentException("Ya has emitido una valoración para este libro previamente.");
        }

        Resena resena = new Resena();
        resena.setLibroId(requestDTO.getLibroId());
        resena.setUsuarioId(requestDTO.getUsuarioId());
        resena.setCalificacion(requestDTO.getCalificacion());
        resena.setComentario(requestDTO.getComentario());

        return deEntidadADTO(resenaRepository.save(resena));
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
        return Math.round((suma / resenas.size()) * 10.0) / 10.0; // Redondeado a 1 decimal
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