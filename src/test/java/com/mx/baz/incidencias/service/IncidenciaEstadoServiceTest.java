package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.enums.TipoSeguimiento;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import com.mx.baz.incidencias.events.IncidenciaReabiertaEvent;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class IncidenciaEstadoServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @Mock
    private HistorialIncidenciaService historialIncidenciaService;

    private IncidenciaEstadoService incidenciaEstadoService;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {

    	incidenciaEstadoService =
    	        new IncidenciaEstadoService(
    	                incidenciaRepository,
    	                historialIncidenciaService,
    	                eventPublisher
    	        );
    }

    @Test
    void debeReabrirIncidenciaResueltaCuandoPersisteError() {

        Incidencia incidencia = Incidencia.builder()
                .folio("INC000024108083")
                .estado(EstadoIncidencia.RESUELTA)
                .build();

        boolean resultado =
                incidenciaEstadoService.evaluarCambioEstado(
                        incidencia,
                        TipoSeguimiento.PERSISTE_ERROR
                );

        assertTrue(resultado);

        assertEquals(
                EstadoIncidencia.REABIERTA,
                incidencia.getEstado()
        );

        verify(incidenciaRepository)
                .save(incidencia);

        verify(historialIncidenciaService)
                .registrarCambioEstado(
                        incidencia,
                        EstadoIncidencia.RESUELTA,
                        EstadoIncidencia.REABIERTA,
                        "SISTEMA",
                        "Incidencia reabierta automáticamente "
                                + "por persistencia del error detectada en correo"
                );
        ArgumentCaptor<IncidenciaReabiertaEvent> eventCaptor =
                ArgumentCaptor.forClass(
                        IncidenciaReabiertaEvent.class
                );

        verify(eventPublisher)
                .publishEvent(eventCaptor.capture());

        IncidenciaReabiertaEvent eventoPublicado =
                eventCaptor.getValue();

        assertEquals(
                incidencia,
                eventoPublicado.getIncidencia()
        );

        assertEquals(
                "Incidencia reabierta automáticamente "
                        + "por persistencia del error detectada en correo",
                eventoPublicado.getMotivo()
        );
    }

    @Test
    void noDebeReabrirIncidenciaQueNoEstaResuelta() {

        Incidencia incidencia = Incidencia.builder()
                .folio("INC000024108083")
                .estado(EstadoIncidencia.EN_PROCESO)
                .build();

        boolean resultado =
                incidenciaEstadoService.evaluarCambioEstado(
                        incidencia,
                        TipoSeguimiento.PERSISTE_ERROR
                );

        assertFalse(resultado);

        assertEquals(
                EstadoIncidencia.EN_PROCESO,
                incidencia.getEstado()
        );

        verify(incidenciaRepository, never())
                .save(incidencia);

        verifyNoInteractions(
                historialIncidenciaService,
                eventPublisher
        );
    }

    @Test
    void noDebeReabrirIncidenciaResueltaPorRespuestaNormal() {

        Incidencia incidencia = Incidencia.builder()
                .folio("INC000024108083")
                .estado(EstadoIncidencia.RESUELTA)
                .build();

        boolean resultado =
                incidenciaEstadoService.evaluarCambioEstado(
                        incidencia,
                        TipoSeguimiento.RESPUESTA
                );

        assertFalse(resultado);

        assertEquals(
                EstadoIncidencia.RESUELTA,
                incidencia.getEstado()
        );

        verifyNoInteractions(
                incidenciaRepository,
                historialIncidenciaService,
                eventPublisher
        );
    }

    @Test
    void debeIgnorarIncidenciaNula() {

        boolean resultado =
                incidenciaEstadoService.evaluarCambioEstado(
                        null,
                        TipoSeguimiento.PERSISTE_ERROR
                );

        assertFalse(resultado);

        verifyNoInteractions(
                incidenciaRepository,
                historialIncidenciaService
        );
    }

    @Test
    void debeIgnorarTipoSeguimientoNulo() {

        Incidencia incidencia = Incidencia.builder()
                .folio("INC000024108083")
                .estado(EstadoIncidencia.RESUELTA)
                .build();

        boolean resultado =
                incidenciaEstadoService.evaluarCambioEstado(
                        incidencia,
                        null
                );

        assertFalse(resultado);

        verifyNoInteractions(
                incidenciaRepository,
                historialIncidenciaService
        );
    }
}