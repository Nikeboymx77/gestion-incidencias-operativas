package com.mx.baz.incidencias.events;

import com.mx.baz.incidencias.entity.Incidencia;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IncidenciaEnProcesoEvent {

    private final Incidencia incidencia;

    private final String usuario;

    private final String comentario;

}