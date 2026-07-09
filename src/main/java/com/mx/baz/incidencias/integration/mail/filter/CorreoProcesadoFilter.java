package com.mx.baz.incidencias.integration.mail.filter;

import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.integration.mail.repository.CorreoProcesadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CorreoProcesadoFilter implements CorreoFilter {

    private final CorreoProcesadoRepository correoProcesadoRepository;

    @Override
    public boolean debeProcesarse(CorreoDTO correo) {
        if (correo == null || correo.getIdCorreo() == null || correo.getIdCorreo().isBlank()) {
            return false;
        }

        return !correoProcesadoRepository.existsByIdCorreo(correo.getIdCorreo());
    }
}