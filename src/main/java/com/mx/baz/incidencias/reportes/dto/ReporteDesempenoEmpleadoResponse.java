package com.mx.baz.incidencias.reportes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteDesempenoEmpleadoResponse {

    private Long empleadoId;

    private String nombre;

    private Long totalIncidencias;

    private Long resueltas;

    private Double porcentajeResolucion;

    private Double tiempoPromedioAtencionMinutos;

    private Double tiempoPromedioResolucionMinutos;
}
