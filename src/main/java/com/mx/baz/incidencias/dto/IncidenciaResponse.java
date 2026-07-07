package com.mx.baz.incidencias.dto;

import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.enums.PrioridadIncidencia;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class IncidenciaResponse {

    private Long id;
    private String folio;
    private String asunto;
    private String remitente;
    private LocalDateTime fechaCorreo;
    private String carpetaOrigen;
    private PrioridadIncidencia prioridad;
    private String descripcion;
    private EstadoIncidencia estado;
    private EmpleadoResumenResponse empleadoAsignado;
    private LocalDateTime fechaAsignacion;
    private String usuarioQueLaTomo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaResolucion;
}
