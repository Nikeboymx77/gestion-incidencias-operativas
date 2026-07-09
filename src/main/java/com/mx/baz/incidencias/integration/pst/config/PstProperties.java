package com.mx.baz.incidencias.integration.pst.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pst")
public class PstProperties {

    private String filePath;
    private String folderName;
    private Integer maxMails = 10;
}