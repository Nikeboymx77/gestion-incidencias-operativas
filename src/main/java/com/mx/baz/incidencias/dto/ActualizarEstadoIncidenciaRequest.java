package com.mx.baz.incidencias.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarEstadoIncidenciaRequest {

    private String usuario;
    private String comentario;
}