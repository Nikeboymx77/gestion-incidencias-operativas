package com.mx.baz.incidencias.integration.mail.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FolioGenerator {

    private static final Pattern INC_PATTERN = Pattern.compile("(INC\\d+)", Pattern.CASE_INSENSITIVE);

    public String generarDesdeCorreo(String asunto) {

        if (asunto != null) {
            Matcher matcher = INC_PATTERN.matcher(asunto);

            if (matcher.find()) {
                return matcher.group(1).toUpperCase();
            }
        }

        return generarTemporal();
    }

    private String generarTemporal() {
        return "SGIO-"
                + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}