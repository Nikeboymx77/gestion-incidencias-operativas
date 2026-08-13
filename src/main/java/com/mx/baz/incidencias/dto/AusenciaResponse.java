package com.mx.baz.incidencias.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AusenciaResponse {

    private Long id;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String motivo;

    private String observaciones;
}