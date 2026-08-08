package com.mx.baz.incidencias.events;

import com.mx.baz.incidencias.entity.Incidencia;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IncidenciaReasignadaEvent {

    private final Incidencia incidencia;

    private final String empleadoAnterior;

    private final String empleadoNuevo;

    private final String usuario;

    private final String comentario;
}
