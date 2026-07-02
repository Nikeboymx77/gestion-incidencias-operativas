package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.dto.ActualizarEstadoIncidenciaRequest;
import com.mx.baz.incidencias.dto.IncidenciaRequest;
import com.mx.baz.incidencias.dto.ResolverIncidenciaRequest;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.entity.HistorialIncidencia;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.notification.NotificationService;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import com.mx.baz.incidencias.repository.HistorialIncidenciaRepository;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final HistorialIncidenciaRepository historialIncidenciaRepository;
    private final NotificationService notificationService;

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

        //return incidenciaRepository.save(incidencia);
        Incidencia incidenciaGuardada = incidenciaRepository.save(incidencia);

        notificationService.notificarNuevaIncidencia(incidenciaGuardada);

        return incidenciaGuardada;
    }
    
    public Incidencia resolverIncidencia(
            String folio,
            ResolverIncidenciaRequest request) {

        Incidencia incidencia = incidenciaRepository.findByFolio(folio)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));

        if (incidencia.getEstado() == EstadoIncidencia.PENDIENTE) {
            throw new RuntimeException(
                    "La incidencia " + folio + " aún no ha sido tomada"
            );
        }

        if (incidencia.getEstado() == EstadoIncidencia.RESUELTA) {
            throw new RuntimeException(
                    "La incidencia " + folio + " ya fue resuelta"
            );
        }

        if (incidencia.getEstado() != EstadoIncidencia.EN_PROCESO) {
            throw new RuntimeException(
                    "La incidencia " + folio + " no puede resolverse porque está en estado "
                            + incidencia.getEstado()
            );
        }

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

        notificationService.notificarIncidenciaResuelta(
                incidencia,
                request.getUsuario(),
                request.getComentario()
        );

        return incidencia;
    }
    
    public List<Incidencia> obtenerPendientes() {
        return incidenciaRepository.findByEstado(EstadoIncidencia.PENDIENTE);
    }

    public Incidencia obtenerPorFolio(String folio) {
        return incidenciaRepository.findByFolio(folio)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));
    }
    
    public Incidencia tomarIncidencia(
            String folio,
            ActualizarEstadoIncidenciaRequest request) {

        Incidencia incidencia = incidenciaRepository.findByFolio(folio)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));

        if (incidencia.getEstado() == EstadoIncidencia.EN_PROCESO) {
            throw new RuntimeException(
                    "La incidencia " + folio + " ya fue tomada por "
                            + incidencia.getUsuarioQueLaTomo()
            );
        }

        if (incidencia.getEstado() == EstadoIncidencia.RESUELTA) {
            throw new RuntimeException(
                    "La incidencia " + folio + " ya fue resuelta"
            );
        }

        if (incidencia.getEstado() != EstadoIncidencia.PENDIENTE) {
            throw new RuntimeException(
                    "La incidencia " + folio + " no puede ser tomada porque está en estado "
                            + incidencia.getEstado()
            );
        }

        incidencia.setEstado(EstadoIncidencia.EN_PROCESO);
        incidencia.setUsuarioQueLaTomo(request.getUsuario());
        incidencia.setFechaInicio(LocalDateTime.now());

        incidenciaRepository.save(incidencia);

        HistorialIncidencia historial = HistorialIncidencia.builder()
                .incidencia(incidencia)
                .accion("EN_PROCESO")
                .usuario(request.getUsuario())
                .comentario(request.getComentario())
                .build();

        historialIncidenciaRepository.save(historial);

        notificationService.notificarIncidenciaEnProceso(
                incidencia,
                request.getUsuario(),
                request.getComentario()
        );

        return incidencia;
    }
}