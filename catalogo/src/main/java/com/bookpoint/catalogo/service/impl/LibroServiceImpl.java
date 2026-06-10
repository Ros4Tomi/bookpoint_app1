package com.bookpoint.catalogo.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.catalogo.dto.LibroRequestDTO;
import com.bookpoint.catalogo.dto.LibroResponseDTO;
import com.bookpoint.catalogo.exception.ResourceNotFoundException;
import com.bookpoint.catalogo.model.Categoria;
import com.bookpoint.catalogo.model.Libro;
import com.bookpoint.catalogo.repository.LibroRepository;
import com.bookpoint.catalogo.service.LibroService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;

    @Override
    public LibroResponseDTO guardarLibro(LibroRequestDTO requestDTO) {
        log.info("Registrando libro: {}", requestDTO.getIsbn());
        if (libroRepository.existsByIsbn(requestDTO.getIsbn())) {
            throw new IllegalArgumentException("El ISBN ya existe: " + requestDTO.getIsbn());
        }
        Libro libro = deDTOAEntidad(requestDTO);
        return deEntidadADTO(libroRepository.save(libro));
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResponseDTO obtenerPorId(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID no encontrado: " + id));
        return deEntidadADTO(libro);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResponseDTO obtenerPorIsbn(String isbn) {
        Libro libro = libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("ISBN no encontrado: " + isbn));
        return deEntidadADTO(libro);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResponseDTO> obtenerTodos(Pageable pageable) {
        return libroRepository.findAll(pageable).map(this::deEntidadADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResponseDTO> obtenerDisponibles(Pageable pageable) {
        return libroRepository.findByDisponibleTrue(pageable).map(this::deEntidadADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResponseDTO> obtenerPorCategoria(Categoria categoria, Pageable pageable) {
        return libroRepository.findByCategoria(categoria, pageable).map(this::deEntidadADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResponseDTO> buscarPorTitulo(String titulo, Pageable pageable) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo, pageable).map(this::deEntidadADTO);
    }

    @Override
    public LibroResponseDTO actualizarLibro(Long id, LibroRequestDTO requestDTO) {
        log.info("Actualizando libro: {}", id);
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID no existe: " + id));

        if (!libro.getIsbn().equals(requestDTO.getIsbn()) && libroRepository.existsByIsbn(requestDTO.getIsbn())) {
            throw new IllegalArgumentException("El nuevo ISBN ya pertenece a otro libro.");
        }

        libro.setIsbn(requestDTO.getIsbn());
        libro.setTitulo(requestDTO.getTitulo());
        libro.setAutor(requestDTO.getAutor());
        libro.setPrecio(requestDTO.getPrecio());
        libro.setCategoria(requestDTO.getCategoria());
        libro.setDescripcion(requestDTO.getDescripcion());
        if (requestDTO.getDisponible() != null) libro.setDisponible(requestDTO.getDisponible());

        return deEntidadADTO(libroRepository.save(libro));
    }

    @Override
    public void eliminarLibro(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID inexistente: " + id));
        libroRepository.delete(libro);
    }

    @Override
    public void cambiarDisponibilidad(Long id, Boolean estado) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID no encontrado: " + id));
        libro.setDisponible(estado);
        libroRepository.save(libro);
    }

    private Libro deDTOAEntidad(LibroRequestDTO dto) {
        Libro libro = new Libro();
        libro.setIsbn(dto.getIsbn());
        libro.setTitulo(dto.getTitulo());
        libro.setAutor(dto.getAutor());
        libro.setPrecio(dto.getPrecio());
        libro.setCategoria(dto.getCategoria());
        libro.setDescripcion(dto.getDescripcion());
        libro.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        return libro;
    }

    private LibroResponseDTO deEntidadADTO(Libro libro) {
        return LibroResponseDTO.builder()
                .id(libro.getId())
                .isbn(libro.getIsbn())
                .titulo(libro.getTitulo())
                .autor(libro.getAutor())
                .precio(libro.getPrecio())
                .categoria(libro.getCategoria())
                .descripcion(libro.getDescripcion())
                .disponible(libro.getDisponible())
                .fechaCreacion(libro.getFechaCreacion())
                .build();
    }
}