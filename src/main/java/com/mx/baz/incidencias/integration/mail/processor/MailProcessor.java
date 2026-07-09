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

    public void procesar(CorreoDTO correo) {

        MailValidationResult validationResult = mailValidator.validar(correo);

        if (!validationResult.isValido()) {
            log.warn("Correo rechazado. idCorreo: {}, motivo: {}",
                    correo != null ? correo.getIdCorreo() : "SIN_ID",
                    validationResult.getMotivo());
            return;
        }

        CorreoDTO correoNormalizado = mailNormalizer.normalizar(correo);
        
        CorreoMetadata metadata = mailInformationExtractor.extraer(correoNormalizado);

        log.info("Metadata extraída - folio: {}, sucursal: {}, cliente: {}, CU: {}, motivo: {}",
                metadata.getFolio(),
                metadata.getSucursal(),
                metadata.getNombreCliente(),
                metadata.getClienteUnico(),
                metadata.getMotivo());

        if (correoProcesadoRepository.existsByIdCorreo(correoNormalizado.getIdCorreo())) {
            log.info("Correo ya procesado, se omite: {}", correoNormalizado.getIdCorreo());
            return;
        }

        IncidenciaRequest request = IncidenciaRequest.builder()
        		.folio(folioGenerator.generarDesdeCorreo(correoNormalizado.getAsunto()))
                .asunto(correoNormalizado.getAsunto())
                .remitente(correoNormalizado.getRemitente())
                .fechaCorreo(correoNormalizado.getFechaCorreo())
                .carpetaOrigen(correoNormalizado.getCarpetaOrigen())
                .prioridad(PrioridadIncidencia.MEDIA)
                .descripcion(correoNormalizado.getDescripcion())
                .build();

        IncidenciaResponse incidenciaCreada = incidenciaService.crearIncidencia(request);

        correoProcesadoRepository.save(
                CorreoProcesado.builder()
                        .idCorreo(correoNormalizado.getIdCorreo())
                        .folioIncidencia(incidenciaCreada.getFolio())
                        .build()
        );

        log.info("Correo procesado correctamente. idCorreo: {}, folio: {}",
                correoNormalizado.getIdCorreo(),
                incidenciaCreada.getFolio());
    }
}