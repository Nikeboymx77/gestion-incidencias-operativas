package com.mx.baz.incidencias.repository.projection;

import java.time.LocalDateTime;

public interface ReporteIncidenciaAntiguaProjection {

    Long getIncidenciaId();

    String getFolio();

    String getAsunto();

    String getEstado();

    String getPrioridad();

    Long getEmpleadoId();

    String getEmpleado();

    LocalDateTime getFechaCreacion();

    Long getAntiguedadMinutos();
}
