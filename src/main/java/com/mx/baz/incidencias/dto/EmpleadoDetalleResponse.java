package com.mx.baz.incidencias.dto;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EmpleadoDetalleResponse {

    private Long id;

    private String nombre;

    private String usernameTelegram;

    private String email;

    private Boolean activo;

    private List<DayOfWeek> diasLaborales;

    private List<AusenciaResponse> ausencias;

    private Long pendientes;

    private Long enProceso;

    private Long reabiertas;

    private Long totalActivas;

    private LocalDateTime ultimaAsignacion;
}
