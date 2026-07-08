package com.mx.baz.incidencias.integration.graph;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GraphAuthClient {

    private final GraphProperties graphProperties;

    private final RestClient restClient = RestClient.create();

    public String obtenerAccessToken() {

        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", graphProperties.getClientId());
        form.add("client_secret", graphProperties.getClientSecret());
        form.add("scope", graphProperties.getScope());
        form.add("grant_type", "client_credentials");

        GraphTokenResponse response = restClient.post()
                .uri(graphProperties.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GraphTokenResponse.class);

        if (response == null || response.getAccessToken() == null) {
            throw new IllegalStateException("No se pudo obtener token de Microsoft Graph");
        }

        return response.getAccessToken();
    }
}
