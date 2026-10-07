package com.mx.baz.incidencias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioPasswordRequest {

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(
        min = 8,
        message = "La contraseña debe tener al menos 8 caracteres"
    )
    private String password;
}
