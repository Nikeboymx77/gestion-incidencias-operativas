package com.mx.baz.incidencias.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.baz.incidencias.service.ReporteProcesosService;

@RestController
@RequestMapping("/api/reportes/procesos")
public class ReporteProcesosController {

    private final ReporteProcesosService reporteProcesosService;

    public ReporteProcesosController(
            ReporteProcesosService reporteProcesosService) {

        this.reporteProcesosService = reporteProcesosService;
    }

    @GetMapping("/top10")
    public ResponseEntity<Map<String, Object>> obtenerTop10(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta) {

        if (desde.isAfter(hasta)) {
            return ResponseEntity.badRequest().body(
                    Map.of("error",
                            "La fecha desde no puede ser posterior a hasta")
            );
        }

        Map<String, Object> reporte =
                reporteProcesosService.generarReporte(
                        desde.atStartOfDay(),
                        hasta.plusDays(1).atStartOfDay()
                );

        return ResponseEntity.ok(reporte);
    }
    

	@GetMapping("/sin-detectar")
	public ResponseEntity<?> obtenerSinDetectar(
	        @RequestParam
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate desde,
	
	        @RequestParam
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate hasta) {
	
	    if (desde.isAfter(hasta)) {
	        return ResponseEntity.badRequest().body(
	                Map.of("error",
	                        "La fecha desde no puede ser posterior a hasta")
	        );
	    }
	
	    return ResponseEntity.ok(
	            reporteProcesosService.obtenerSinDetectar(
	                    desde.atStartOfDay(),
	                    hasta.plusDays(1).atStartOfDay()
	            )
	    );
	}

}
