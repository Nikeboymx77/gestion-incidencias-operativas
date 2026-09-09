package com.mx.baz.incidencias.repository.projection;

public interface ReporteCargaEmpleadoProjection {

    Long getEmpleadoId();

    String getNombre();

    Long getTotalActivas();

    Long getPendientes();

    Long getEnProceso();

    Long getReabiertas();
}
