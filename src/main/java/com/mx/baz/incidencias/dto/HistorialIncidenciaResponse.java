package com.mx.baz.incidencias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialIncidenciaResponse {

    private Long id;

    private String accion;

    private String comentario;

    private String usuario;

    private LocalDateTime fechaEvento;
}
