package com.bookpoint.carrito.service.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.carrito.dto.CarritoResponseDTO;
import com.bookpoint.carrito.dto.ItemRequestDTO;
import com.bookpoint.carrito.dto.ItemResponseDTO;
import com.bookpoint.carrito.exception.ResourceNotFoundException;
import com.bookpoint.carrito.model.Carrito;
import com.bookpoint.carrito.model.ItemCarrito;
import com.bookpoint.carrito.repository.CarritoRepository;
import com.bookpoint.carrito.service.CarritoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;

    @Override
    public CarritoResponseDTO obtenerOCrearCarrito(Long usuarioId) {
        return deEntidadADTO(buscarOInstanciar(usuarioId));
    }

    @Override
    public CarritoResponseDTO agregarItem(Long usuarioId, ItemRequestDTO itemDTO) {
        log.info("Agregando libro ID: {} al carrito del usuario: {}", itemDTO.getLibroId(), usuarioId);
        Carrito carrito = buscarOInstanciar(usuarioId);

        Optional<ItemCarrito> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getLibroId().equals(itemDTO.getLibroId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            itemExistente.get().setCantidad(itemExistente.get().getCantidad() + itemDTO.getCantidad());
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setLibroId(itemDTO.getLibroId());
            nuevoItem.setCantidad(itemDTO.getCantidad());
            nuevoItem.setCarrito(carrito);
            carrito.getItems().add(nuevoItem);
        }

        return deEntidadADTO(carritoRepository.save(carrito));
    }

    @Override
    public CarritoResponseDTO actualizarCantidadItem(Long usuarioId, Long libroId, Integer cantidad) {
        if (cantidad <= 0) {
            return removerItem(usuarioId, libroId);
        }

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe carrito activo para el usuario: " + usuarioId));

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getLibroId().equals(libroId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("El libro especificado no se encuentra en el carrito"));

        item.setCantidad(cantidad);
        return deEntidadADTO(carritoRepository.save(carrito));
    }

    @Override
    public CarritoResponseDTO removerItem(Long usuarioId, Long libroId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el carrito solicitado"));

        boolean removido = carrito.getItems().removeIf(item -> item.getLibroId().equals(libroId));
        
        if (!removido) {
            throw new ResourceNotFoundException("El item no existía en el carrito");
        }

        return deEntidadADTO(carritoRepository.save(carrito));
    }

    @Override
    public void limpiarCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo vaciar, carrito no encontrado"));
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    private Carrito buscarOInstanciar(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuarioId(usuarioId);
                    return carritoRepository.save(nuevo);
                });
    }

    private CarritoResponseDTO deEntidadADTO(Carrito carrito) {
        return CarritoResponseDTO.builder()
                .id(carrito.getId())
                .usuarioId(carrito.getUsuarioId())
                .fechaActualizacion(carrito.getFechaActualizacion())
                .items(carrito.getItems().stream().map(item -> ItemResponseDTO.builder()
                        .id(item.getId())
                        .libroId(item.getLibroId())
                        .cantidad(item.getCantidad())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}