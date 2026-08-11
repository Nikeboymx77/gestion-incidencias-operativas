package com.mx.baz.incidencias.controller;

import com.mx.baz.incidencias.dto.HistorialIncidenciaResponse;
import com.mx.baz.incidencias.service.HistorialIncidenciaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class HistorialIncidenciaController {

    private final HistorialIncidenciaService
            historialIncidenciaService;


    @GetMapping("/{folio}/historial")
    public ResponseEntity<List<HistorialIncidenciaResponse>>
    obtenerHistorial(
            @PathVariable String folio
    ) {

        return ResponseEntity.ok(
                historialIncidenciaService
                        .obtenerPorFolio(folio)
        );
    }
}
