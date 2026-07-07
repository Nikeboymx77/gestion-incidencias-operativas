package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.dto.ActualizarEstadoIncidenciaRequest;
import com.mx.baz.incidencias.dto.IncidenciaRequest;
import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.dto.ResolverIncidenciaRequest;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.entity.HistorialIncidencia;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.exception.BusinessException;
import com.mx.baz.incidencias.exception.ErrorCodes;
import com.mx.baz.incidencias.mapper.IncidenciaMapper;
import com.mx.baz.incidencias.notification.NotificationService;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import com.mx.baz.incidencias.repository.HistorialIncidenciaRepository;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import com.mx.baz.incidencias.schedule.AssignmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final AssignmentService assignmentService;
    private final IncidenciaMapper incidenciaMapper;

    @Transactional
    public IncidenciaResponse crearIncidencia(IncidenciaRequest request) {

        Empleado empleadoAsignado = assignmentService.obtenerEmpleadoDisponible();

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

        Incidencia incidenciaGuardada = incidenciaRepository.save(incidencia);

        assignmentService.actualizarUltimaAsignacion(empleadoAsignado);

        notificationService.notificarNuevaIncidencia(incidenciaGuardada);

        return incidenciaMapper.toResponse(incidenciaGuardada);
    }
    
    public IncidenciaResponse resolverIncidencia(
            String folio,
            ResolverIncidenciaRequest request) {

        Incidencia incidencia = incidenciaRepository.findByFolio(folio)
                .orElseThrow(() -> new BusinessException(ErrorCodes.INCIDENCIA_NO_ENCONTRADA,"Incidencia no encontrada"));

        if (incidencia.getEstado() == EstadoIncidencia.PENDIENTE) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_NO_TOMADA,
                    "La incidencia " + folio + " aún no ha sido tomada"
            );
        }

        if (incidencia.getEstado() == EstadoIncidencia.RESUELTA) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_RESUELTA,
                    "La incidencia " + folio + " ya fue resuelta"
            );
        }

        if (incidencia.getEstado() != EstadoIncidencia.EN_PROCESO) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_YA_TOMADA,
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

        return incidenciaMapper.toResponse(incidencia);
    }
    
    public List<IncidenciaResponse> obtenerPendientes() {
    	return incidenciaRepository.findByEstado(EstadoIncidencia.PENDIENTE)
                .stream()
                .map(incidenciaMapper::toResponse)
                .toList();
    }

    public IncidenciaResponse obtenerPorFolio(String folio) {
        return incidenciaRepository.findByFolio(folio)
                .map(incidenciaMapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.INCIDENCIA_NO_ENCONTRADA,
                        "Incidencia no encontrada"
                ));
    }
    
    public IncidenciaResponse tomarIncidencia(
            String folio,
            ActualizarEstadoIncidenciaRequest request) {

        Incidencia incidencia = incidenciaRepository.findByFolio(folio)
                .orElseThrow(() -> new BusinessException(ErrorCodes.INCIDENCIA_NO_ENCONTRADA,"Incidencia no encontrada"));

        if (incidencia.getEstado() == EstadoIncidencia.EN_PROCESO) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_YA_TOMADA,
                    "La incidencia " + folio + " ya fue tomada por "
                            + incidencia.getUsuarioQueLaTomo()
            );
        }

        if (incidencia.getEstado() == EstadoIncidencia.RESUELTA) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_RESUELTA,
                    "La incidencia " + folio + " ya fue resuelta"
            );
        }

        if (incidencia.getEstado() != EstadoIncidencia.PENDIENTE) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_YA_TOMADA,
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

        return incidenciaMapper.toResponse(incidencia);
    }
}