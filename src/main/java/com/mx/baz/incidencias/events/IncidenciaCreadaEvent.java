package com.mx.baz.incidencias.events;

import com.mx.baz.incidencias.entity.Incidencia;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IncidenciaCreadaEvent {

    private final Incidencia incidencia;
   
}
