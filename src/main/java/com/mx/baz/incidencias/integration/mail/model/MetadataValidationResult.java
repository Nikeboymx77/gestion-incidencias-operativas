package com.mx.baz.incidencias.integration.mail.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MetadataValidationResult {

    private boolean completa;
    private List<String> camposFaltantes;
}
