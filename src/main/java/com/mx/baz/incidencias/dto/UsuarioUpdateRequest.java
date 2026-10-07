package com.mx.baz.incidencias.dto;

import com.mx.baz.incidencias.entity.RolUsuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioUpdateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(
        max = 100,
        message = "El nombre no puede exceder 100 caracteres"
    )
    private String nombre;

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rol;
}
