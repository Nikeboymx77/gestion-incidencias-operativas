package com.mx.baz.incidencias.reportes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteComparativoResponse {

    private Periodo periodoActual;
    private Periodo periodoAnterior;

    @Data
    @Builder
    public static class Periodo {

        private Long total;
        private Long pendientes;
        private Long enProceso;
        private Long resueltas;
        private Long reabiertas;
        private Long canceladas;

    }
}
