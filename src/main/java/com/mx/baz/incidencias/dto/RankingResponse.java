package com.mx.baz.incidencias.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankingResponse {

    private List<RankingEmpleadoResponse> empleados;
}
