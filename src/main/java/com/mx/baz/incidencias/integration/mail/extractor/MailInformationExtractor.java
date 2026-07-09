package com.mx.baz.incidencias.integration.mail.extractor;

import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.mail.model.CorreoMetadata;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MailInformationExtractor {

    private static final Pattern FOLIO_PATTERN =
            Pattern.compile("(INC\\d+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern SUCURSAL_PATTERN =
            Pattern.compile("Sucursal:\\s*(.+?)(?:\\s+WS_|\\s+Cliente\\s+Único:|$)", Pattern.CASE_INSENSITIVE);

    private static final Pattern CLIENTE_UNICO_PATTERN =
            Pattern.compile("Cliente\\s+Único:\\s*([0-9\\-]+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern NOMBRE_PATTERN =
            Pattern.compile("Nombre:\\s*(.+?)(?:\\s+Fecha\\s+de\\s+Nacimiento:|$)", Pattern.CASE_INSENSITIVE);

    private static final Pattern MOTIVO_PATTERN =
            Pattern.compile("MOTIVO:\\s*(.+?)(?:\\s+\\d+\\s+\\d+\\s+\\d+|\\s+STATUS|\\s+PEDIDO|$)", Pattern.CASE_INSENSITIVE);

    public CorreoMetadata extraer(CorreoDTO correo) {

        String asunto = correo.getAsunto() != null ? correo.getAsunto() : "";
        String descripcion = correo.getDescripcion() != null ? correo.getDescripcion() : "";
        String texto = (asunto + " " + descripcion).replaceAll("\\s+", " ").trim();

        return CorreoMetadata.builder()
                .folio(extraerPrimero(FOLIO_PATTERN, texto))
                .sucursal(extraerSucursal(asunto, texto))
                .clienteUnico(extraerPrimero(CLIENTE_UNICO_PATTERN, texto))
                .nombreCliente(extraerPrimero(NOMBRE_PATTERN, texto))
                .motivo(extraerMotivo(texto))
                .build();
    }

    private String unirTexto(CorreoDTO correo) {
        if (correo == null) {
            return "";
        }

        return ((correo.getAsunto() != null ? correo.getAsunto() : "") + "\n"
                + (correo.getDescripcion() != null ? correo.getDescripcion() : ""))
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String extraerPrimero(Pattern pattern, String texto) {
        Matcher matcher = pattern.matcher(texto);

        if (matcher.find()) {
            return limpiar(matcher.group(1));
        }

        return null;
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }

        return valor
                .replaceAll("\\s+", " ")
                .trim();
    }
    
    private String extraerSucursal(String asunto, String texto) {

        Pattern asuntoPattern = Pattern.compile("INC\\d+\\s*/\\s*(.+)", Pattern.CASE_INSENSITIVE);
        Matcher asuntoMatcher = asuntoPattern.matcher(asunto);

        if (asuntoMatcher.find()) {
            return limpiar(asuntoMatcher.group(1));
        }

        Pattern sucursalPattern = Pattern.compile("Sucursal:\\s*(.+?)(?:\\s+WS_|\\s+Cliente\\s+Único:|\\s+Cliente Unico:|$)", Pattern.CASE_INSENSITIVE);
        return extraerPrimero(sucursalPattern, texto);
    }

    private String extraerMotivo(String texto) {

        Pattern motivoInactividad = Pattern.compile("MOTIVO:\\s*(.+?)(?:\\s+\\d+\\s+\\d+\\s+\\d+|\\s+STATUS|\\s+PEDIDO|$)", Pattern.CASE_INSENSITIVE);
        String motivo = extraerPrimero(motivoInactividad, texto);

        if (motivo != null) {
            return motivo;
        }

        Pattern errorPattern = Pattern.compile("(Inconveniente con el API.+?)(?:\\s+Validaciones:|$)", Pattern.CASE_INSENSITIVE);
        return extraerPrimero(errorPattern, texto);
    }
}