package com.mx.baz.incidencias.repository.projection;

import java.time.LocalDate;

public interface ReporteEvolucionTiemposProjection {

    LocalDate getFecha();

    Double getTiempoPromedioAtencionMinutos();

    Double getTiempoPromedioResolucionMinutos();
}
