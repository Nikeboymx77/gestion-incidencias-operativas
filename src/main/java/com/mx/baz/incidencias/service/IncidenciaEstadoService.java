package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.enums.TipoSeguimiento;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.mx.baz.incidencias.events.IncidenciaReabiertaEvent;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidenciaEstadoService {

    private static final String USUARIO_SISTEMA = "SISTEMA";

    private static final String COMENTARIO_REAPERTURA =
            "Incidencia reabierta automáticamente "
                    + "por persistencia del error detectada en correo";

    private final IncidenciaRepository incidenciaRepository;
    private final HistorialIncidenciaService historialIncidenciaService;
    
    private final ApplicationEventPublisher eventPublisher;

    public boolean evaluarCambioEstado(
            Incidencia incidencia,
            TipoSeguimiento tipoSeguimiento) {

        if (incidencia == null || tipoSeguimiento == null) {
            return false;
        }

        return switch (tipoSeguimiento) {

            case PERSISTE_ERROR ->
                    reabrirSiEstaResuelta(incidencia);

            case RESPUESTA,
                 REASIGNACION,
                 ESCALAMIENTO,
                 CONFIRMACION_SOLUCION,
                 INFORMATIVO -> false;
        };
    }

    private boolean reabrirSiEstaResuelta(
            Incidencia incidencia) {

        if (incidencia.getEstado()
                != EstadoIncidencia.RESUELTA) {

            return false;
        }

        EstadoIncidencia estadoAnterior =
                incidencia.getEstado();

        incidencia.setEstado(
                EstadoIncidencia.REABIERTA
        );

        incidenciaRepository.save(incidencia);

        historialIncidenciaService.registrarCambioEstado(
                incidencia,
                estadoAnterior,
                EstadoIncidencia.REABIERTA,
                USUARIO_SISTEMA,
                COMENTARIO_REAPERTURA
        );
        
        eventPublisher.publishEvent(
                new IncidenciaReabiertaEvent(
                        incidencia,
                        COMENTARIO_REAPERTURA
                )
        );

        log.warn(
                "Incidencia reabierta automáticamente. " +
                "Folio: {}, estadoAnterior: {}, estadoNuevo: {}",
                incidencia.getFolio(),
                estadoAnterior,
                incidencia.getEstado()
        );

        return true;
    }
}