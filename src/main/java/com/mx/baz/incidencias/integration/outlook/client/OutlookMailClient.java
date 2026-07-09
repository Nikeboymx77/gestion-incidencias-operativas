package com.mx.baz.incidencias.integration.outlook.client;

import com.mx.baz.incidencias.integration.mail.client.MailClient;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnProperty(
        name = "mail.provider",
        havingValue = "outlook"
)
public class OutlookMailClient implements MailClient {

    @Override
    public List<CorreoDTO> obtenerCorreosPendientes() {

        System.out.println("Leyendo Outlook...");

        return Collections.emptyList();

    }

}