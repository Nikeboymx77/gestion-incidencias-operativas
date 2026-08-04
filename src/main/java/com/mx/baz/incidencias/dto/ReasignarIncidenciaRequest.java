package com.mx.baz.incidencias.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReasignarIncidenciaRequest {

    @NotBlank(message = "El usuario de Telegram destino es obligatorio")
    private String usernameTelegram;

    @NotBlank(message = "El usuario que realiza la reasignación es obligatorio")
    private String usuario;

    @NotBlank(message = "El motivo de reasignación es obligatorio")
    private String comentario;
}