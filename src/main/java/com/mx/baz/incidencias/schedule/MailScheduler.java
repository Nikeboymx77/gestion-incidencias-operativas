package com.mx.baz.incidencias.schedule;

import com.mx.baz.incidencias.integration.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailScheduler {

    private final MailService mailService;

//    @Scheduled(fixedDelay = 60000)
    @Scheduled(fixedDelay = 15000)
    public void revisarCorreos() {

        log.info("========================================");
        log.info("Revisando correos pendientes...");
        log.info("========================================");

        mailService.procesarCorreosPendientes();

    }

}
