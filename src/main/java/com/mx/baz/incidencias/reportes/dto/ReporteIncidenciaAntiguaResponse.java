package com.mx.baz.incidencias.reportes.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReporteIncidenciaAntiguaResponse {

    private Long incidenciaId;

    private String folio;

    private String asunto;

    private String estado;

    private String prioridad;

    private Long empleadoId;

    private String empleado;

    private LocalDateTime fechaCreacion;

    private Long antiguedadMinutos;
}
