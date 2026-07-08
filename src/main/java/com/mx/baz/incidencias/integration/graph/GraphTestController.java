package com.mx.baz.incidencias.integration.graph;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
public class GraphTestController {

    private final GraphAuthClient graphAuthClient;

    @GetMapping("/token-test")
    public String probarToken() {
        String token = graphAuthClient.obtenerAccessToken();
        return "Token obtenido correctamente. Longitud: " + token.length();
    }
}
