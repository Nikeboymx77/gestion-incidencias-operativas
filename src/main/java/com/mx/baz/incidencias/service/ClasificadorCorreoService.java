package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.enums.TipoSeguimiento;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class ClasificadorCorreoService {

    public TipoSeguimiento clasificar(CorreoDTO correo) {

        if (correo == null) {
            return TipoSeguimiento.RESPUESTA;
        }

        String textoCompleto = normalizar(
                unirTexto(
                        correo.getAsunto(),
                        correo.getDescripcion()
                )
        );
        
        if (persisteError(textoCompleto)) {
            return TipoSeguimiento.PERSISTE_ERROR;
        }

        if (esReasignacion(textoCompleto)) {
            return TipoSeguimiento.REASIGNACION;
        }

        return TipoSeguimiento.RESPUESTA;
    }

    private boolean esReasignacion(String texto) {

        return contieneAlgunaFrase(
                texto,
                "me apoyan a validarlo",
                "nos apoyan a validarlo",
                "les agradezco su apoyo",
                "se canaliza para su apoyo",
                "se turna para su atencion",
                "se reasigna para su atencion",
                "favor de apoyar con la validacion",
                "solicito su apoyo para validar"
        );
    }
    
    private boolean persisteError(String texto) {

        return contieneAlgunaFrase(
                texto,
                "el problema persiste",
                "el error persiste",
                "la falla persiste",
                "continua el problema",
                "continua el error",
                "continua la falla",
                "sigue presentando el error",
                "sigue presentando la falla",
                "sigue sin funcionar",
                "aun presenta el error",
                "aun presenta la falla",
                "no se soluciono",
                "no quedo solucionado",
                "el inconveniente continua",
                "se sigue presentando",
                "continua presentandose",
                "sigue con el mismo error"
        );
    }

    private boolean contieneAlgunaFrase(
            String texto,
            String... frases) {

        if (texto == null || texto.isBlank()) {
            return false;
        }

        for (String frase : frases) {
            if (texto.contains(frase)) {
                return true;
            }
        }

        return false;
    }

    private String unirTexto(
            String asunto,
            String descripcion) {

        String asuntoSeguro =
                asunto == null ? "" : asunto;

        String descripcionSegura =
                descripcion == null ? "" : descripcion;

        return asuntoSeguro + " " + descripcionSegura;
    }

    private String normalizar(String texto) {

        if (texto == null) {
            return "";
        }

        String sinAcentos = Normalizer.normalize(
                        texto,
                        Normalizer.Form.NFD
                )
                .replaceAll("\\p{M}", "");

        return sinAcentos
                .toLowerCase(Locale.ROOT)
                .replace('\u00A0', ' ')
                .replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replaceAll("\\s+", " ")
                .trim();
    }
}