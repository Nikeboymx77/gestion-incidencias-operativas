package com.mx.baz.incidencias.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadisticasResponse {

    private Long pendientes;

    private Long enProceso;

    private Long reabiertas;

    private Long resueltasHoy;

    private Long empleadosActivos;
}
