package com.mx.baz.incidencias.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoRequest {

    private String nombre;
    private String usernameTelegram;
    private String email;
    private Boolean trabajaSemana;
    private Boolean trabajaFinSemana;
    private Integer ordenAsignacion;
}