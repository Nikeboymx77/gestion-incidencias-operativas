package com.mx.baz.incidencias.controller;

import com.mx.baz.incidencias.dto.ActualizarEstadoIncidenciaRequest;
import com.mx.baz.incidencias.dto.BalanceResponse;
import com.mx.baz.incidencias.dto.CancelarIncidenciaRequest;
import com.mx.baz.incidencias.dto.EstadisticasResponse;
import com.mx.baz.incidencias.dto.IncidenciaAtrasadaResponse;
import com.mx.baz.incidencias.dto.IncidenciaRequest;
import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.dto.RankingEmpleadoResponse;
import com.mx.baz.incidencias.dto.ReasignarIncidenciaRequest;
import com.mx.baz.incidencias.dto.ResolverIncidenciaRequest;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.service.IncidenciaService;
import com.mx.baz.incidencias.dto.ReabrirIncidenciaRequest;
import com.mx.baz.incidencias.dto.CrearIncidenciaManualRequest;

import jakarta.validation.Valid;
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
    
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasResponse> obtenerEstadisticas() {
        return ResponseEntity.ok(incidenciaService.obtenerEstadisticas());
    }
    
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingEmpleadoResponse>> obtenerRanking() {

        return ResponseEntity.ok(
                incidenciaService.obtenerRanking()
        );
    }
    
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> obtenerBalance() {
        return ResponseEntity.ok(
                incidenciaService.obtenerBalance()
        );
    }
    
    @GetMapping("/atrasadas")
    public ResponseEntity<List<IncidenciaAtrasadaResponse>> obtenerIncidenciasAtrasadas(
            @RequestParam(defaultValue = "3") int dias
    ) {
        return ResponseEntity.ok(
                incidenciaService.obtenerIncidenciasAtrasadas(dias)
        );
    }
    
    @PutMapping("/{folio}/cancelar")
    public ResponseEntity<IncidenciaResponse> cancelarIncidencia(
            @PathVariable String folio,
            @Valid @RequestBody CancelarIncidenciaRequest request
    ) {

        IncidenciaResponse response =
                incidenciaService.cancelarIncidencia(
                        folio,
                        request.getUsuario(),
                        request.getComentario()
                );

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{folio}/reasignar")
    public ResponseEntity<IncidenciaResponse> reasignarIncidencia(
            @PathVariable String folio,
            @Valid
            @RequestBody ReasignarIncidenciaRequest request
    ) {

        IncidenciaResponse respuesta =
                incidenciaService.reasignarIncidencia(
                        folio,
                        request
                );

        return ResponseEntity.ok(respuesta);
    }
    
    @PutMapping("/{folio}/reabrir")
    public ResponseEntity<IncidenciaResponse> reabrirIncidencia(
            @PathVariable String folio,
            @Valid
            @RequestBody ReabrirIncidenciaRequest request
    ) {

        return ResponseEntity.ok(
                incidenciaService.reabrirIncidencia(
                        folio,
                        request
                )
        );
    }
    
    @PostMapping("/manual")
    public ResponseEntity<IncidenciaResponse>
    crearIncidenciaManual(
            @Valid
            @RequestBody
            CrearIncidenciaManualRequest request
    ) {

        IncidenciaResponse response =
                incidenciaService
                        .crearIncidenciaManual(
                                request
                        );

        return ResponseEntity.ok(
                response
        );
    }
}
