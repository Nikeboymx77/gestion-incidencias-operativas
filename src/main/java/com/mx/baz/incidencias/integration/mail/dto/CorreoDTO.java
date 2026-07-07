package com.mx.baz.incidencias.integration.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorreoDTO {
    private String asunto;
    private String remitente;
    private String descripcion;
    private LocalDateTime fechaCorreo;
    private String carpetaOrigen;
    private String idCorreo;
}