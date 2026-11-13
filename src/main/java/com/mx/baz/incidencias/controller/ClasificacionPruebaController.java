package com.mx.baz.incidencias.controller;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.entity.ReglaClasificacion;
import com.mx.baz.incidencias.entity.ReglaProcesoAfectado;
import com.mx.baz.incidencias.reportes.service.ClasificacionProcesoService;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import com.mx.baz.incidencias.service.ClasificacionIncidenciaService;
import com.mx.baz.incidencias.service.ExtractorMensajeActualService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import com.mx.baz.incidencias.entity.IncidenciaClasificacion;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/clasificacion/pruebas")
@RequiredArgsConstructor
public class ClasificacionPruebaController {

    private final IncidenciaRepository incidenciaRepository;
    private final ClasificacionIncidenciaService clasificacionService;
    
    @Autowired
    private ClasificacionProcesoService clasificacionProcesoService;
    
    @Autowired
    private ExtractorMensajeActualService extractorMensajeActualService;

    @GetMapping("/{incidenciaId}")
    public ResponseEntity<?> probarClasificacion(
            @PathVariable Long incidenciaId) {

        Optional<Incidencia> incidenciaOpt =
                incidenciaRepository.findById(incidenciaId);

        if (incidenciaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Incidencia incidencia = incidenciaOpt.get();

        Optional<ReglaClasificacion> reglaOpt =
                clasificacionService.buscarRegla(
                        incidencia.getDescripcion()
                );

        if (reglaOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "incidenciaId", incidencia.getId(),
                    "folio", incidencia.getFolio(),
                    "estadoClasificacion", "SIN_CLASIFICAR",
                    "mensaje", "No se encontró una regla aplicable"
            ));
        }

        ReglaClasificacion regla = reglaOpt.get();

