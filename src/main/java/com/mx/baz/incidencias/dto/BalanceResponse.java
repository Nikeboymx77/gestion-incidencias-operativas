package com.mx.baz.incidencias.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BalanceResponse {

    private List<BalanceEmpleadoResponse> cargaBaja;

    private List<BalanceEmpleadoResponse> cargaMedia;

    private List<BalanceEmpleadoResponse> cargaAlta;
}
