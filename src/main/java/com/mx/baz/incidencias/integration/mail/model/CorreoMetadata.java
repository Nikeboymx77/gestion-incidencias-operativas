package com.mx.baz.incidencias.integration.mail.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CorreoMetadata {

    private String folio;
    private String sucursal;
    private String clienteUnico;
    private String nombreCliente;
    private String motivo;
}
