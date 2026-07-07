package com.mx.baz.incidencias.integration.mail.service;

import com.mx.baz.incidencias.integration.mail.client.MailClient;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.mail.processor.MailProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailClient mailClient;
    private final MailProcessor mailProcessor;

    @Transactional
    public void procesarCorreosPendientes() {

        List<CorreoDTO> correos = mailClient.obtenerCorreosPendientes();

        log.info("Correos pendientes encontrados: {}", correos.size());

        correos.forEach(mailProcessor::procesar);
    }
}