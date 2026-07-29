package com.mx.baz.incidencias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoResumenOperativoResponse {

    private Long id;
    private String nombre;
    private String usernameTelegram;
    private String email;
    private Boolean activo;

    private Long pendientes;
    private Long enProceso;
    private Long reabiertas;
    private Long totalActivas;

    private LocalDateTime ultimaAsignacion;
}
