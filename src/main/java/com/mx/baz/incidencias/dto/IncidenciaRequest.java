package com.mx.baz.incidencias.dto;

import com.mx.baz.incidencias.enums.PrioridadIncidencia;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class IncidenciaRequest {

    private String folio;
    private String asunto;
    private String remitente;
    private LocalDateTime fechaCorreo;
    private String carpetaOrigen;
    private PrioridadIncidencia prioridad;
    private String descripcion;
    private String sucursal;
    private String clienteUnico;
    private String nombreCliente;
    private String equipo;
    private String motivo;
}
