package com.mx.baz.incidencias.reportes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteCargaEmpleadoResponse {

    private Long empleadoId;

    private String nombre;

    private Long totalActivas;

    private Long pendientes;

    private Long enProceso;

    private Long reabiertas;
}