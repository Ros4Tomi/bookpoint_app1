package com.bookpoint.carrito.service.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.carrito.client.CatalogoClient;
import com.bookpoint.carrito.client.UsuarioClient; // <-- NUEVO IMPORT
import com.bookpoint.carrito.dto.CarritoResponseDTO;
import com.bookpoint.carrito.dto.ItemRequestDTO;
import com.bookpoint.carrito.dto.ItemResponseDTO;
import com.bookpoint.carrito.dto.LibroResponseDTO;
import com.bookpoint.carrito.dto.UsuarioResponseDTO; // <-- NUEVO IMPORT
import com.bookpoint.carrito.exception.ResourceNotFoundException;
import com.bookpoint.carrito.model.Carrito;
import com.bookpoint.carrito.model.ItemCarrito;
import com.bookpoint.carrito.repository.CarritoRepository;
import com.bookpoint.carrito.service.CarritoService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final CatalogoClient catalogoClient;
    private final UsuarioClient usuarioClient; // <-- NUEVA INYECCIÓN QUIRÚRGICA

    // Método privado auxiliar para validar el estado del usuario mediante Feign
    private void validarUsuarioActivo(Long usuarioId) {
        try {
            UsuarioResponseDTO usuario = usuarioClient.obtenerPorId(usuarioId);
            if (!usuario.getActivo()) {
                log.warn("Operación denegada en Carrito: El usuario con ID {} está INACTIVO", usuarioId);
                throw new IllegalArgumentException("Validación fallida: La cuenta de usuario se encuentra suspendida o inactiva.");
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) throw e;
            log.error("Error de comunicación o usuario inexistente en ms-usuarios para ID: {}", usuarioId, e);
            throw new ResourceNotFoundException("Validación fallida: El usuario con ID " + usuarioId + " no está registrado en el sistema.");
        }
    }

    @Override
    public CarritoResponseDTO obtenerOCrearCarrito(Long usuarioId) {
        validarUsuarioActivo(usuarioId); // <-- VALIDACIÓN ANTES DE OBTENER O CREAR
        return deEntidadADTO(buscarOInstanciar(usuarioId));
    }

    @Override
    public CarritoResponseDTO agregarItem(Long usuarioId, ItemRequestDTO itemDTO) {
        log.info("Agregando libro ID: {} al carrito del usuario: {}", itemDTO.getLibroId(), usuarioId);
        
        validarUsuarioActivo(usuarioId); // <-- VALIDACIÓN ANTES DE AGREGAR EL ÍTEM

        LibroResponseDTO libroExt;
        try {
            libroExt = catalogoClient.obtenerLibroPorId(itemDTO.getLibroId());
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Error: El libro con ID " + itemDTO.getLibroId() + " no existe en el catálogo.");
        } catch (FeignException e) {
            throw new RuntimeException("El microservicio de Catálogo no responde en este momento.");
        }

        if (libroExt.getStock() < itemDTO.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente. Solo quedan " + libroExt.getStock() + " unidades.");
        }

        Carrito carrito = buscarOInstanciar(usuarioId);

        Optional<ItemCarrito> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getLibroId().equals(itemDTO.getLibroId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            int nuevaCantidad = itemExistente.get().getCantidad() + itemDTO.getCantidad();
            if (libroExt.getStock() < nuevaCantidad) {
                throw new IllegalArgumentException("No puedes agregar más unidades. Supera el stock disponible (" + libroExt.getStock() + ").");
            }
            itemExistente.get().setCantidad(nuevaCantidad);
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

        try {
            LibroResponseDTO libroExt = catalogoClient.obtenerLibroPorId(libroId);
            if (libroExt.getStock() < cantidad) {
                throw new IllegalArgumentException("No puedes actualizar a esa cantidad. Stock disponible: " + libroExt.getStock());
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("El libro ya no existe en el catálogo.");
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
                .items(carrito.getItems().stream().map(item -> {
                    
                    String tituloFallback = "Libro No Disponible";
                    Double precioFallback = 0.0;
                    
                    try {
                        LibroResponseDTO libro = catalogoClient.obtenerLibroPorId(item.getLibroId());
                        tituloFallback = libro.getTitulo();
                        precioFallback = libro.getPrecio();
                    } catch (Exception e) {
                        log.warn("No se pudo recuperar información para el libro ID: {}", item.getLibroId());
                    }

                    return ItemResponseDTO.builder()
                            .id(item.getId())
                            .libroId(item.getLibroId())
                            .cantidad(item.getCantidad())
                            .tituloLibro(tituloFallback)   
                            .precioUnitario(precioFallback) 
                            .subtotal(precioFallback * item.getCantidad()) 
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }
}