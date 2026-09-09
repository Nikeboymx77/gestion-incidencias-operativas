package com.mx.baz.incidencias.reportes.service;

import com.mx.baz.incidencias.repository.IncidenciaRepository;
import com.mx.baz.incidencias.repository.projection.ReporteDiaProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mx.baz.incidencias.repository.projection.ReporteEstadoProjection;
import com.mx.baz.incidencias.repository.projection.ReporteEvolucionTiemposProjection;
import com.mx.baz.incidencias.repository.projection.ReporteIncidenciaAntiguaProjection;
import com.mx.baz.incidencias.repository.projection.ReporteEmpleadoProjection;
import com.mx.baz.incidencias.reportes.dto.ReporteTiemposResponse;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.reportes.dto.ReporteComparativoResponse;
import com.mx.baz.incidencias.reportes.dto.ReporteTendenciaComparativaResponse;
import com.mx.baz.incidencias.reportes.dto.ReporteDesempenoEmpleadoResponse;
import com.mx.baz.incidencias.repository.projection.ReporteCargaEmpleadoProjection;
import com.mx.baz.incidencias.repository.projection.ReporteDesempenoEmpleadoProjection;
import com.mx.baz.incidencias.reportes.dto.ReporteCargaEmpleadoResponse;
import com.mx.baz.incidencias.reportes.dto.ReporteEvolucionTiemposResponse;
import com.mx.baz.incidencias.reportes.dto.ReporteIncidenciaAntiguaResponse;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;

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
    
    @Transactional(readOnly = true)
    public ReporteComparativoResponse obtenerComparativo(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        long diasPeriodo =
                ChronoUnit.DAYS.between(
                        fechaDesde,
                        fechaHasta
                ) + 1;


        LocalDate fechaHastaAnterior =
                fechaDesde.minusDays(1);

        LocalDate fechaDesdeAnterior =
                fechaHastaAnterior
                        .minusDays(
                                diasPeriodo - 1
                        );


        ReporteComparativoResponse.Periodo periodoActual =
                construirPeriodo(
                        fechaDesde,
                        fechaHasta
                );


        ReporteComparativoResponse.Periodo periodoAnterior =
                construirPeriodo(
                        fechaDesdeAnterior,
                        fechaHastaAnterior
                );


        return ReporteComparativoResponse.builder()
                .periodoActual(periodoActual)
                .periodoAnterior(periodoAnterior)
                .build();
    }
    
    private ReporteComparativoResponse.Periodo construirPeriodo(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        List<ReporteEstadoProjection> datos =
                obtenerIncidenciasPorEstado(
                        fechaDesde,
                        fechaHasta
                );


        long total = 0;
        long pendientes = 0;
        long enProceso = 0;
        long resueltas = 0;
        long reabiertas = 0;
        long canceladas = 0;


        for (ReporteEstadoProjection dato : datos) {

            long cantidad =
                    dato.getTotal() != null
                            ? dato.getTotal()
                            : 0;


            total += cantidad;


            if (dato.getEstado() == null) {
                continue;
            }


            switch (dato.getEstado()) {

                case PENDIENTE ->
                        pendientes += cantidad;

                case EN_PROCESO ->
                        enProceso += cantidad;

                case RESUELTA ->
                        resueltas += cantidad;

                case REABIERTA ->
                        reabiertas += cantidad;

                case CANCELADA ->
                        canceladas += cantidad;

                default -> {
                    // Otros estados no afectan
                    // las métricas principales.
                }
            }
        }


        return ReporteComparativoResponse.Periodo.builder()
                .total(total)
                .pendientes(pendientes)
                .enProceso(enProceso)
                .resueltas(resueltas)
                .reabiertas(reabiertas)
                .canceladas(canceladas)
                .build();
    }
    @Transactional(readOnly = true)
    public ReporteTendenciaComparativaResponse
    obtenerTendenciaComparativa(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        long diasPeriodo =
                ChronoUnit.DAYS.between(
                        fechaDesde,
                        fechaHasta
                ) + 1;


        LocalDate fechaHastaAnterior =
                fechaDesde.minusDays(1);

        LocalDate fechaDesdeAnterior =
                fechaDesde.minusDays(
                        diasPeriodo
                );


        List<ReporteDiaProjection> datosActuales =
                obtenerIncidenciasPorDia(
                        fechaDesde,
                        fechaHasta
                );


        List<ReporteDiaProjection> datosAnteriores =
                obtenerIncidenciasPorDia(
                        fechaDesdeAnterior,
                        fechaHastaAnterior
                );


        List<ReporteTendenciaComparativaResponse.PuntoTendencia>
                periodoActual =
                construirTendencia(
                        fechaDesde,
                        fechaHasta,
                        datosActuales
                );


        List<ReporteTendenciaComparativaResponse.PuntoTendencia>
                periodoAnterior =
                construirTendencia(
                        fechaDesdeAnterior,
                        fechaHastaAnterior,
                        datosAnteriores
                );


        return ReporteTendenciaComparativaResponse
                .builder()
                .periodoActual(periodoActual)
                .periodoAnterior(periodoAnterior)
                .build();
    }
    private List<ReporteTendenciaComparativaResponse.PuntoTendencia>
    construirTendencia(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            List<ReporteDiaProjection> datos
    ) {

        Map<LocalDate, ReporteDiaProjection> datosPorFecha =
                datos.stream()
                        .collect(
                                Collectors.toMap(
                                        ReporteDiaProjection::getFecha,
                                        Function.identity()
                                )
                        );


        List<ReporteTendenciaComparativaResponse.PuntoTendencia>
                tendencia =
                new ArrayList<>();


        LocalDate fechaActual =
                fechaDesde;

        int dia = 1;


        while (!fechaActual.isAfter(fechaHasta)) {

            ReporteDiaProjection dato =
                    datosPorFecha.get(
                            fechaActual
                    );


            long total =
                    dato != null
                            && dato.getTotal() != null
                            ? dato.getTotal()
                            : 0L;


            tendencia.add(
                    ReporteTendenciaComparativaResponse
                            .PuntoTendencia
                            .builder()
                            .dia(dia)
                            .fecha(fechaActual)
                            .total(total)
                            .build()
            );


            fechaActual =
                    fechaActual.plusDays(1);

            dia++;
        }


        return tendencia;
    }
    
    @Transactional(readOnly = true)
    public List<ReporteDesempenoEmpleadoResponse>
    obtenerDesempenoPorEmpleado(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        LocalDateTime desde =
                fechaDesde.atStartOfDay();

        LocalDateTime hasta =
                fechaHasta
                        .plusDays(1)
                        .atStartOfDay();


        List<ReporteDesempenoEmpleadoProjection> datos =
                incidenciaRepository
                        .obtenerDesempenoPorEmpleado(
                                desde,
                                hasta
                        );


        List<ReporteDesempenoEmpleadoResponse> resultado =
                new ArrayList<>();


        for (ReporteDesempenoEmpleadoProjection dato : datos) {

            long total =
                    dato.getTotalIncidencias() != null
                            ? dato.getTotalIncidencias()
                            : 0L;

            long resueltas =
                    dato.getResueltas() != null
                            ? dato.getResueltas()
                            : 0L;


            double porcentajeResolucion =
                    total > 0
                            ? ((double) resueltas / total) * 100
                            : 0.0;


            resultado.add(
                    ReporteDesempenoEmpleadoResponse
                            .builder()
                            .empleadoId(
                                    dato.getEmpleadoId()
                            )
                            .nombre(
                                    dato.getNombre()
                            )
                            .totalIncidencias(
                                    total
                            )
                            .resueltas(
                                    resueltas
                            )
                            .porcentajeResolucion(
                                    porcentajeResolucion
                            )
                            .tiempoPromedioAtencionMinutos(
                                    dato.getTiempoPromedioAtencionMinutos()
                            )
                            .tiempoPromedioResolucionMinutos(
                                    dato.getTiempoPromedioResolucionMinutos()
                            )
                            .build()
            );
        }
        
        resultado.sort(
                Comparator
                        .comparing(
                                ReporteDesempenoEmpleadoResponse::getPorcentajeResolucion,
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                ReporteDesempenoEmpleadoResponse::getResueltas,
                                Comparator.reverseOrder()
                        )
        );


        return resultado;
    }
    
    @Transactional(readOnly = true)
    public List<ReporteCargaEmpleadoResponse>
    obtenerCargaOperativaPorEmpleado() {

        List<ReporteCargaEmpleadoProjection> datos =
                incidenciaRepository
                        .obtenerCargaOperativaPorEmpleado();


        List<ReporteCargaEmpleadoResponse> resultado =
                new ArrayList<>();


        for (ReporteCargaEmpleadoProjection dato : datos) {

            long pendientes =
                    dato.getPendientes() != null
                            ? dato.getPendientes()
                            : 0L;

            long enProceso =
                    dato.getEnProceso() != null
                            ? dato.getEnProceso()
                            : 0L;

            long reabiertas =
                    dato.getReabiertas() != null
                            ? dato.getReabiertas()
                            : 0L;

            long totalActivas =
                    dato.getTotalActivas() != null
                            ? dato.getTotalActivas()
                            : 0L;


            resultado.add(
                    ReporteCargaEmpleadoResponse
                            .builder()
                            .empleadoId(
                                    dato.getEmpleadoId()
                            )
                            .nombre(
                                    dato.getNombre()
                            )
                            .totalActivas(
                                    totalActivas
                            )
                            .pendientes(
                                    pendientes
                            )
                            .enProceso(
                                    enProceso
                            )
                            .reabiertas(
                                    reabiertas
                            )
                            .build()
            );
        }


        return resultado;
    }
    
    @Transactional(readOnly = true)
    public List<ReporteEvolucionTiemposResponse>
    obtenerEvolucionTiempos(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        LocalDateTime desde =
                fechaDesde.atStartOfDay();

        LocalDateTime hasta =
                fechaHasta
                        .plusDays(1)
                        .atStartOfDay();


        List<ReporteEvolucionTiemposProjection> datos =
                incidenciaRepository
                        .obtenerEvolucionTiempos(
                                desde,
                                hasta
                        );


        List<ReporteEvolucionTiemposResponse> resultado =
                new ArrayList<>();


        for (ReporteEvolucionTiemposProjection dato : datos) {

            resultado.add(
                    ReporteEvolucionTiemposResponse
                            .builder()
                            .fecha(
                                    dato.getFecha()
                            )
                            .tiempoPromedioAtencionMinutos(
                                    dato.getTiempoPromedioAtencionMinutos()
                            )
                            .tiempoPromedioResolucionMinutos(
                                    dato.getTiempoPromedioResolucionMinutos()
                            )
                            .build()
            );
        }


        return resultado;
    }
    
    @Transactional(readOnly = true)
    public List<ReporteIncidenciaAntiguaResponse>
    obtenerIncidenciasActivasMasAntiguas() {

        List<ReporteIncidenciaAntiguaProjection> datos =
                incidenciaRepository
                        .obtenerIncidenciasActivasMasAntiguas();

        List<ReporteIncidenciaAntiguaResponse> resultado =
                new ArrayList<>();

        for (ReporteIncidenciaAntiguaProjection dato : datos) {

            resultado.add(
                    ReporteIncidenciaAntiguaResponse
                            .builder()
                            .incidenciaId(
                                    dato.getIncidenciaId()
                            )
                            .folio(
                                    dato.getFolio()
                            )
                            .asunto(
                                    dato.getAsunto()
                            )
                            .estado(
                                    dato.getEstado()
                            )
                            .prioridad(
                                    dato.getPrioridad()
                            )
                            .empleadoId(
                                    dato.getEmpleadoId()
                            )
                            .empleado(
                                    dato.getEmpleado()
                            )
                            .fechaCreacion(
                                    dato.getFechaCreacion()
                            )
                            .antiguedadMinutos(
                                    dato.getAntiguedadMinutos()
                            )
                            .build()
            );
        }

        return resultado;
    }
}
