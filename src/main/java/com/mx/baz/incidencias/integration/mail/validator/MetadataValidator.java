package com.mx.baz.incidencias.integration.mail.validator;

import com.mx.baz.incidencias.integration.mail.model.CorreoMetadata;
import com.mx.baz.incidencias.integration.mail.model.MetadataValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MetadataValidator {

    public MetadataValidationResult validar(CorreoMetadata metadata) {

        List<String> camposFaltantes = new ArrayList<>();

        if (metadata == null) {
            camposFaltantes.add("metadata");

            return MetadataValidationResult.builder()
                    .completa(false)
                    .camposFaltantes(camposFaltantes)
                    .build();
        }

        agregarSiVacio(camposFaltantes, "folio", metadata.getFolio());
        agregarSiVacio(camposFaltantes, "sucursal", metadata.getSucursal());
        agregarSiVacio(camposFaltantes, "clienteUnico", metadata.getClienteUnico());
        agregarSiVacio(camposFaltantes, "nombreCliente", metadata.getNombreCliente());
        agregarSiVacio(camposFaltantes, "equipo", metadata.getEquipo());
        agregarSiVacio(camposFaltantes, "motivo", metadata.getMotivo());

        return MetadataValidationResult.builder()
                .completa(camposFaltantes.isEmpty())
                .camposFaltantes(camposFaltantes)
                .build();
    }

    private void agregarSiVacio(
            List<String> camposFaltantes,
            String nombreCampo,
            String valor) {

        if (valor == null || valor.isBlank()) {
            camposFaltantes.add(nombreCampo);
        }
    }
}