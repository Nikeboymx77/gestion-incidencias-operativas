package com.mx.baz.incidencias.integration.mail.validator;

import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.mail.dto.MailValidationResult;
import org.springframework.stereotype.Component;

@Component
public class MailValidator {

    public MailValidationResult validar(CorreoDTO correo) {

        if (correo == null) {
            return MailValidationResult.invalido("Correo vacío");
        }

        if (correo.getIdCorreo() == null || correo.getIdCorreo().isBlank()) {
            return MailValidationResult.invalido("idCorreo obligatorio");
        }

        if (correo.getAsunto() == null || correo.getAsunto().isBlank()) {
            return MailValidationResult.invalido("Asunto obligatorio");
        }

        if (correo.getRemitente() == null || correo.getRemitente().isBlank()) {
            return MailValidationResult.invalido("Remitente obligatorio");
        }

        if (correo.getDescripcion() == null || correo.getDescripcion().isBlank()) {
            return MailValidationResult.invalido("Descripción obligatoria");
        }

        if (correo.getFechaCorreo() == null) {
            return MailValidationResult.invalido("Fecha del correo obligatoria");
        }

        return MailValidationResult.valido();
    }
}