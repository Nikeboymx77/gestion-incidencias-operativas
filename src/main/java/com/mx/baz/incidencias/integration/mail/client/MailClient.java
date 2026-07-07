package com.mx.baz.incidencias.integration.mail.client;

import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;

import java.util.List;

public interface MailClient {

    List<CorreoDTO> obtenerCorreosPendientes();

}
