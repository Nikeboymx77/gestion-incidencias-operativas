package com.mx.baz.incidencias.integration.mail.processor;

import com.mx.baz.incidencias.dto.IncidenciaRequest;
import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.enums.PrioridadIncidencia;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.mail.dto.MailValidationResult;
import com.mx.baz.incidencias.integration.mail.entity.CorreoProcesado;
import com.mx.baz.incidencias.integration.mail.normalizer.MailNormalizer;
import com.mx.baz.incidencias.integration.mail.repository.CorreoProcesadoRepository;
import com.mx.baz.incidencias.integration.mail.validator.MailValidator;
import com.mx.baz.incidencias.service.IncidenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.mx.baz.incidencias.integration.mail.generator.FolioGenerator;
import com.mx.baz.incidencias.integration.mail.extractor.MailInformationExtractor;
import com.mx.baz.incidencias.integration.mail.model.CorreoMetadata;
import com.mx.baz.incidencias.integration.mail.model.MetadataValidationResult;
import com.mx.baz.incidencias.integration.mail.validator.MetadataValidator;
import com.mx.baz.incidencias.service.SeguimientoIncidenciaService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailProcessor {

    private final MailValidator mailValidator;
    private final MailNormalizer mailNormalizer;
    private final CorreoProcesadoRepository correoProcesadoRepository;
    private final IncidenciaService incidenciaService;
    private final FolioGenerator folioGenerator;
    private final MailInformationExtractor mailInformationExtractor;
    private final MetadataValidator metadataValidator;
    private final SeguimientoIncidenciaService seguimientoIncidenciaService;

    public void procesar(CorreoDTO correo) {

        MailValidationResult validationResult = mailValidator.validar(correo);

        if (!validationResult.isValido()) {
            log.warn(
                    "Correo rechazado. idCorreo: {}, motivo: {}",
                    correo != null ? correo.getIdCorreo() : "SIN_ID",
                    validationResult.getMotivo()
            );
            return;
        }

        CorreoDTO correoNormalizado = mailNormalizer.normalizar(correo);

        /*
         * Primero verificamos si este correo específico ya fue procesado.
         * Esto evita volver a procesar exactamente el mismo mensaje.
         */
        if (correoProcesadoRepository.existsByIdCorreo(
                correoNormalizado.getIdCorreo())) {

            log.info(
                    "Correo ya procesado, se omite: {}",
                    correoNormalizado.getIdCorreo()
            );
            return;
        }

        CorreoMetadata metadata =
                mailInformationExtractor.extraer(correoNormalizado);

        MetadataValidationResult validation =
                metadataValidator.validar(metadata);

        if (!validation.isCompleta()) {
            log.warn(
                    "Metadata incompleta para correo {}. Campos faltantes: {}",
                    correoNormalizado.getIdCorreo(),
                    validation.getCamposFaltantes()
            );
        }

        log.info(
                "Metadata extraída - folio: {}, sucursal: {}, cliente: {}, CU: {}, motivo: {}",
                metadata.getFolio(),
                metadata.getSucursal(),
                metadata.getNombreCliente(),
                metadata.getClienteUnico(),
                metadata.getMotivo()
        );

        /*
         * Generamos o extraemos el folio una sola vez.
         * Este mismo folio se utiliza para buscar y, si es necesario,
         * crear la incidencia.
         */
        String folio = folioGenerator.generarDesdeCorreo(
                correoNormalizado.getAsunto()
        );

        /*
         * Si el folio ya existe, el correo se registra como seguimiento.
         */
        boolean registradoComoSeguimiento =
                seguimientoIncidenciaService.registrarSiExiste(
                        folio,
                        correoNormalizado
                );

        if (registradoComoSeguimiento) {

            /*
             * También marcamos el correo como procesado.
             * De lo contrario, el scheduler intentaría guardarlo
             * como seguimiento nuevamente.
             */
            correoProcesadoRepository.save(
                    CorreoProcesado.builder()
                            .idCorreo(correoNormalizado.getIdCorreo())
                            .folioIncidencia(folio)
                            .build()
            );

            log.info(
                    "Correo procesado como seguimiento. idCorreo: {}, folio: {}",
                    correoNormalizado.getIdCorreo(),
                    folio
            );

            return;
        }

        /*
         * Si el folio no existe, seguimos con el flujo normal
         * de creación de una incidencia.
         */
        IncidenciaRequest request = IncidenciaRequest.builder()
                .folio(folio)
                .asunto(correoNormalizado.getAsunto())
                .remitente(correoNormalizado.getRemitente())
                .fechaCorreo(correoNormalizado.getFechaCorreo())
                .carpetaOrigen(correoNormalizado.getCarpetaOrigen())
                .prioridad(PrioridadIncidencia.MEDIA)
                .descripcion(correoNormalizado.getDescripcion())
                .sucursal(metadata.getSucursal())
                .clienteUnico(metadata.getClienteUnico())
                .nombreCliente(metadata.getNombreCliente())
                .equipo(metadata.getEquipo())
                .motivo(metadata.getMotivo())
                .build();

        IncidenciaResponse incidenciaCreada =
                incidenciaService.crearIncidencia(request);

        correoProcesadoRepository.save(
                CorreoProcesado.builder()
                        .idCorreo(correoNormalizado.getIdCorreo())
                        .folioIncidencia(incidenciaCreada.getFolio())
                        .build()
        );

        log.info(
                "Correo procesado correctamente. idCorreo: {}, folio: {}",
                correoNormalizado.getIdCorreo(),
                incidenciaCreada.getFolio()
        );
    }
}