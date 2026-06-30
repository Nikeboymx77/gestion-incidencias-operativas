package com.mx.baz.incidencias.dto;

import com.mx.baz.incidencias.enums.PrioridadIncidencia;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class IncidenciaRequest {

    private String folio;
    private String asunto;
    private String remitente;
    private LocalDateTime fechaCorreo;
    private String carpetaOrigen;
    private PrioridadIncidencia prioridad;
    private String descripcion;
}
