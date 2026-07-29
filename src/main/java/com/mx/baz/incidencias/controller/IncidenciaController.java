package com.mx.baz.incidencias.controller;

import com.mx.baz.incidencias.dto.ActualizarEstadoIncidenciaRequest;
import com.mx.baz.incidencias.dto.IncidenciaRequest;
import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.dto.ResolverIncidenciaRequest;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.service.IncidenciaService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @PostMapping
    public IncidenciaResponse crearIncidencia(
            @RequestBody IncidenciaRequest request) {

        return incidenciaService.crearIncidencia(request);
    }
    
    @PutMapping("/{folio}/resolver")
    public IncidenciaResponse resolverIncidencia(
            @PathVariable String folio,
            @RequestBody ResolverIncidenciaRequest request) {

        return incidenciaService.resolverIncidencia(folio, request);
    }
    
    @GetMapping("/pendientes")
    public List<IncidenciaResponse> obtenerPendientes() {
        return incidenciaService.obtenerPendientes();
    }

    @GetMapping("/{folio}")
    public IncidenciaResponse obtenerPorFolio(@PathVariable String folio) {
        return incidenciaService.obtenerPorFolio(folio);
    }
    
    @PutMapping("/{folio}/tomar")
    public IncidenciaResponse tomarIncidencia(
            @PathVariable String folio,
            @RequestBody ActualizarEstadoIncidenciaRequest request) {

        return incidenciaService.tomarIncidencia(folio, request);
    }
    
    @GetMapping("/empleado/{usernameTelegram}/pendientes")
    public ResponseEntity<List<IncidenciaResponse>>
            obtenerPendientesPorEmpleado(
                    @PathVariable String usernameTelegram) {

        return ResponseEntity.ok(
                incidenciaService.obtenerPendientesPorEmpleado(
                        usernameTelegram
                )
        );
    }
    
    @GetMapping("/empleado/nombre/{nombreEmpleado}/pendientes")
    public ResponseEntity<List<IncidenciaResponse>>
    obtenerPendientesPorNombreEmpleado(
            @PathVariable String nombreEmpleado) {

        return ResponseEntity.ok(
                incidenciaService
                        .obtenerPendientesPorNombreEmpleado(
                                nombreEmpleado
                        )
        );
    }
}
