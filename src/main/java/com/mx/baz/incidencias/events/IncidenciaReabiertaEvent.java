package com.mx.baz.incidencias.events;

import com.mx.baz.incidencias.entity.Incidencia;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IncidenciaReabiertaEvent {

    private final Incidencia incidencia;
    private final String motivo;
}