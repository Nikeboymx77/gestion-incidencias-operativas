package com.mx.baz.incidencias.integration.mail.processor;

import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.mail.dto.MailValidationResult;
import com.mx.baz.incidencias.integration.mail.repository.CorreoProcesadoRepository;
import com.mx.baz.incidencias.integration.mail.normalizer.MailNormalizer;
import com.mx.baz.incidencias.integration.mail.validator.MailValidator;
import com.mx.baz.incidencias.service.IncidenciaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailProcessorTest {

    @Mock
    private MailValidator mailValidator;

    @Mock
    private MailNormalizer mailNormalizer;

    @Mock
    private CorreoProcesadoRepository correoProcesadoRepository;

    @Mock
    private IncidenciaService incidenciaService;

    @InjectMocks
    private MailProcessor mailProcessor;

    @Test
    void debeIgnorarCorreoInvalido() {
        CorreoDTO correo = CorreoDTO.builder()
                .idCorreo("MAIL-1")
                .build();

        when(mailValidator.validar(correo))
                .thenReturn(MailValidationResult.invalido("Asunto obligatorio"));

        mailProcessor.procesar(correo);

        verify(mailNormalizer, never()).normalizar(any());
        verify(correoProcesadoRepository, never()).existsByIdCorreo(any());
        verify(incidenciaService, never()).crearIncidencia(any());
        verify(correoProcesadoRepository, never()).save(any());
    }

    @Test
    void debeIgnorarCorreoDuplicado() {
        CorreoDTO correo = crearCorreoValido();

        when(mailValidator.validar(correo))
                .thenReturn(MailValidationResult.valido());

        when(mailNormalizer.normalizar(correo))
                .thenReturn(correo);

        when(correoProcesadoRepository.existsByIdCorreo("MAIL-1"))
                .thenReturn(true);

        mailProcessor.procesar(correo);

        verify(incidenciaService, never()).crearIncidencia(any());
        verify(correoProcesadoRepository, never()).save(any());
    }

    @Test
    void debeCrearIncidenciaYRegistrarCorreoProcesado() {
        CorreoDTO correo = crearCorreoValido();

        when(mailValidator.validar(correo))
                .thenReturn(MailValidationResult.valido());

        when(mailNormalizer.normalizar(correo))
                .thenReturn(correo);

        when(correoProcesadoRepository.existsByIdCorreo("MAIL-1"))
                .thenReturn(false);

        IncidenciaResponse incidenciaResponse = IncidenciaResponse.builder()
                .folio("MAIL-1")
                .build();

        when(incidenciaService.crearIncidencia(any()))
                .thenReturn(incidenciaResponse);

        mailProcessor.procesar(correo);

        verify(incidenciaService, times(1)).crearIncidencia(any());
        verify(correoProcesadoRepository, times(1)).save(any());
    }

    private CorreoDTO crearCorreoValido() {
        return CorreoDTO.builder()
                .idCorreo("MAIL-1")
                .asunto("Error al liberar crédito")
                .remitente("sucursal123@empresa.com")
                .descripcion("No se pudo liberar el crédito del cliente.")
                .fechaCorreo(LocalDateTime.of(2026, 7, 7, 10, 15))
                .carpetaOrigen("Incidencias")
                .build();
    }
}