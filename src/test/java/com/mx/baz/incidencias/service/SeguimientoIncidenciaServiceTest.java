package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.entity.SeguimientoIncidencia;
import com.mx.baz.incidencias.enums.TipoSeguimiento;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import com.mx.baz.incidencias.repository.SeguimientoIncidenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeguimientoIncidenciaServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @Mock
    private SeguimientoIncidenciaRepository seguimientoRepository;

    @Mock
    private ClasificadorCorreoService clasificadorCorreoService;

    @Mock
    private IncidenciaEstadoService incidenciaEstadoService;

    private SeguimientoIncidenciaService seguimientoIncidenciaService;

    @BeforeEach
    void setUp() {

        seguimientoIncidenciaService =
                new SeguimientoIncidenciaService(
                        incidenciaRepository,
                        seguimientoRepository,
                        clasificadorCorreoService,
                        incidenciaEstadoService
                );
    }

    @Test
    void debeDevolverFalseCuandoElFolioEsNulo() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("Correo de prueba")
                .build();

        boolean resultado =
                seguimientoIncidenciaService.registrarSiExiste(
                        null,
                        correo
                );

        assertFalse(resultado);

        verifyNoInteractions(
                incidenciaRepository,
                seguimientoRepository,
                clasificadorCorreoService,
                incidenciaEstadoService
        );
    }

    @Test
    void debeDevolverFalseCuandoElFolioEstaVacio() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("Correo de prueba")
                .build();

        boolean resultado =
                seguimientoIncidenciaService.registrarSiExiste(
                        "   ",
                        correo
                );

        assertFalse(resultado);

        verifyNoInteractions(
                incidenciaRepository,
                seguimientoRepository,
                clasificadorCorreoService,
                incidenciaEstadoService
        );
    }

    @Test
    void debeDevolverFalseCuandoLaIncidenciaNoExiste() {

        String folio = "INC000024108083";

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("Re: " + folio)
                .build();

        when(incidenciaRepository.findByFolio(folio))
                .thenReturn(Optional.empty());

        boolean resultado =
                seguimientoIncidenciaService.registrarSiExiste(
                        folio,
                        correo
                );

        assertFalse(resultado);

        verify(incidenciaRepository)
                .findByFolio(folio);

        verifyNoInteractions(
                seguimientoRepository,
                clasificadorCorreoService,
                incidenciaEstadoService
        );
    }

    @Test
    void debeRegistrarRespuestaSinRequerirAtencion() {

        String folio = "INC000024108083";

        Incidencia incidencia = new Incidencia();

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("Re: " + folio)
                .remitente("usuario@correo.com")
                .descripcion("Se realizó el ajuste solicitado.")
                .build();

        when(incidenciaRepository.findByFolio(folio))
                .thenReturn(Optional.of(incidencia));

        when(clasificadorCorreoService.clasificar(correo))
                .thenReturn(TipoSeguimiento.RESPUESTA);

        boolean resultado =
                seguimientoIncidenciaService.registrarSiExiste(
                        folio,
                        correo
                );

        ArgumentCaptor<SeguimientoIncidencia> captor =
                ArgumentCaptor.forClass(
                        SeguimientoIncidencia.class
                );

        verify(seguimientoRepository)
                .save(captor.capture());

        SeguimientoIncidencia seguimientoGuardado =
                captor.getValue();

        assertTrue(resultado);

        assertEquals(
                TipoSeguimiento.RESPUESTA,
                seguimientoGuardado.getTipo()
        );

        assertFalse(
                seguimientoGuardado.isRequiereAtencion()
        );

        assertEquals(
                correo.getAsunto(),
                seguimientoGuardado.getAsunto()
        );

        assertEquals(
                correo.getDescripcion(),
                seguimientoGuardado.getContenido()
        );

        verify(incidenciaEstadoService)
                .evaluarCambioEstado(
                        incidencia,
                        TipoSeguimiento.RESPUESTA
                );
    }

    @Test
    void debeMarcarAtencionCuandoPersisteElError() {

        String folio = "INC000024108083";

        Incidencia incidencia = new Incidencia();

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("Re: " + folio)
                .descripcion("El problema persiste.")
                .build();

        when(incidenciaRepository.findByFolio(folio))
                .thenReturn(Optional.of(incidencia));

        when(clasificadorCorreoService.clasificar(correo))
                .thenReturn(TipoSeguimiento.PERSISTE_ERROR);

        boolean resultado =
                seguimientoIncidenciaService.registrarSiExiste(
                        folio,
                        correo
                );

        ArgumentCaptor<SeguimientoIncidencia> captor =
                ArgumentCaptor.forClass(
                        SeguimientoIncidencia.class
                );

        verify(seguimientoRepository)
                .save(captor.capture());

        SeguimientoIncidencia seguimientoGuardado =
                captor.getValue();

        assertTrue(resultado);

        assertEquals(
                TipoSeguimiento.PERSISTE_ERROR,
                seguimientoGuardado.getTipo()
        );

        assertTrue(
                seguimientoGuardado.isRequiereAtencion()
        );

        verify(incidenciaEstadoService)
                .evaluarCambioEstado(
                        incidencia,
                        TipoSeguimiento.PERSISTE_ERROR
                );
    }

    @Test
    void debeMarcarAtencionCuandoEsReasignacion() {

        String folio = "INC000024108083";

        Incidencia incidencia = new Incidencia();

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("Re: " + folio)
                .descripcion(
                        "Favor de apoyar con la validación."
                )
                .build();

        when(incidenciaRepository.findByFolio(folio))
                .thenReturn(Optional.of(incidencia));

        when(clasificadorCorreoService.clasificar(correo))
                .thenReturn(TipoSeguimiento.REASIGNACION);

        boolean resultado =
                seguimientoIncidenciaService.registrarSiExiste(
                        folio,
                        correo
                );

        ArgumentCaptor<SeguimientoIncidencia> captor =
                ArgumentCaptor.forClass(
                        SeguimientoIncidencia.class
                );

        verify(seguimientoRepository)
                .save(captor.capture());

        SeguimientoIncidencia seguimientoGuardado =
                captor.getValue();

        assertTrue(resultado);

        assertEquals(
                TipoSeguimiento.REASIGNACION,
                seguimientoGuardado.getTipo()
        );

        assertTrue(
                seguimientoGuardado.isRequiereAtencion()
        );

        verify(incidenciaEstadoService)
                .evaluarCambioEstado(
                        incidencia,
                        TipoSeguimiento.REASIGNACION
                );
    }
}