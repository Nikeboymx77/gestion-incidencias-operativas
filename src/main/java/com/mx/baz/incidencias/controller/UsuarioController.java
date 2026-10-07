package com.mx.baz.incidencias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mx.baz.incidencias.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {

        model.addAttribute(
                "usuarios",
                usuarioService.obtenerTodos()
        );

        return "usuarios";
    }
}
