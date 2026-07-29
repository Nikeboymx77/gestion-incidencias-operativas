package com.mx.baz.incidencias.dto;

import com.mx.baz.incidencias.enums.EstadoIncidencia;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncidenciaAtrasadaResponse {

    private String folio;

    private String asunto;

    private String empleado;

    private EstadoIncidencia estado;

    private Long diasAbierta;
}