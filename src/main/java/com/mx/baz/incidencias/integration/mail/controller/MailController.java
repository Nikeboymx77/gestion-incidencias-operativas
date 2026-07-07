package com.mx.baz.incidencias.integration.mail.controller;

import com.mx.baz.incidencias.integration.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/procesar")
    public String procesarCorreos() {
        mailService.procesarCorreosPendientes();
        return "Correos procesados correctamente";
    }
}