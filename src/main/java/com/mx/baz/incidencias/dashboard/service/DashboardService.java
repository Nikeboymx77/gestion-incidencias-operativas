package com.mx.baz.incidencias.dashboard.service;

import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.mapper.IncidenciaMapper;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mx.baz.incidencias.exception.BusinessException;
import com.mx.baz.incidencias.exception.ErrorCodes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mx.baz.incidencias.repository.projection.DashboardMetricasProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.mx.baz.incidencias.enums.PrioridadIncidencia;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncidenciaRepository incidenciaRepository;
    private final IncidenciaMapper incidenciaMapper;
    private final EmpleadoRepository empleadoRepository;

    @Transactional(readOnly = true)
    public long contarTotal() {
        return incidenciaRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarPendientes() {
        return incidenciaRepository.countByEstado(EstadoIncidencia.PENDIENTE);
    }

    @Transactional(readOnly = true)
    public long contarEnProceso() {
        return incidenciaRepository.countByEstado(EstadoIncidencia.EN_PROCESO);
    }

    @Transactional(readOnly = true)
    public long contarResueltas() {
        return incidenciaRepository.countByEstado(EstadoIncidencia.RESUELTA);
    }
    
    @Transactional(readOnly = true)
    public long contarReabiertas() {
        return incidenciaRepository.countByEstado(EstadoIncidencia.REABIERTA);
    }

    @Transactional(readOnly = true)
    public List<IncidenciaResponse> obtenerIncidenciasRecientes() {
        return incidenciaRepository.findTop20ByOrderByCreatedAtDesc()
                .stream()
                .map(incidenciaMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Page<Incidencia> buscarIncidencias(
            String texto,
            EstadoIncidencia estado,
            Long empleadoId,
            PrioridadIncidencia prioridad,
            String carpetaOrigen,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            int page,
            int size
    ) {

        String textoNormalizado =
                texto == null || texto.isBlank()
                        ? null
                        : texto.trim();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
          
        );
        
        LocalDateTime fechaDesdeInicio =
                fechaDesde != null
                        ? fechaDesde.atStartOfDay()
                        : null;


        LocalDateTime fechaHastaExclusiva =
                fechaHasta != null
                        ? fechaHasta
                            .plusDays(1)
                            .atStartOfDay()
                        : null;
        
        String origenNormalizado =
                carpetaOrigen == null
                || carpetaOrigen.isBlank()
                        ? null
                        : carpetaOrigen.trim();

        return incidenciaRepository
                .buscarParaDashboard(
                        textoNormalizado,
                        estado,
                        empleadoId,
                        prioridad,
                        origenNormalizado,
                        fechaDesdeInicio,
                        fechaHastaExclusiva,
                        pageable
                );
    }
    
    @Transactional(readOnly = true)
    public IncidenciaResponse obtenerDetalle(String folio) {

        return incidenciaRepository.findByFolio(folio)
                .map(incidenciaMapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.INCIDENCIA_NO_ENCONTRADA,
                        "Incidencia no encontrada: " + folio
                ));
    }
    
    @Transactional(readOnly = true)
    public List<Empleado> obtenerEmpleadosActivos() {
        return empleadoRepository.findByActivoTrueOrderByNombreAsc();
    }
    
    @Transactional(readOnly = true)
    public long contarCanceladas() {
        return incidenciaRepository.countByEstado(
                EstadoIncidencia.CANCELADA
        );
    }
    
    @Transactional(readOnly = true)
    public DashboardMetricasProjection obtenerMetricas(
            String texto,
            EstadoIncidencia estado,
            Long empleadoId,
            PrioridadIncidencia prioridad,
            String carpetaOrigen,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        String textoNormalizado =
                texto == null || texto.isBlank()
                        ? null
                        : texto.trim();


        LocalDateTime fechaDesdeInicio =
                fechaDesde != null
                        ? fechaDesde.atStartOfDay()
                        : null;


        LocalDateTime fechaHastaExclusiva =
                fechaHasta != null
                        ? fechaHasta
                            .plusDays(1)
                            .atStartOfDay()
                        : null;
        
        String origenNormalizado =
                carpetaOrigen == null
                || carpetaOrigen.isBlank()
                        ? null
                        : carpetaOrigen.trim();


        return incidenciaRepository
                .obtenerMetricasDashboard(
                        textoNormalizado,
                        estado,
                        empleadoId,
                        prioridad,
                        origenNormalizado,
                        fechaDesdeInicio,
                        fechaHastaExclusiva
                );
    }
    
    @Transactional(readOnly = true)
    public List<String> obtenerOrigenesDisponibles() {

        return incidenciaRepository
                .obtenerOrigenesDisponibles();
    }
    
}