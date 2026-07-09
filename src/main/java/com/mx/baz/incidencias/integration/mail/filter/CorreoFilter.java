package com.mx.baz.incidencias.integration.mail.filter;

import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;

public interface CorreoFilter {

    boolean debeProcesarse(CorreoDTO correo);
}
