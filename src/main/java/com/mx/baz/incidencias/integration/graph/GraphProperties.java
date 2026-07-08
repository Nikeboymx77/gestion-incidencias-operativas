package com.mx.baz.incidencias.integration.graph;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "graph")
public class GraphProperties {

    private String tenantId;
    private String clientId;
    private String clientSecret;
    private String scope;
    private String tokenUrl;
}