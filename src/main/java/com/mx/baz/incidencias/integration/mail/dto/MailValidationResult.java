package com.mx.baz.incidencias.integration.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MailValidationResult {

    private boolean valido;
    private String motivo;

    public static MailValidationResult valido() {
        return new MailValidationResult(true, null);
    }

    public static MailValidationResult invalido(String motivo) {
        return new MailValidationResult(false, motivo);
    }
}
