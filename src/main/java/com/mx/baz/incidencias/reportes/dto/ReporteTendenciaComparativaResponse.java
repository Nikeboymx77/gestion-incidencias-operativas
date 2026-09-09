package com.mx.baz.incidencias.reportes.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ReporteTendenciaComparativaResponse {

    private List<PuntoTendencia> periodoActual;
    private List<PuntoTendencia> periodoAnterior;


    @Data
    @Builder
    public static class PuntoTendencia {

        private Integer dia;

        private LocalDate fecha;

        private Long total;
    }
}
