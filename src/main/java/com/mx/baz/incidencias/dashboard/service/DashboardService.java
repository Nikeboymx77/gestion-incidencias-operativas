package com.mx.baz.incidencias.dashboard.service;

import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.mapper.IncidenciaMapper;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mx.baz.incidencias.exception.BusinessException;
import com.mx.baz.incidencias.exception.ErrorCodes;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncidenciaRepository incidenciaRepository;
    private final IncidenciaMapper incidenciaMapper;

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
    public List<IncidenciaResponse> obtenerIncidenciasRecientes() {
        return incidenciaRepository.findTop20ByOrderByCreatedAtDesc()
                .stream()
                .map(incidenciaMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<IncidenciaResponse> buscarIncidencias(
            String texto,
            EstadoIncidencia estado) {

        String textoNormalizado =
                texto == null || texto.isBlank()
                        ? null
                        : texto.trim();

        return incidenciaRepository
                .buscarParaDashboard(textoNormalizado, estado)
                .stream()
                .map(incidenciaMapper::toResponse)
                .toList();
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
    
}