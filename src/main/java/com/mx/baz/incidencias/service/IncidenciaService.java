package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.dto.IncidenciaRequest;
import com.mx.baz.incidencias.dto.ResolverIncidenciaRequest;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.entity.HistorialIncidencia;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import com.mx.baz.incidencias.repository.HistorialIncidenciaRepository;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final HistorialIncidenciaRepository historialIncidenciaRepository;

    public Incidencia crearIncidencia(IncidenciaRequest request) {

        DayOfWeek diaActual = LocalDateTime.now().getDayOfWeek();

        Empleado empleadoAsignado;

        if (diaActual == DayOfWeek.SATURDAY || diaActual == DayOfWeek.SUNDAY) {

            empleadoAsignado = empleadoRepository
                    .findFirstByTrabajaFinSemanaTrueAndActivoTrueOrderByOrdenAsignacionAsc()
                    .orElseThrow(() ->
                            new RuntimeException("No hay empleados activos para fin de semana"));

        } else {

            empleadoAsignado = empleadoRepository
                    .findFirstByTrabajaSemanaTrueAndActivoTrueOrderByOrdenAsignacionAsc()
                    .orElseThrow(() ->
                            new RuntimeException("No hay empleados activos para semana"));
        }

        Incidencia incidencia = Incidencia.builder()
                .folio(request.getFolio())
                .asunto(request.getAsunto())
                .remitente(request.getRemitente())
                .fechaCorreo(request.getFechaCorreo())
                .carpetaOrigen(request.getCarpetaOrigen())
                .prioridad(request.getPrioridad())
                .descripcion(request.getDescripcion())
                .estado(EstadoIncidencia.PENDIENTE)
                .empleadoAsignado(empleadoAsignado)
                .fechaAsignacion(LocalDateTime.now())
                .build();

        return incidenciaRepository.save(incidencia);
    }
    
    public Incidencia resolverIncidencia(
            String folio,
            ResolverIncidenciaRequest request) {

        Incidencia incidencia = incidenciaRepository
                .findByFolio(folio)
                .orElseThrow(() ->
                        new RuntimeException("Incidencia no encontrada"));

        incidencia.setEstado(EstadoIncidencia.RESUELTA);
        incidencia.setFechaResolucion(LocalDateTime.now());

        incidenciaRepository.save(incidencia);

        HistorialIncidencia historial = HistorialIncidencia.builder()
                .incidencia(incidencia)
                .accion("RESUELTA")
                .usuario(request.getUsuario())
                .comentario(request.getComentario())
                .build();

        historialIncidenciaRepository.save(historial);

        return incidencia;
    }
}