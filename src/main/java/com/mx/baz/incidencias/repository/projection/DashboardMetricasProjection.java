package com.mx.baz.incidencias.repository.projection;

public interface DashboardMetricasProjection {

    Long getTotal();

    Long getPendientes();

    Long getEnProceso();

    Long getResueltas();

    Long getReabiertas();

    Long getCanceladas();
}