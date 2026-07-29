package com.mx.baz.incidencias.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankingEmpleadoResponse {

    private Long empleadoId;

    private String nombre;

    private Long pendientes;

    private Long enProceso;

    private Long reabiertas;

    private Long resueltas;

    private Long totalActivas;
}
