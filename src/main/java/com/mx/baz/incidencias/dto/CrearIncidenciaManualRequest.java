package com.mx.baz.incidencias.dto;

import com.mx.baz.incidencias.enums.PrioridadIncidencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearIncidenciaManualRequest {

    @NotBlank(
            message = "El asunto es obligatorio"
    )
    private String asunto;


    /*
     * Persona que reportó el caso.
     *
     * Ejemplo:
     * "Carlos Buitrón - WhatsApp"
     */
    private String reportadoPor;


    @NotNull(
            message = "La prioridad es obligatoria"
    )
    private PrioridadIncidencia prioridad;


    @NotBlank(
            message = "La descripción es obligatoria"
    )
    private String descripcion;


    private String sucursal;

    private String clienteUnico;

    private String nombreCliente;

    private String equipo;

    private String motivo;
}
