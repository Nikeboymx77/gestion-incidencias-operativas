package com.mx.baz.incidencias.controller;

import com.mx.baz.incidencias.dto.EmpleadoRequest;
import com.mx.baz.incidencias.dto.EmpleadoResponse;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @PostMapping
    public EmpleadoResponse crearEmpleado(@RequestBody EmpleadoRequest request) {
        return empleadoService.crearEmpleado(request);
    }

    @GetMapping
    public List<Empleado> obtenerEmpleados() {
        return empleadoService.obtenerEmpleados();
    }
}