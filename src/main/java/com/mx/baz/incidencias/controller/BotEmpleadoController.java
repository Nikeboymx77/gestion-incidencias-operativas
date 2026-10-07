package com.mx.baz.incidencias.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mx.baz.incidencias.dto.EmpleadoDetalleResponse;
import com.mx.baz.incidencias.dto.EmpleadoResumenOperativoResponse;
import com.mx.baz.incidencias.service.EmpleadoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bot/empleados")
@RequiredArgsConstructor
public class BotEmpleadoController {

    private final EmpleadoService empleadoService;


    @GetMapping("/resumen")
    public ResponseEntity<List<EmpleadoResumenOperativoResponse>>
            obtenerResumenOperativo() {

        return ResponseEntity.ok(
                empleadoService.obtenerResumenOperativo()
        );
    }


    @GetMapping("/{usernameTelegram}")
    public ResponseEntity<EmpleadoDetalleResponse>
            obtenerDetalleEmpleado(
                    @PathVariable String usernameTelegram) {

        return ResponseEntity.ok(
                empleadoService.obtenerDetalleEmpleado(
                        usernameTelegram
                )
        );
    }
}
