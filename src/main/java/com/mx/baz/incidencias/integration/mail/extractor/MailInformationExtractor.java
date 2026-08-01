package com.mx.baz.incidencias.integration.mail.extractor;

import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.mail.model.CorreoMetadata;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class MailInformationExtractor {
	
	private static final int REGEX_FLAGS =
	        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

    private static final Pattern FOLIO_PATTERN =
            Pattern.compile("\\b(INC\\s*0*\\d+)\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern CLIENTE_UNICO_PATTERN =
            Pattern.compile(
                    "Cliente\\s+[ÚUúu]nico\\s*:\\s*([0-9\\-]+)",
                    REGEX_FLAGS
            );

    private static final Pattern NOMBRE_CLIENTE_PATTERN =
            Pattern.compile(
                    "Nombre\\s*:\\s*(.+?)"
                            + "(?=\\s+Cliente\\s+[ÚU]nico\\s*:"
                            + "|\\s+CU\\s*:"
                            + "|\\s+Fecha\\s+de\\s+Nacimiento\\s*:"
                            + "|\\s+Estado\\s+de\\s+L[ií]nea"
                            + "|\\s+Sucursal(?:\\s+gestora)?\\s*:"
                            + "|\\s+Equipo\\s*:"
                            + "|\\s+MOTIVO\\s*:"
                            + "|\\s+De\\s*:"
                            + "|\\s+Enviado\\s*:"
                            + "|\\s+Para\\s*:"
                            + "|\\s+Cc\\s*:"
                            + "|\\s+CC\\s*:"
                            + "|\\s+Asunto\\s*:"
                            + "|\\s+Versión\\s*:"
                            + "|\\s+Versi[oó]n(?:\\s+aplicaci[oó]n)?\\s*:"
                            + "|\\s+fi[A-Za-z0-9_]+\\s*"
                            + "|\\s+Agradezco\\s+su"
                            + "|\\s+Cliente\\s+con"
                            + "|\\s+Se\\s+valida\\b"
                            + "|\\s+Se\\s+solicita\\b"
                            + "|\\s+Solicito\\b"
                            + "|\\s+Les\\s+agradezco\\b"
                            + "|\\s+Agradezco\\b"
                            + "|\\s+Gracias\\b"
                            + "|\\s+Buen\\s+d[ií]a\\b"
                            + "|\\s+Buena\\s+tarde\\b"
                            + "|\\s+Quedo\\s+(?:atento|pendiente)\\b"
                            + "|\\s+Saludos\\b"
                            + "|$)",
                    REGEX_FLAGS
            );

    private static final Pattern SUCURSAL_ETIQUETA_PATTERN =
            Pattern.compile(
                    "Sucursal(?:\\s+gestora)?\\s*:\\s*(.+?)"
                            + "(?=\\s+CU\\s*:"
                            + "|\\s+Equipo\\s*:"
                            + "|\\s+Cliente\\s+[ÚU]nico\\s*:"
                            + "|\\s+Nombre\\s*:"
                            + "|\\s+Fecha\\s+de\\s+Nacimiento"
                            + "|\\s+MOTIVO\\s*:"
                            + "|\\s+Se\\s+valida\\b"
                            + "|\\s+Se\\s+solicita\\b"
                            + "|\\s+Solicito\\b"
                            + "|\\s+Les\\s+agradezco\\b"
                            + "|\\s+Agradezco\\b"
                            + "|\\s+Gracias\\b"
                            + "|\\s+Buen\\s+d[ií]a\\b"
                            + "|\\s+Buena\\s+tarde\\b"
                            + "|\\s+Quedo\\s+(?:atento|pendiente)\\b"
                            + "|\\s+Saludos\\b"
                            + "|\\s+De\\s*:"
                            + "|\\s+Enviado\\s*:"
                            + "|\\s+Para\\s*:"
                            + "|\\s+Cc\\s*:"
                            + "|\\s+CC\\s*:"
                            + "|\\s+Asunto\\s*:"
                            + "|\\s+fi[A-Za-z0-9_]+\\s*"
                            + "|$)",
                    REGEX_FLAGS
            );

    private static final Pattern SUCURSAL_ASUNTO_PATTERN =
            Pattern.compile(
                    "\\bINC\\s*0*\\d+\\s*/\\s*(.+)$",
                    Pattern.CASE_INSENSITIVE
            );

    private static final Pattern MOTIVO_ETIQUETA_PATTERN =
            Pattern.compile(
                    "MOTIVO\\s*:?\\s*(.+?)"
                            + "(?=\\s+No\\.\\s*Registros"
                            + "|\\s+STATUS\\s+LCR"
                            + "|\\s+PEDIDO\\s*:"
                            + "|\\s+DATOS\\s*:"
                            + "|\\s+Agradezco\\b"
                            + "|\\s+Quedo\\s+(?:atento|pendiente)\\b"
                            + "|\\s+Saludos\\b"
                            + "|\\s+De\\s*:"
                            + "|\\s+Enviado\\s*:"
                            + "|\\s+Para\\s*:"
                            + "|\\s+Cc\\s*:"
                            + "|\\s+CC\\s*:"
                            + "|\\s+Asunto\\s*:"
                            + "|\\s+fi[A-Za-z0-9_]+\\s*"
                            + "|$)",
                    REGEX_FLAGS
            );
    
    private static final Pattern ERRORES_REACTIVACION_PATTERN =
            Pattern.compile(
                    "(?:1\\.-\\s*Error\\s*)?"
                            + "((?:Lo\\s+sentimos\\s+)?"
                            + "Inconveniente\\s+con\\s+el\\s+API.+?)"
                            + "(?=\\s+Se\\s+pide\\s+de\\s+su\\s+apoyo"
                            + "|\\s+Información\\s+del\\s+cliente"
                            + "|\\s+Nombre\\s*:"
                            + "|\\s+Cliente\\s+[ÚUúu]nico\\s*:"
                            + "|\\s+De\\s*:"
                            + "|\\s+Enviado\\s*:"
                            + "|\\s+Para\\s*:"
                            + "|\\s+CC\\s*:"
                            + "|\\s+Asunto\\s*:"
                            + "|$)",
                    REGEX_FLAGS
            );

    private static final Pattern ERROR_PATTERN =
            Pattern.compile(
                    "(Inconveniente\\s+con\\s+el\\s+API.+?)"
                            + "(?=\\s+Validaciones\\s*:"
                            + "|\\s+Se\\s+consulta"
                            + "|\\s+Se\\s+pide\\s+de\\s+su\\s+apoyo"
                            + "|\\s+Información\\s+del\\s+cliente"
                            + "|\\s+Nombre\\s*:"
                            + "|\\s+Cliente\\s+[ÚUúu]nico\\s*:"
                            + "|\\s+De\\s*:"
                            + "|\\s+Enviado\\s*:"
                            + "|\\s+Para\\s*:"
                            + "|\\s+CC\\s*:"
                            + "|\\s+Asunto\\s*:"
                            + "|$)",
                    REGEX_FLAGS
            );
    
    private static final Pattern EQUIPO_PATTERN =
            Pattern.compile(
                    "Equipo\\s*:\\s*([^\\s]+)",
                    Pattern.CASE_INSENSITIVE
            );
    
    private static final Pattern MENSAJE_REPORTADO_PATTERN =
            Pattern.compile(
                    "Mensaje\\s+reportado\\s+(.+?)(?=\\s+Información\\s+en\\s+Sistema|\\s+Informacion\\s+en\\s+Sistema|$)",
                    Pattern.CASE_INSENSITIVE
            );

    public CorreoMetadata extraer(CorreoDTO correo) {

        if (correo == null) {
            return CorreoMetadata.builder().build();
        }

        String asunto = normalizarTexto(correo.getAsunto());
        String descripcion = normalizarTexto(correo.getDescripcion());
        String textoCompleto = normalizarTexto(asunto + " " + descripcion);

        return CorreoMetadata.builder()
                .folio(
                        limitarLongitud(
                                "folio",
                                extraerPrimero(
                                        FOLIO_PATTERN,
                                        textoCompleto
                                ),
                                MAX_FOLIO
                        )
                )
                .sucursal(
                        limitarLongitud(
                                "sucursal",
                                extraerSucursal(
                                        asunto,
                                        textoCompleto
                                ),
                                MAX_SUCURSAL
                        )
                )
                .clienteUnico(
                        limitarLongitud(
                                "clienteUnico",
                                extraerPrimero(
                                        CLIENTE_UNICO_PATTERN,
                                        textoCompleto
                                ),
                                MAX_CLIENTE_UNICO
                        )
                )
                .nombreCliente(
                        limitarLongitud(
                                "nombreCliente",
                                extraerPrimero(
                                        NOMBRE_CLIENTE_PATTERN,
                                        textoCompleto
                                ),
                                MAX_NOMBRE_CLIENTE
                        )
                )
                .motivo(
                        limitarLongitud(
                                "motivo",
                                extraerMotivo(textoCompleto),
                                MAX_LONGITUD_MOTIVO
                        )
                )
                .equipo(
                        limitarLongitud(
                                "equipo",
                                extraerPrimero(
                                        EQUIPO_PATTERN,
                                        textoCompleto
                                ),
                                MAX_EQUIPO
                        )
                )
                .build();
    }

    private String extraerSucursal(String asunto, String textoCompleto) {

        String sucursal = extraerPrimero(SUCURSAL_ETIQUETA_PATTERN, textoCompleto);

        if (sucursal != null) {
            return sucursal;
        }

        return extraerPrimero(SUCURSAL_ASUNTO_PATTERN, asunto);
    }

    private String extraerMotivo(String textoCompleto) {

        String motivo = extraerPrimero(
                MOTIVO_ETIQUETA_PATTERN,
                textoCompleto
        );

        if (motivo != null) {
            return motivo;
        }

        motivo = extraerPrimero(
                MENSAJE_REPORTADO_PATTERN,
                textoCompleto
        );

        if (motivo != null) {
            return motivo;
        }

        motivo = extraerPrimero(
                ERRORES_REACTIVACION_PATTERN,
                textoCompleto
        );

        if (motivo != null) {
            return motivo;
        }

        return extraerPrimero(
                ERROR_PATTERN,
                textoCompleto
        );
    }

    private String extraerPrimero(Pattern pattern, String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }
        

        Matcher matcher = pattern.matcher(texto);

        if (!matcher.find()) {
            return null;
        }

        return limpiarValor(matcher.group(1));
    }

    private String normalizarTexto(String texto) {

        if (texto == null) {
            return "";
        }

        return texto
                .replace('\u00A0', ' ')
                .replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String limpiarValor(String valor) {

        if (valor == null) {
            return null;
        }

        String limpio = valor
                .replaceAll("\\s+", " ")
                .replaceAll("^[\\-:/\\s]+", "")
                .replaceAll("[\\-:/\\s]+$", "")
                .trim();

        return limpio.isBlank() ? null : limpio;
    }
    
    private static final int MAX_LONGITUD_MOTIVO = 1000;
    private static final int MAX_FOLIO = 255;
    private static final int MAX_SUCURSAL = 255;
    private static final int MAX_CLIENTE_UNICO = 255;
    private static final int MAX_NOMBRE_CLIENTE = 255;
        private static final int MAX_EQUIPO = 255;

        
    private String limitarLongitud(
            String campo,
            String valor,
            int longitudMaxima
    ) {

        if (valor == null) {
            return null;
        }

        String limpio = limpiarValor(valor);

        if (limpio == null) {
            return null;
        }

        if (limpio.length() <= longitudMaxima) {
            return limpio;
        }

        log.warn(
                "El campo [{}] excede la longitud máxima. "
                        + "Longitud original: {}, máximo: {}, valor: [{}]",
                campo,
                limpio.length(),
                longitudMaxima,
                limpio
        );

        return limpio.substring(0, longitudMaxima);
    }
}