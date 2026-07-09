package com.mx.baz.incidencias.integration.pst.client;

import com.mx.baz.incidencias.integration.mail.client.MailClient;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.pst.config.PstProperties;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import com.mx.baz.incidencias.integration.mail.filter.CorreoFilter;
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "mail.provider",
        havingValue = "pst"
)
public class PstMailClient implements MailClient {

    private final PstProperties pstProperties;
    private final CorreoFilter correoFilter;
    
    @Override
    public List<CorreoDTO> obtenerCorreosPendientes() {

        List<CorreoDTO> correos = new ArrayList<>();

        try {
            File archivoPst = new File(pstProperties.getFilePath());

            if (!archivoPst.exists()) {
                log.error("No existe el archivo PST: {}", pstProperties.getFilePath());
                return correos;
            }

            PSTFile pstFile = new PSTFile(archivoPst);
            PSTFolder root = pstFile.getRootFolder();

            PSTFolder carpeta = buscarCarpeta(root, pstProperties.getFolderName());

            if (carpeta == null) {
                log.error("No se encontró la carpeta '{}' dentro del PST", pstProperties.getFolderName());
                return correos;
            }

            log.info("Carpeta PST encontrada: {}", carpeta.getDisplayName());

            PSTMessage mensaje;

            while ((mensaje = (PSTMessage) carpeta.getNextChild()) != null) {

                CorreoDTO correo = mapearMensaje(mensaje);

                if (!correoFilter.debeProcesarse(correo)) {
                    log.info("Correo PST omitido desde lectura: {}", correo.getIdCorreo());
                    continue;
                }

                correos.add(correo);

                if (correos.size() >= pstProperties.getMaxMails()) {
                    break;
                }
            }

            log.info("Correos leídos desde PST: {}", correos.size());

        } catch (Exception ex) {
            log.error("Error leyendo PST", ex);
        }

        return correos;
    }

    private PSTFolder buscarCarpeta(PSTFolder carpetaActual, String nombreBuscado) throws Exception {

        if (carpetaActual.getDisplayName() != null
                && carpetaActual.getDisplayName().equalsIgnoreCase(nombreBuscado)) {
            return carpetaActual;
        }

        if (carpetaActual.hasSubfolders()) {
            List<PSTFolder> subCarpetas = carpetaActual.getSubFolders();

            for (PSTFolder subCarpeta : subCarpetas) {
                PSTFolder encontrada = buscarCarpeta(subCarpeta, nombreBuscado);

                if (encontrada != null) {
                    return encontrada;
                }
            }
        }

        return null;
    }

    private CorreoDTO mapearMensaje(PSTMessage mensaje) {

        LocalDateTime fechaCorreo = null;

        if (mensaje.getMessageDeliveryTime() != null) {
            fechaCorreo = mensaje.getMessageDeliveryTime()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        String idCorreo = mensaje.getInternetMessageId();

        if (idCorreo == null || idCorreo.isBlank()) {
            idCorreo = "PST-" + Math.abs((mensaje.getSubject() + fechaCorreo).hashCode());
        }

        return CorreoDTO.builder()
                .idCorreo(idCorreo)
                .asunto(mensaje.getSubject())
                .remitente(mensaje.getSenderEmailAddress())
                .descripcion(obtenerDescripcion(mensaje))
                .fechaCorreo(fechaCorreo)
                .carpetaOrigen(pstProperties.getFolderName())
                .build();
    }
    
    private String obtenerDescripcion(PSTMessage mensaje) {

        String body = mensaje.getBody();

        if (body != null && !body.isBlank()) {
            return body;
        }

        String htmlBody = mensaje.getBodyHTML();

        if (htmlBody != null && !htmlBody.isBlank()) {
            return htmlBody
                    .replaceAll("<[^>]*>", " ")
                    .replaceAll("&nbsp;", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
        }

        return "Sin descripción disponible";
    }
}
