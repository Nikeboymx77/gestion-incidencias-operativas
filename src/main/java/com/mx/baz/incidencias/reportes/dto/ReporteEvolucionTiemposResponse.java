package com.mx.baz.incidencias.reportes.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReporteEvolucionTiemposResponse {

    private LocalDate fecha;

    private Double tiempoPromedioAtencionMinutos;

    private Double tiempoPromedioResolucionMinutos;
}