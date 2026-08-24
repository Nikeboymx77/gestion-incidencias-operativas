package com.mx.baz.incidencias.reportes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteTiemposResponse {

    private Double tiempoPromedioAtencionMinutos;

    private Double tiempoPromedioResolucionMinutos;
}
