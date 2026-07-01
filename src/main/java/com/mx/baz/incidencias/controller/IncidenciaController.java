package com.mx.baz.incidencias.controller;

import com.mx.baz.incidencias.dto.IncidenciaRequest;
import com.mx.baz.incidencias.dto.ResolverIncidenciaRequest;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.service.IncidenciaService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @PostMapping
    public Incidencia crearIncidencia(
            @RequestBody IncidenciaRequest request) {

        return incidenciaService.crearIncidencia(request);
    }
    
    @PutMapping("/{folio}/resolver")
    public Incidencia resolverIncidencia(
            @PathVariable String folio,
            @RequestBody ResolverIncidenciaRequest request) {

        return incidenciaService.resolverIncidencia(folio, request);
    }
    
    @GetMapping("/pendientes")
    public List<Incidencia> obtenerPendientes() {
        return incidenciaService.obtenerPendientes();
    }

    @GetMapping("/{folio}")
    public Incidencia obtenerPorFolio(@PathVariable String folio) {
        return incidenciaService.obtenerPorFolio(folio);
    }
}
