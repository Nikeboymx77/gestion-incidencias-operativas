package com.mx.baz.incidencias.integration.mail.normalizer;

import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import org.springframework.stereotype.Component;

@Component
public class MailNormalizer {

    public CorreoDTO normalizar(CorreoDTO correo) {

        correo.setIdCorreo(correo.getIdCorreo().trim());
        correo.setAsunto(correo.getAsunto().trim());
        correo.setRemitente(correo.getRemitente().trim().toLowerCase());
        correo.setDescripcion(correo.getDescripcion().trim());

        if (correo.getCarpetaOrigen() != null) {
            correo.setCarpetaOrigen(correo.getCarpetaOrigen().trim());
        }

        return correo;
    }
}