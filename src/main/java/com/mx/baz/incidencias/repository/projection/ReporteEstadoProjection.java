package com.mx.baz.incidencias.repository.projection;

import com.mx.baz.incidencias.enums.EstadoIncidencia;

public interface ReporteEstadoProjection {

    EstadoIncidencia getEstado();

    Long getTotal();
}
