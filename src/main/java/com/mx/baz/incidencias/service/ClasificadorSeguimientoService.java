package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.enums.TipoSeguimiento;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
public class ClasificadorSeguimientoService {

    private static final List<String> FRASES_PERSISTE_ERROR = List.of(
        "continua el error",
        "continua presentando el error",
        "persiste el error",
        "persiste el problema",
        "sigue presentando el error",
        "sigue con el mismo error",
        "sigue sin funcionar",
        "no quedo solucionado",
        "no se soluciono",
        "el problema continua",
        "se volvio a intentar y continua",
        "se realizo nuevamente y presenta error",
        "favor de validar nuevamente",
        "nos apoyan a revisar nuevamente"
    );

    public TipoSeguimiento clasificar(
            String asunto,
            String contenido) {

        String texto = normalizar(
            valorSeguro(asunto) + " " + valorSeguro(contenido)
        );

        boolean persisteError = FRASES_PERSISTE_ERROR.stream()
                .anyMatch(texto::contains);

        if (persisteError) {
            return TipoSeguimiento.PERSISTE_ERROR;
        }

        return TipoSeguimiento.RESPUESTA;
    }

    private String normalizar(String texto) {
        String normalizado = Normalizer.normalize(
            texto.toLowerCase(),
            Normalizer.Form.NFD
        );

        return normalizado
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }
}
