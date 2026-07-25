package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.entity.HistorialIncidencia;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.repository.HistorialIncidenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistorialIncidenciaService {

    private static final String USUARIO_SISTEMA = "SISTEMA";

    private final HistorialIncidenciaRepository historialIncidenciaRepository;

    public HistorialIncidencia registrarCambioEstado(
            Incidencia incidencia,
            EstadoIncidencia estadoAnterior,
            EstadoIncidencia estadoNuevo,
            String usuario,
            String comentario) {

        if (incidencia == null) {
            throw new IllegalArgumentException(
                    "La incidencia es obligatoria para registrar el historial"
            );
        }

        if (estadoAnterior == null || estadoNuevo == null) {
            throw new IllegalArgumentException(
                    "Los estados anterior y nuevo son obligatorios"
            );
        }

        String usuarioRegistro =
                usuario == null || usuario.isBlank()
                        ? USUARIO_SISTEMA
                        : usuario.trim();

        String comentarioRegistro =
                comentario == null || comentario.isBlank()
                        ? construirComentarioPorDefecto(
                                estadoAnterior,
                                estadoNuevo
                        )
                        : comentario.trim();

        HistorialIncidencia historial =
                HistorialIncidencia.builder()
                        .incidencia(incidencia)
                        .accion(estadoNuevo.name())
                        .usuario(usuarioRegistro)
                        .comentario(comentarioRegistro)
                        .build();

        HistorialIncidencia historialGuardado =
                historialIncidenciaRepository.save(historial);

        log.info(
                "Cambio de estado registrado en historial. " +
                "Folio: {}, estadoAnterior: {}, estadoNuevo: {}, usuario: {}",
                incidencia.getFolio(),
                estadoAnterior,
                estadoNuevo,
                usuarioRegistro
        );

        return historialGuardado;
    }

    private String construirComentarioPorDefecto(
            EstadoIncidencia estadoAnterior,
            EstadoIncidencia estadoNuevo) {

        return "Cambio de estado de "
                + estadoAnterior
                + " a "
                + estadoNuevo;
    }
}