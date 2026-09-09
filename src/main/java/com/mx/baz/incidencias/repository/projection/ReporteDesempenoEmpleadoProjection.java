package com.mx.baz.incidencias.repository.projection;

public interface ReporteDesempenoEmpleadoProjection {

    Long getEmpleadoId();

    String getNombre();

    Long getTotalIncidencias();

    Long getResueltas();

    Double getTiempoPromedioAtencionMinutos();

    Double getTiempoPromedioResolucionMinutos();
}
