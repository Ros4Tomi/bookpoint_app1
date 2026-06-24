package com.bookpoint.promociones.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookpoint.promociones.dto.PromocionRequestDTO;
import com.bookpoint.promociones.dto.PromocionResponseDTO;
import com.bookpoint.promociones.exception.ResourceNotFoundException;
import com.bookpoint.promociones.model.Promocion;
import com.bookpoint.promociones.repository.PromocionRepository;
import com.bookpoint.promociones.service.PromocionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromocionServiceImpl implements PromocionService {

    private final PromocionRepository promocionRepository;

    @Override
    public PromocionResponseDTO crearPromocion(PromocionRequestDTO requestDTO) {
        log.info("Creando nueva campaña promocional: {}", requestDTO.getCodigo());

        if (promocionRepository.existsByCodigo(requestDTO.getCodigo().toUpperCase())) {
            throw new IllegalArgumentException("El código promocional ya existe en el sistema.");
        }

        if (requestDTO.getFechaFin().isBefore(requestDTO.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la de inicio.");
        }

        Promocion promocion = new Promocion();
        promocion.setCodigo(requestDTO.getCodigo().toUpperCase());
        promocion.setDescripcion(requestDTO.getDescripcion());
        promocion.setPorcentajeDescuento(requestDTO.getPorcentajeDescuento());
        promocion.setFechaInicio(requestDTO.getFechaInicio());
        promocion.setFechaFin(requestDTO.getFechaFin());
        promocion.setActivo(true);

        return deEntidadADTO(promocionRepository.save(promocion));
    }

    @Override
    @Transactional(readOnly = true)
    public PromocionResponseDTO validarYObtenerCupón(String codigo) {
        log.info("Validando vigencia del cupón: {}", codigo);
        
        Promocion promocion = promocionRepository.findByCodigoAndActivoTrue(codigo.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("El cupón ingresado no existe o se encuentra inactivo."));

        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isBefore(promocion.getFechaInicio()) || ahora.isAfter(promocion.getFechaFin())) {
            throw new IllegalArgumentException("El cupón de descuento ha expirado o no ha comenzado su vigencia.");
        }

        return deEntidadADTO(promocion);
    }

    @Override
    public PromocionResponseDTO desactivarPromocion(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la promoción seleccionada"));
        promocion.setActivo(false);
        return deEntidadADTO(promocionRepository.save(promocion));
    }

    private PromocionResponseDTO deEntidadADTO(Promocion promocion) {
        return PromocionResponseDTO.builder()
                .id(promocion.getId())
                .codigo(promocion.getCodigo())
                .descripcion(promocion.getDescripcion())
                .porcentajeDescuento(promocion.getPorcentajeDescuento())
                .fechaInicio(promocion.getFechaInicio())
                .fechaFin(promocion.getFechaFin())
                .activo(promocion.getActivo())
                .build();
    }
}