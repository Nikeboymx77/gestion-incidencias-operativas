package com.mx.baz.incidencias.integration.mail.service;

import com.mx.baz.incidencias.dto.IncidenciaRequest;
import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.enums.PrioridadIncidencia;
import com.mx.baz.incidencias.integration.mail.client.MailClient;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.mail.entity.CorreoProcesado;
import com.mx.baz.incidencias.integration.mail.repository.CorreoProcesadoRepository;
import com.mx.baz.incidencias.service.IncidenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailClient mailClient;
    private final IncidenciaService incidenciaService;
    private final CorreoProcesadoRepository correoProcesadoRepository;

    @Transactional
    public void procesarCorreosPendientes() {

        List<CorreoDTO> correos = mailClient.obtenerCorreosPendientes();

        log.info("Correos pendientes encontrados: {}", correos.size());

        correos.forEach(correo -> {
        	
        	if (correoProcesadoRepository.existsByIdCorreo(correo.getIdCorreo())) {
                log.info("Correo ya procesado, se omite: {}", correo.getIdCorreo());
                return;
            }
        	
            IncidenciaRequest request = IncidenciaRequest.builder()
            		.folio(correo.getIdCorreo())
                    .asunto(correo.getAsunto())
                    .remitente(correo.getRemitente())
                    .fechaCorreo(correo.getFechaCorreo())
                    .carpetaOrigen(correo.getCarpetaOrigen())
                    .prioridad(PrioridadIncidencia.MEDIA)
                    .descripcion(correo.getDescripcion())
                    .build();           
            
            
            IncidenciaResponse incidenciaCreada = incidenciaService.crearIncidencia(request);
            
            

            correoProcesadoRepository.save(
                    CorreoProcesado.builder()
                            .idCorreo(correo.getIdCorreo())
                            .folioIncidencia(incidenciaCreada.getFolio())
                            .build()
            );
        });
    }

    private String generarFolio() {
        return "MAIL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
