
package com.mx.baz.incidencias.dto;

import java.time.DayOfWeek;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoDiasMasivosRequest {

    private List<Long> empleadoIds;

    private String operacion;

    private List<DayOfWeek> diasLaborales;

}

