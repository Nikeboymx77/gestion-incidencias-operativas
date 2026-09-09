package com.mx.baz.incidencias.reportes.controller;

import com.mx.baz.incidencias.repository.projection.ReporteDiaProjection;
import com.mx.baz.incidencias.repository.projection.ReporteEmpleadoProjection;
import com.mx.baz.incidencias.repository.projection.ReporteEstadoProjection;
import com.mx.baz.incidencias.reportes.service.ReporteExcelService;
import com.mx.baz.incidencias.reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mx.baz.incidencias.reportes.dto.ReporteTiemposResponse;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.mx.baz.incidencias.reportes.dto.ReporteCargaEmpleadoResponse;
import com.mx.baz.incidencias.reportes.dto.ReporteComparativoResponse;
import com.mx.baz.incidencias.reportes.dto.ReporteTendenciaComparativaResponse;
import com.mx.baz.incidencias.reportes.dto.ReporteDesempenoEmpleadoResponse;
import com.mx.baz.incidencias.reportes.dto.ReporteEvolucionTiemposResponse;
import com.mx.baz.incidencias.reportes.dto.ReporteIncidenciaAntiguaResponse;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final ReporteExcelService reporteExcelService;

    @GetMapping("/incidencias-por-dia")
    public ResponseEntity<List<ReporteDiaProjection>>
    obtenerIncidenciasPorDia(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        return ResponseEntity.ok(
                reporteService
                        .obtenerIncidenciasPorDia(
                                fechaDesde,
                                fechaHasta
                        )
        );
    }
    
    @GetMapping("/incidencias-por-estado")
    public ResponseEntity<List<ReporteEstadoProjection>>
    obtenerIncidenciasPorEstado(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        return ResponseEntity.ok(
                reporteService
                        .obtenerIncidenciasPorEstado(
                                fechaDesde,
                                fechaHasta
                        )
        );
    }
    
    @GetMapping("/incidencias-por-empleado")
    public ResponseEntity<List<ReporteEmpleadoProjection>>
    obtenerIncidenciasPorEmpleado(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        return ResponseEntity.ok(
                reporteService
                        .obtenerIncidenciasPorEmpleado(
                                fechaDesde,
                                fechaHasta
                        )
        );
    }
    
    @GetMapping("/tiempos-promedio")
    public ResponseEntity<ReporteTiemposResponse>
    obtenerTiemposPromedio(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        return ResponseEntity.ok(
                reporteService
                        .obtenerTiemposPromedio(
                                fechaDesde,
                                fechaHasta
                        )
        );
    }
    
    
    @GetMapping("/comparativo")
    public ResponseEntity<ReporteComparativoResponse>
    obtenerComparativo(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        return ResponseEntity.ok(
                reporteService
                        .obtenerComparativo(
                                fechaDesde,
                                fechaHasta
                        )
        );
    }
    
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        byte[] archivo =
                reporteExcelService
                        .generarReporte(
                                fechaDesde,
                                fechaHasta
                        );


        String nombreArchivo =
                "Reporte_SGIO_"
                + fechaDesde
                + "_"
                + fechaHasta
                + ".xlsx";


        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + nombreArchivo
                                + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(
                        archivo
                );
    }
    
    @GetMapping("/tendencia-comparativa")
    public ResponseEntity<ReporteTendenciaComparativaResponse>
    obtenerTendenciaComparativa(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        return ResponseEntity.ok(
                reporteService
                        .obtenerTendenciaComparativa(
                                fechaDesde,
                                fechaHasta
                        )
        );
    }
    
    @GetMapping("/desempeno-por-empleado")
    public ResponseEntity<List<ReporteDesempenoEmpleadoResponse>>
    obtenerDesempenoPorEmpleado(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        return ResponseEntity.ok(
                reporteService
                        .obtenerDesempenoPorEmpleado(
                                fechaDesde,
                                fechaHasta
                        )
        );
    }
    
    @GetMapping("/carga-operativa")
    public ResponseEntity<List<ReporteCargaEmpleadoResponse>>
    obtenerCargaOperativaPorEmpleado() {

        return ResponseEntity.ok(
                reporteService
                        .obtenerCargaOperativaPorEmpleado()
        );
    }
    
    @GetMapping("/evolucion-tiempos")
    public ResponseEntity<List<ReporteEvolucionTiemposResponse>>
    obtenerEvolucionTiempos(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        return ResponseEntity.ok(
                reporteService
                        .obtenerEvolucionTiempos(
                                fechaDesde,
                                fechaHasta
                        )
        );
    }
    
    @GetMapping("/incidencias-antiguas")
    public ResponseEntity<List<ReporteIncidenciaAntiguaResponse>>
    obtenerIncidenciasActivasMasAntiguas() {

        return ResponseEntity.ok(
                reporteService
                        .obtenerIncidenciasActivasMasAntiguas()
        );
    }
    
}
