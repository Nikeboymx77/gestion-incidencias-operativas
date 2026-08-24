package com.mx.baz.incidencias.repository.projection;

import java.time.LocalDate;

public interface ReporteDiaProjection {

    LocalDate getFecha();

    Long getTotal();
}