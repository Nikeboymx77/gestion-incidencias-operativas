package com.mx.baz.incidencias.dto;

import com.mx.baz.incidencias.enums.MotivoAusencia;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmpleadoAusenciaRequest {

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private MotivoAusencia motivo;

    private String observaciones;
}