        return ResponseEntity.ok(Map.of(
                "incidenciaId", incidencia.getId(),
                "folio", incidencia.getFolio(),
                "estadoClasificacion", "CLASIFICADA",
                "categoria", regla.getCategoria().getCodigo(),
                "nombreCategoria", regla.getCategoria().getNombre(),
                "reglaId", regla.getId(),
                "patronDetectado", regla.getPatron()
        ));
    }
    
    @PostMapping("/{incidenciaId}/guardar")
    public ResponseEntity<?> guardarClasificacion(
            @PathVariable Long incidenciaId) {

        if (!incidenciaRepository.existsById(incidenciaId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje", "Incidencia no encontrada",
                            "incidenciaId", incidenciaId
                    ));
        }

        IncidenciaClasificacion clasificacion =
                clasificacionService.clasificarYGuardar(incidenciaId);

        Map<String, Object> respuesta = new java.util.LinkedHashMap<>();

        respuesta.put("clasificacionId", clasificacion.getId());
        respuesta.put("incidenciaId", incidenciaId);
        respuesta.put("estadoClasificacion",
                clasificacion.getEstado().name());

        respuesta.put("categoria",
                clasificacion.getCategoria() != null
                        ? clasificacion.getCategoria().getCodigo()
                        : null);

        respuesta.put("reglaId",
                clasificacion.getRegla() != null
                        ? clasificacion.getRegla().getId()
                        : null);

        respuesta.put("versionRegla", clasificacion.getVersionRegla());

        return ResponseEntity.ok(respuesta);
    }
    
    @GetMapping("/lote/vista-previa")
    public ResponseEntity<?> vistaPreviaLote() {

        List<Incidencia> incidencias =
                incidenciaRepository.buscarPendientesClasificacion(
                        PageRequest.of(0, 20)
                );

        List<Map<String, Object>> resultados = new ArrayList<>();

        for (Incidencia incidencia : incidencias) {

            Optional<ReglaClasificacion> reglaOpt =
                    clasificacionService.buscarRegla(
                            incidencia.getDescripcion()
                    );

            Map<String, Object> resultado = new LinkedHashMap<>();

            resultado.put("incidenciaId", incidencia.getId());
            resultado.put("folio", incidencia.getFolio());

            if (reglaOpt.isPresent()) {

                ReglaClasificacion regla = reglaOpt.get();

                resultado.put("estado", "CLASIFICADA");
                resultado.put(
                        "categoria",
                        regla.getCategoria().getCodigo()
                );
                resultado.put("reglaId", regla.getId());

            } else {

                resultado.put("estado", "SIN_CLASIFICAR");
                resultado.put("categoria", null);
                resultado.put("reglaId", null);
            }

            resultados.add(resultado);
        }

        return ResponseEntity.ok(Map.of(
                "totalAnalizadas", resultados.size(),
                "resultados", resultados
        ));
    }
    
    @GetMapping("/proceso/{incidenciaId}")
    public ResponseEntity<?> probarProceso(
            @PathVariable Long incidenciaId) {

        return incidenciaRepository.findById(incidenciaId)
                .map(incidencia -> {

                    Optional<ReglaProcesoAfectado> reglaOpt =
                            clasificacionProcesoService.buscarRegla(
                                    incidencia.getAsunto()
                            );

                    Map<String, Object> resultado =
                            new LinkedHashMap<>();

                    resultado.put("incidenciaId", incidencia.getId());
                    resultado.put("folio", incidencia.getFolio());
                    resultado.put("asunto", incidencia.getAsunto());

                    if (reglaOpt.isPresent()) {

                        ReglaProcesoAfectado regla = reglaOpt.get();

                        resultado.put("estado", "DETECTADO");
                        resultado.put(
                                "proceso",
                                regla.getProceso().getCodigo()
                        );
                        resultado.put("reglaId", regla.getId());

                    } else {

                        resultado.put("estado", "SIN_DETECTAR");
                        resultado.put("proceso", null);
                        resultado.put("reglaId", null);
                    }

                    return ResponseEntity.ok(resultado);

                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/completa/{incidenciaId}")
    public ResponseEntity<?> vistaPreviaCompleta(
            @PathVariable Long incidenciaId) {

        Optional<Incidencia> incidenciaOpt =
                incidenciaRepository.findById(incidenciaId);

        if (incidenciaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Incidencia incidencia = incidenciaOpt.get();

        Map<String, Object> resultado = new LinkedHashMap<>();

        resultado.put("incidenciaId", incidencia.getId());
        resultado.put("folio", incidencia.getFolio());
        resultado.put("asunto", incidencia.getAsunto());

        // 1. Detectar proceso afectado
        Optional<ReglaProcesoAfectado> reglaProceso =
                clasificacionProcesoService.buscarRegla(
                        incidencia.getAsunto()
                );

        Map<String, Object> proceso = new LinkedHashMap<>();

        if (reglaProceso.isPresent()) {

            ReglaProcesoAfectado regla = reglaProceso.get();

            proceso.put("estado", "DETECTADO");
            proceso.put("codigo", regla.getProceso().getCodigo());
            proceso.put("reglaId", regla.getId());

        } else {

            proceso.put("estado", "SIN_DETECTAR");
            proceso.put("codigo", null);
            proceso.put("reglaId", null);
        }

        resultado.put("procesoAfectado", proceso);

        // 2. Detectar error reportado
        Optional<ReglaClasificacion> reglaError =
                clasificacionService.buscarRegla(
                        incidencia.getDescripcion()
                );

        Map<String, Object> error = new LinkedHashMap<>();

        if (reglaError.isPresent()) {

            ReglaClasificacion regla = reglaError.get();

            error.put("estado", "DETECTADO");
            error.put("codigo", regla.getCategoria().getCodigo());
            error.put("reglaId", regla.getId());

        } else {

            error.put("estado", "SIN_DETECTAR");
            error.put("codigo", null);
            error.put("reglaId", null);
        }

        resultado.put("errorReportado", error);
        
        boolean procesoDetectado = reglaProceso.isPresent();
        boolean errorDetectado = reglaError.isPresent();

        String estadoGeneral;

        if (procesoDetectado && errorDetectado) {
            estadoGeneral = "COMPLETA";
        } else if (procesoDetectado || errorDetectado) {
            estadoGeneral = "PARCIAL";
        } else {
            estadoGeneral = "SIN_DETECCION";
        }

        resultado.put("estadoGeneral", estadoGeneral);

        return ResponseEntity.ok(resultado);
    }
    
    @GetMapping("/lote/completa")
    public ResponseEntity<?> vistaPreviaLoteCompleta() {

        List<Incidencia> incidencias =
                incidenciaRepository.buscarPendientesClasificacion(
                        PageRequest.of(0, 20)
                );

        int completas = 0;
        int parciales = 0;
        int sinDeteccion = 0;

        List<Map<String, Object>> resultados = new ArrayList<>();

        for (Incidencia incidencia : incidencias) {

            Optional<ReglaProcesoAfectado> reglaProceso =
                    clasificacionProcesoService.buscarRegla(
                            incidencia.getAsunto()
                    );

            Optional<ReglaClasificacion> reglaError =
                    clasificacionService.buscarRegla(
                            incidencia.getDescripcion()
                    );

            boolean procesoDetectado = reglaProceso.isPresent();
            boolean errorDetectado = reglaError.isPresent();

            String estadoGeneral;

            if (procesoDetectado && errorDetectado) {
                estadoGeneral = "COMPLETA";
                completas++;
            } else if (procesoDetectado || errorDetectado) {
                estadoGeneral = "PARCIAL";
                parciales++;
            } else {
                estadoGeneral = "SIN_DETECCION";
                sinDeteccion++;
            }

            Map<String, Object> resultado = new LinkedHashMap<>();

            resultado.put("incidenciaId", incidencia.getId());
            resultado.put("folio", incidencia.getFolio());

            resultado.put(
                    "proceso",
                    reglaProceso
                        .map(r -> r.getProceso().getCodigo())
                        .orElse(null)
            );

            resultado.put(
                    "error",
                    reglaError
                        .map(r -> r.getCategoria().getCodigo())
                        .orElse(null)
            );

            resultado.put("estadoGeneral", estadoGeneral);

            resultados.add(resultado);
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("totalAnalizadas", incidencias.size());
        respuesta.put("completas", completas);
        respuesta.put("parciales", parciales);
        respuesta.put("sinDeteccion", sinDeteccion);
        respuesta.put("resultados", resultados);

        return ResponseEntity.ok(respuesta);
    }
    
    @GetMapping("/descripcion/{incidenciaId}")
    public ResponseEntity<Map<String, Object>> probarDescripcion(
            @PathVariable Long incidenciaId) {

        Optional<Incidencia> incidenciaOpt =
                incidenciaRepository.findById(incidenciaId);

        if (incidenciaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Incidencia incidencia = incidenciaOpt.get();

        Optional<ReglaProcesoAfectado> reglaOpt =
                clasificacionProcesoService.buscarReglaDescripcion(
                        incidencia.getDescripcion()
                );

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("incidenciaId", incidencia.getId());
        respuesta.put("folio", incidencia.getFolio());
        respuesta.put("origen", "DESCRIPCION");

        if (reglaOpt.isPresent()) {

            ReglaProcesoAfectado regla = reglaOpt.get();

            respuesta.put("estado", "DETECTADO");
            respuesta.put("proceso", regla.getProceso().getCodigo());
            respuesta.put("reglaId", regla.getId());

        } else {

            respuesta.put("estado", "SIN_DETECCION");
            respuesta.put("proceso", null);
            respuesta.put("reglaId", null);
        }

        return ResponseEntity.ok(respuesta);
    }
    
    @GetMapping("/descripcion-limpia/{incidenciaId}")
    public ResponseEntity<?> probarDescripcionLimpia(
            @PathVariable Long incidenciaId) {

        return incidenciaRepository.findById(incidenciaId)
                .map(incidencia -> {

                    String descripcionOriginal =
                            incidencia.getDescripcion();

                    String mensajeActual =
                            extractorMensajeActualService.extraer(
                                    descripcionOriginal);

                    Map<String, Object> resultado =
                            new LinkedHashMap<>();

                    resultado.put("incidenciaId", incidenciaId);
                    resultado.put("folio", incidencia.getFolio());
                    resultado.put("longitudOriginal",
                            descripcionOriginal == null
                                    ? 0 : descripcionOriginal.length());
                    resultado.put("longitudMensajeActual",
                            mensajeActual.length());
                    resultado.put("mensajeActual", mensajeActual);

                    return ResponseEntity.ok(resultado);

                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
