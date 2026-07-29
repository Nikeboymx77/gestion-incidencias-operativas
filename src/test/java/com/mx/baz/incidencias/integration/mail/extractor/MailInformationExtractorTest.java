package com.mx.baz.incidencias.integration.mail.extractor;

import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.mail.model.CorreoMetadata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MailInformationExtractorTest {

    private final MailInformationExtractor extractor =
            new MailInformationExtractor();

    @Test
    void debeExtraerMetadataDelCorreo() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("INC000022532663 / 9711 Mega Portal Durango")
                .descripcion("""
                        Buen día, solicito apoyo.

                        DATOS:
                        Sucursal: 9711 Mega Portal Durango
                        Cliente Único: 0107-09283-2985
                        Nombre: MARIA ANTONIA RESENDIZ HERRERA
                        Fecha de Nacimiento: 06/ENE/1960
                        Estado de Línea de Crédito: 3 Bloqueado
                        MOTIVO: INACTIVIDAD

                        Agradezco el apoyo.
                        """)
                .build();

        CorreoMetadata metadata = extractor.extraer(correo);

        assertEquals("INC000022532663", metadata.getFolio());
        assertEquals("9711 Mega Portal Durango", metadata.getSucursal());
        assertEquals("0107-09283-2985", metadata.getClienteUnico());
        assertEquals(
                "MARIA ANTONIA RESENDIZ HERRERA",
                metadata.getNombreCliente()
        );
        assertEquals("INACTIVIDAD", metadata.getMotivo());
    }

    @Test
    void debeObtenerSucursalDesdeAsuntoCuandoNoExisteEtiqueta() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("INC000024057662 / 9711 Mega Portal Durango")
                .descripcion("Correo sin etiqueta de sucursal.")
                .build();

        CorreoMetadata metadata = extractor.extraer(correo);

        assertEquals("INC000024057662", metadata.getFolio());
        assertEquals("9711 Mega Portal Durango", metadata.getSucursal());
    }
    
    @Test
    void debeExtraerMetadataDeCorreoEnUnaSolaLinea() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("INC000024057662 / Incidente originación")
                .descripcion("""
                        Buen día Un gusto en saludarlos compañeros de ACA De su amable apoyo en validar el siguiente incidente.
                        El problema se detona en proceso de originación de LCR, al llegar al apartado B Originación /1 Datos básicos
                        muestra la siguiente leyenda Mensaje reportado Lo sentimos Por políticas internas en este momento no es posible
                        otorgarle un crédito Información en Sistema Regional Información del cliente Nombre: RUBI ADRIANA CONTRERAS RIVAS
                        Cliente único: 0101-02168-9256-2 Sucursal: 1486 APATZINGAN CAYETANO ANDRADE Equipo: WS_VTAS06
                        """)
                .build();

        CorreoMetadata metadata = extractor.extraer(correo);

        assertEquals("RUBI ADRIANA CONTRERAS RIVAS", metadata.getNombreCliente());
        assertEquals("0101-02168-9256-2", metadata.getClienteUnico());
        assertEquals("1486 APATZINGAN CAYETANO ANDRADE", metadata.getSucursal());
        assertEquals("WS_VTAS06", metadata.getEquipo());
        assertEquals(
                "Lo sentimos Por políticas internas en este momento no es posible otorgarle un crédito",
                metadata.getMotivo()
        );
    }
    
    @Test
    void debeExtraerNombreYSucursalGestoraDeCorreoReenviado() {

        CorreoDTO correo = CorreoDTO.builder()
                .asunto("""
                        Re: MBC // INC000024108083 //
                        4815 MEGA LOS MOCHIS INDEPENDENCIA
                        """)
                .descripcion("""
                        Nombre: JUAN CARLOS GALAVIZ BRISEÑO
                        Sucursal gestora: 2197

                        Les agradezco su apoyo, buen día.

                        De: Angel Donaldo Manzano Bravo
                        Enviado: lunes, 13 de julio de 2026 16:57:23
                        Para: Mariano Blanco Cruz;
                        Cliente Expediente Electronico de Credito
                        Cc: Soporte Tecnico - Aplicativo 2° Nivel
                        Asunto: Re: MBC // INC000024108083

                        Buena tarde.
                        Se apoya con el desligue de la solicitud,
                        me apoyan a validarlo por favor.
                        """)
                .build();

        CorreoMetadata metadata = extractor.extraer(correo);

        assertEquals(
                "JUAN CARLOS GALAVIZ BRISEÑO",
                metadata.getNombreCliente()
        );

        assertEquals(
                "2197",
                metadata.getSucursal()
        );

        assertEquals(
                "INC000024108083",
                metadata.getFolio()
        );
    }
    
    @Test
    void debeExtraerSucursalAntesDelCampoCu() {

        CorreoDTO correo = new CorreoDTO();

        correo.setAsunto(
                "Apoyo con incidente INC000024168142"
        );

        correo.setDescripcion(
                """
                Buen día

                Solicito de su apoyo para validar un problema al generar
                una solicitud de crédito.

                Sucursal: 2111 Comonfort
                CU: 0101-02111-6368-4
                Nombre: MARIA DE LOURDES PUCHOTE SOTO

                Se valida que si da clic en Solicitar pero no le deja continuar.

                Gracias y Saludos.
                """
        );

        CorreoMetadata resultado =
        		extractor.extraer(correo);

        assertEquals(
                "2111 Comonfort",
                resultado.getSucursal()
        );
    }
    
    @Test
    void debeDetenerSucursalCuandoComienzaLaSolicitudDeApoyo() {

        CorreoDTO correo = new CorreoDTO();

        correo.setAsunto(
                "Incidente INC000024168143"
        );

        correo.setDescripcion(
                """
                Sucursal: 7582 MEGA CABO SAN LUCAS
                Se solicita de su apoyo para validar el incidente.
                """
        );

        CorreoMetadata resultado =
        		extractor.extraer(correo);

        assertEquals(
                "7582 MEGA CABO SAN LUCAS",
                resultado.getSucursal()
        );
    }
}