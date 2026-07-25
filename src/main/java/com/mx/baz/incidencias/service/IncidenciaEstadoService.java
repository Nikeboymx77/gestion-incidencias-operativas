package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.enums.TipoSeguimiento;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidenciaEstadoService {

    private final IncidenciaRepository incidenciaRepository;

    /**
     * Evalúa si el tipo de seguimiento recibido debe provocar
     * un cambio automático en el estado de la incidencia.
     */
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

        incidencia.setEstado(
                EstadoIncidencia.REABIERTA
        );

        incidenciaRepository.save(incidencia);

        log.warn(
                "Incidencia reabierta automáticamente. Folio: {}, motivo: persistencia del error",
                incidencia.getFolio()
        );

        return true;
    }
}
