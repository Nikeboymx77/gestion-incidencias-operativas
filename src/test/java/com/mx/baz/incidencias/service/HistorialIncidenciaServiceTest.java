package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.entity.HistorialIncidencia;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.repository.HistorialIncidenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistorialIncidenciaServiceTest {

    @Mock
    private HistorialIncidenciaRepository historialIncidenciaRepository;

    private HistorialIncidenciaService historialIncidenciaService;

    @BeforeEach
    void setUp() {

        historialIncidenciaService =
                new HistorialIncidenciaService(
                        historialIncidenciaRepository
                );
    }

    @Test
    void debeRegistrarCambioEstado() {

        Incidencia incidencia = Incidencia.builder()
                .id(1L)
                .folio("INC000024108083")
                .estado(EstadoIncidencia.REABIERTA)
                .build();

        when(historialIncidenciaRepository.save(
                org.mockito.ArgumentMatchers.any(
                        HistorialIncidencia.class
                )
        )).thenAnswer(invocation -> invocation.getArgument(0));

        HistorialIncidencia resultado =
                historialIncidenciaService.registrarCambioEstado(
                        incidencia,
                        EstadoIncidencia.RESUELTA,
                        EstadoIncidencia.REABIERTA,
                        "SISTEMA",
                        "Incidencia reabierta automáticamente"
                );

        ArgumentCaptor<HistorialIncidencia> captor =
                ArgumentCaptor.forClass(
                        HistorialIncidencia.class
                );

        verify(historialIncidenciaRepository)
                .save(captor.capture());

        HistorialIncidencia historialGuardado =
                captor.getValue();

        assertEquals(
                incidencia,
                historialGuardado.getIncidencia()
        );

        assertEquals(
                "REABIERTA",
                historialGuardado.getAccion()
        );

        assertEquals(
                "SISTEMA",
                historialGuardado.getUsuario()
        );

        assertEquals(
                "Incidencia reabierta automáticamente",
                historialGuardado.getComentario()
        );

        assertEquals(
                historialGuardado,
                resultado
        );
    }

    @Test
    void debeUsarSistemaCuandoUsuarioEsNulo() {

        Incidencia incidencia = Incidencia.builder()
                .folio("INC000024108083")
                .build();

        when(historialIncidenciaRepository.save(
                org.mockito.ArgumentMatchers.any(
                        HistorialIncidencia.class
                )
        )).thenAnswer(invocation -> invocation.getArgument(0));

        HistorialIncidencia resultado =
                historialIncidenciaService.registrarCambioEstado(
                        incidencia,
                        EstadoIncidencia.RESUELTA,
                        EstadoIncidencia.REABIERTA,
                        null,
                        "Reapertura automática"
                );

        assertEquals(
                "SISTEMA",
                resultado.getUsuario()
        );
    }

    @Test
    void debeConstruirComentarioCuandoEstaVacio() {

        Incidencia incidencia = Incidencia.builder()
                .folio("INC000024108083")
                .build();

        when(historialIncidenciaRepository.save(
                org.mockito.ArgumentMatchers.any(
                        HistorialIncidencia.class
                )
        )).thenAnswer(invocation -> invocation.getArgument(0));

        HistorialIncidencia resultado =
                historialIncidenciaService.registrarCambioEstado(
                        incidencia,
                        EstadoIncidencia.RESUELTA,
                        EstadoIncidencia.REABIERTA,
                        "SISTEMA",
                        " "
                );

        assertEquals(
                "Cambio de estado de RESUELTA a REABIERTA",
                resultado.getComentario()
        );
    }

    @Test
    void debeLanzarExcepcionCuandoIncidenciaEsNula() {

        assertThrows(
                IllegalArgumentException.class,
                () -> historialIncidenciaService
                        .registrarCambioEstado(
                                null,
                                EstadoIncidencia.RESUELTA,
                                EstadoIncidencia.REABIERTA,
                                "SISTEMA",
                                "Reapertura automática"
                        )
        );
    }

    @Test
    void debeLanzarExcepcionCuandoEstadoAnteriorEsNulo() {

        Incidencia incidencia = Incidencia.builder()
                .folio("INC000024108083")
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> historialIncidenciaService
                        .registrarCambioEstado(
                                incidencia,
                                null,
                                EstadoIncidencia.REABIERTA,
                                "SISTEMA",
                                "Reapertura automática"
                        )
        );
    }
}
