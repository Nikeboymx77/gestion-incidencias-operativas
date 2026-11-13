
package com.mx.baz.incidencias.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class ExtractorMensajeActualService {

    // Encabezados al inicio de una línea
    private static final Pattern INICIO_HISTORIAL =
            Pattern.compile(
                "(?im)^[ \\t]*(?:"
                + "De\\s*:"
                + "|From\\s*:"
                + "|On\\s+[^\\r\\n]+?\\s+wrote\\s*:"
                + "|El\\s+[^\\r\\n]+?\\s+escribi[oó]\\s*:"
                + "|-{2,}\\s*(?:Original Message|Mensaje original)\\s*-{2,}"
                + ")"
            );


    // Encabezados de correo aplanados en una sola línea.
    // Detecta remitentes con correo o con nombre + fecha de envío.
    private static final Pattern HISTORIAL_APLANADO =
            Pattern.compile(
                "(?i)(?:^|\\s)(?:De|From)\\s*:\\s*"
                + "(?:"
                + "[^\\r\\n]{0,180}?"
                + "[\\w.+%-]+@[\\w.-]+\\.[A-Za-z]{2,}"
                + "|"
                + "[^\\r\\n]{1,180}?"
                + "\\s+(?:Enviado|Sent)\\s*:"
                + ")"
            );


    // Residuos VML generados por correos de Outlook

	private static final Pattern ESTILOS_VML =
	        Pattern.compile(
	            "(?i)(?:"
	            + "[.#]?[a-z][\\w-]*\\s*\\\\\\s*:\\s*\\*"
	            + "|\\.shape"
	            + ")\\s*"
	            + "\\{\\s*behavior\\s*:\\s*"
	            + "url\\(#default#VML\\)\\s*;?\\s*\\}"
	        );


    public String extraer(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            return "";
        }

        String texto = descripcion;

        // 1. Eliminar residuos de estilos VML
        texto = ESTILOS_VML.matcher(texto).replaceAll(" ");

        // 2. Detectar encabezados de historial
        int posicionCorte = texto.length();

        Matcher matcherLineas =
                INICIO_HISTORIAL.matcher(texto);

        if (matcherLineas.find()) {
            posicionCorte = Math.min(
                    posicionCorte,
                    matcherLineas.start()
            );
        }

        Matcher matcherAplanado =
                HISTORIAL_APLANADO.matcher(texto);

        if (matcherAplanado.find()) {
            posicionCorte = Math.min(
                    posicionCorte,
                    matcherAplanado.start()
            );
        }

        // 3. Conservar solamente el mensaje actual
        texto = texto.substring(0, posicionCorte);

        // 4. Normalizar espacios
        texto = texto.replaceAll("\\s+", " ").trim();

        return texto;
    }
}
