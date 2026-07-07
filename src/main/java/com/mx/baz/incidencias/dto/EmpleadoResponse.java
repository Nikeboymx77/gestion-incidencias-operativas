package com.mx.baz.incidencias.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class EmpleadoResponse {

    private Long id;
    private String nombre;
    private String usernameTelegram;
    private String email;
    private Boolean activo;
    private LocalDateTime ultimaAsignacion;
    private List<DayOfWeek> diasLaborales;
}
