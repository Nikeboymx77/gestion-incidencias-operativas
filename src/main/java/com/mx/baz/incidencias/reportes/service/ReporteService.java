package com.mx.baz.incidencias.reportes.service;

import com.mx.baz.incidencias.repository.IncidenciaRepository;
import com.mx.baz.incidencias.repository.projection.ReporteDiaProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mx.baz.incidencias.repository.projection.ReporteEstadoProjection;
import com.mx.baz.incidencias.repository.projection.ReporteEmpleadoProjection;
import com.mx.baz.incidencias.reportes.dto.ReporteTiemposResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final IncidenciaRepository incidenciaRepository;

    @Transactional(readOnly = true)
    public List<ReporteDiaProjection> obtenerIncidenciasPorDia(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        LocalDateTime inicio =
                fechaDesde.atStartOfDay();

        LocalDateTime finExclusivo =
                fechaHasta
                        .plusDays(1)
                        .atStartOfDay();

        return incidenciaRepository
                .obtenerIncidenciasPorDia(
                        inicio,
                        finExclusivo
                );
    }
    
    @Transactional(readOnly = true)
    public List<ReporteEstadoProjection> obtenerIncidenciasPorEstado(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        LocalDateTime inicio =
                fechaDesde.atStartOfDay();

        LocalDateTime finExclusivo =
                fechaHasta
                        .plusDays(1)
                        .atStartOfDay();

        return incidenciaRepository
                .obtenerIncidenciasPorEstado(
                        inicio,
                        finExclusivo
                );
    }


    @Transactional(readOnly = true)
    public List<ReporteEmpleadoProjection> obtenerIncidenciasPorEmpleado(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        LocalDateTime inicio =
                fechaDesde.atStartOfDay();

        LocalDateTime finExclusivo =
                fechaHasta
                        .plusDays(1)
                        .atStartOfDay();

        return incidenciaRepository
                .obtenerIncidenciasPorEmpleado(
                        inicio,
                        finExclusivo
                );
    }
    
    @Transactional(readOnly = true)
    public ReporteTiemposResponse obtenerTiemposPromedio(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        LocalDateTime inicio =
                fechaDesde.atStartOfDay();

        LocalDateTime finExclusivo =
                fechaHasta
                        .plusDays(1)
                        .atStartOfDay();


        Double promedioAtencion =
                incidenciaRepository
                        .obtenerTiempoPromedioAtencionMinutos(
                                inicio,
                                finExclusivo
                        );


        Double promedioResolucion =
                incidenciaRepository
                        .obtenerTiempoPromedioResolucionMinutos(
                                inicio,
                                finExclusivo
                        );


        return ReporteTiemposResponse.builder()
                .tiempoPromedioAtencionMinutos(
                        promedioAtencion != null
                                ? promedioAtencion
                                : 0.0
                )
                .tiempoPromedioResolucionMinutos(
                        promedioResolucion != null
                                ? promedioResolucion
                                : 0.0
                )
                .build();
    }
}
