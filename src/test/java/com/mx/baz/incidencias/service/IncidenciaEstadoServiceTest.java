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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IncidenciaEstadoServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    private IncidenciaEstadoService incidenciaEstadoService;

    @BeforeEach
    void setUp() {

        incidenciaEstadoService =
                new IncidenciaEstadoService(
                        incidenciaRepository
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

        verify(incidenciaRepository, never())
                .save(incidencia);
    }

    @Test
    void debeIgnorarIncidenciaNula() {

        boolean resultado =
                incidenciaEstadoService.evaluarCambioEstado(
                        null,
                        TipoSeguimiento.PERSISTE_ERROR
                );

        assertFalse(resultado);

        verify(incidenciaRepository, never())
                .save(
                        org.mockito.ArgumentMatchers.any()
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

        verify(incidenciaRepository, never())
                .save(
                        org.mockito.ArgumentMatchers.any()
                );
    }
}
