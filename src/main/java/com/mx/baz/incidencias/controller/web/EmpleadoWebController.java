package com.mx.baz.incidencias.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EmpleadoWebController {

    @GetMapping("/empleados")
    public String empleados() {

        return "empleados";
    }
    
    @GetMapping("/empleados/{id}")
    public String detalleEmpleado(
            @PathVariable Long id
    ) {

        return "empleado-detalle";
    }
}