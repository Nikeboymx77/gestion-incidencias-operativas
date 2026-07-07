package com.mx.baz.incidencias.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmpleadoResumenResponse {

    private Long id;
    private String nombre;
    private String usernameTelegram;
    private String email;
}
