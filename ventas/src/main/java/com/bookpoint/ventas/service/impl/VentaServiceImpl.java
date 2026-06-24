package com.bookpoint.ventas.service.impl;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.ventas.dto.LineaVentaRequestDTO;
import com.bookpoint.ventas.dto.LineaVentaResponseDTO;
import com.bookpoint.ventas.dto.VentaRequestDTO;
import com.bookpoint.ventas.dto.VentaResponseDTO;
import com.bookpoint.ventas.exception.ResourceNotFoundException;
import com.bookpoint.ventas.model.EstadoVenta;
import com.bookpoint.ventas.model.LineaVenta;
import com.bookpoint.ventas.model.Venta;
import com.bookpoint.ventas.repository.VentaRepository;
import com.bookpoint.ventas.service.VentaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;

    @Override
    public VentaResponseDTO registrarVenta(VentaRequestDTO requestDTO) {
        log.info("Procesando registro de venta para el usuario ID: {}", requestDTO.getUsuarioId());
        
        Venta venta = new Venta();
        venta.setUsuarioId(requestDTO.getUsuarioId());
        
        double totalAcumulado = 0.0;
        for (LineaVentaRequestDTO lineaDTO : requestDTO.getLineas()) {
            LineaVenta linea = new LineaVenta();
            linea.setLibroId(lineaDTO.getLibroId());
            linea.setCantidad(lineaDTO.getCantidad());
            linea.setPrecioUnitario(lineaDTO.getPrecioUnitario());
            linea.setVenta(venta);
            venta.getLineas().add(linea);
            
            totalAcumulado += lineaDTO.getCantidad() * lineaDTO.getPrecioUnitario();
        }
        venta.setTotal(totalAcumulado);
        
        return deEntidadADTO(ventaRepository.save(venta));
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerVentaPorId(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no localizada con ID: " + id));
        return deEntidadADTO(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponseDTO> obtenerTodasLasVentas(Pageable pageable) {
        return ventaRepository.findAll(pageable).map(this::deEntidadADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponseDTO> obtenerVentasPorUsuario(Long usuarioId, Pageable pageable) {
        return ventaRepository.findByUsuarioId(usuarioId, pageable).map(this::deEntidadADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponseDTO> obtenerVentasPorEstado(EstadoVenta estado, Pageable pageable) {
        return ventaRepository.findByEstado(estado, pageable).map(this::deEntidadADTO);
    }

    @Override
    public VentaResponseDTO actualizarEstadoVenta(Long id, EstadoVenta nuevoEstado) {
        log.info("Modificando estado de la venta ID: {} a {}", id, nuevoEstado);
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo actualizar, ID de venta no existe: " + id));
        venta.setEstado(nuevoEstado);
        return deEntidadADTO(ventaRepository.save(venta));
    }

    private VentaResponseDTO deEntidadADTO(Venta venta) {
        return VentaResponseDTO.builder()
                .id(venta.getId())
                .usuarioId(venta.getUsuarioId())
                .fechaVenta(venta.getFechaVenta())
                .estado(venta.getEstado())
                .total(venta.getTotal())
                .lineas(venta.getLineas().stream().map(linea -> LineaVentaResponseDTO.builder()
                        .id(linea.getId())
                        .libroId(linea.getLibroId())
                        .cantidad(linea.getCantidad())
                        .precioUnitario(linea.getPrecioUnitario())
                        .subtotal(linea.getCantidad() * linea.getPrecioUnitario())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}