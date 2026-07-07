package com.mx.baz.incidencias.integration.mail.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockMailClient implements MailClient {

    private final ObjectMapper objectMapper;

    @Override
    public List<CorreoDTO> obtenerCorreosPendientes() {
        try {
            ClassPathResource resource = new ClassPathResource("mock/correos.json");

            try (InputStream inputStream = resource.getInputStream()) {
                List<CorreoDTO> correos = objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<CorreoDTO>>() {}
                );

                log.info("Correos mock leídos desde JSON: {}", correos.size());

                return correos;
            }

        } catch (Exception e) {
            log.error("Error leyendo correos mock desde JSON", e);
            return List.of();
        }
    }
}