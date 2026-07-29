package com.mx.baz.incidencias.repository.projection;

public interface RankingEmpleadoProjection {

    Long getEmpleadoId();

    String getNombre();

    Long getPendientes();

    Long getEnProceso();

    Long getReabiertas();

    Long getResueltas();

    Long getTotalActivas();
}
