package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.enums.TipoSeguimiento;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClasificadorCorreoServiceTest {

    private final ClasificadorCorreoService clasificador =
            new ClasificadorCorreoService();

    @Test
    void debeClasificarCorreoComoReasignacion() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto(
                        "Re: MBC // INC000024108083 // " +
                        "4815 MEGA LOS MOCHIS INDEPENDENCIA"
                )
                .descripcion("""
                        Buena tarde.

                        Se apoya con el desligue de la solicitud,
                        me apoyan a validarlo por favor.

                        Quedo atento.
                        Saludos.
                        """)
                .build();

        TipoSeguimiento resultado =
                clasificador.clasificar(correo);

        assertEquals(
                TipoSeguimiento.REASIGNACION,
                resultado
        );
    }

    @Test
    void debeClasificarCorreoNormalComoRespuesta() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto(
                        "Re: INC000024108083"
                )
                .descripcion("""
                        Buen día.

                        Se realizó el ajuste solicitado.
                        Favor de confirmar el resultado.

                        Saludos.
                        """)
                .build();

        TipoSeguimiento resultado =
                clasificador.clasificar(correo);

        assertEquals(
                TipoSeguimiento.RESPUESTA,
                resultado
        );
    }

    @Test
    void debeClasificarCorreoNuloComoRespuesta() {

        TipoSeguimiento resultado =
                clasificador.clasificar(null);

        assertEquals(
                TipoSeguimiento.RESPUESTA,
                resultado
        );
    }

    @Test
    void debeReconocerReasignacionAunqueTengaAcentosYMayusculas() {

        CorreoDTO correo = CorreoDTO.builder()
                .descripcion("""
                        SE REASIGNA PARA SU ATENCIÓN.
                        Favor de apoyar con la validación.
                        """)
                .build();

        TipoSeguimiento resultado =
                clasificador.clasificar(correo);

        assertEquals(
                TipoSeguimiento.REASIGNACION,
                resultado
        );
    }
    
    @Test
    void debeClasificarCorreoComoPersisteError() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("Re: INC000024108083")
                .descripcion("""
                        Buen día.

                        Se realizó nuevamente la prueba,
                        pero el problema persiste.

                        Favor de apoyarnos.
                        """)
                .build();

        TipoSeguimiento resultado =
                clasificador.clasificar(correo);

        assertEquals(
                TipoSeguimiento.PERSISTE_ERROR,
                resultado
        );
    }
    
    @Test
    void debeReconocerPersistenciaAunqueTengaAcentosYMayusculas() {

        CorreoDTO correo = CorreoDTO.builder()
                .descripcion("""
                        BUEN DÍA.

                        AÚN PRESENTA LA FALLA
                        Y SIGUE SIN FUNCIONAR.
                        """)
                .build();

        TipoSeguimiento resultado =
                clasificador.clasificar(correo);

        assertEquals(
                TipoSeguimiento.PERSISTE_ERROR,
                resultado
        );
    }
    
    @Test
    void debeDetectarPersistenciaDesdeElAsunto() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto(
                        "Re: INC000024108083 - Sigue con el mismo error"
                )
                .descripcion(
                        "Favor de apoyar con la validación."
                )
                .build();

        TipoSeguimiento resultado =
                clasificador.clasificar(correo);

        assertEquals(
                TipoSeguimiento.PERSISTE_ERROR,
                resultado
        );
    }
    
    @Test
    void debePriorizarPersisteErrorSobreReasignacion() {

        CorreoDTO correo = CorreoDTO.builder()
                .descripcion("""
                        Buen día.

                        El problema persiste.
                        Favor de apoyar con la validación.

                        Saludos.
                        """)
                .build();

        TipoSeguimiento resultado =
                clasificador.clasificar(correo);

        assertEquals(
                TipoSeguimiento.PERSISTE_ERROR,
                resultado
        );
    }
}
