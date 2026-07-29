package com.mx.baz.incidencias.dto;

import com.mx.baz.incidencias.enums.NivelCarga;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceEmpleadoResponse {

    private Long empleadoId;

    private String nombre;

    private Long totalActivas;

    private NivelCarga nivelCarga;
}
