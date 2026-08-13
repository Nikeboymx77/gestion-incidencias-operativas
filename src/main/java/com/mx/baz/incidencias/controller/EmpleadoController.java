package com.mx.baz.incidencias.controller;

import com.mx.baz.incidencias.dto.EmpleadoDetalleResponse;
import com.mx.baz.incidencias.dto.EmpleadoRequest;
import com.mx.baz.incidencias.dto.EmpleadoResponse;
import com.mx.baz.incidencias.dto.EmpleadoResumenOperativoResponse;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import com.mx.baz.incidencias.dto.EmpleadoAusenciaRequest;

import org.springframework.http.ResponseEntity;
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
    
    @GetMapping("/resumen")
    public ResponseEntity<List<EmpleadoResumenOperativoResponse>> obtenerResumenOperativo() {

        return ResponseEntity.ok(
                empleadoService.obtenerResumenOperativo()
        );
    }
    
    @GetMapping("/{usernameTelegram}")
    public ResponseEntity<EmpleadoDetalleResponse> obtenerDetalleEmpleado(
            @PathVariable String usernameTelegram
    ) {
        return ResponseEntity.ok(
                empleadoService.obtenerDetalleEmpleado(
                        usernameTelegram
                )
        );
    }
    
    @GetMapping("/id/{id}")
    public ResponseEntity<EmpleadoDetalleResponse>
    obtenerDetalleEmpleadoPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                empleadoService
                        .obtenerDetalleEmpleadoPorId(
                                id
                        )
        );
    }
   
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDetalleResponse>
    actualizarEmpleado(
            @PathVariable Long id,
            @RequestBody EmpleadoRequest request
    ) {

        return ResponseEntity.ok(
                empleadoService.actualizarEmpleado(
                        id,
                        request
                )
        );
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EmpleadoDetalleResponse>
    cambiarEstadoEmpleado(
            @PathVariable Long id,
            @RequestParam Boolean activo
    ) {

        return ResponseEntity.ok(
                empleadoService.cambiarEstadoEmpleado(
                        id,
                        activo
                )
        );
    }
    
    @PostMapping("/{id}/ausencias")
    public ResponseEntity<EmpleadoDetalleResponse>
    registrarAusencia(
            @PathVariable Long id,
            @RequestBody EmpleadoAusenciaRequest request
    ) {

        return ResponseEntity.ok(
                empleadoService.registrarAusencia(
                        id,
                        request
                )
        );
    }
    
    @DeleteMapping(
            "/{id}/ausencias/{ausenciaId}"
    )
    public ResponseEntity<EmpleadoDetalleResponse>
    eliminarAusencia(
            @PathVariable Long id,
            @PathVariable Long ausenciaId
    ) {

        return ResponseEntity.ok(
                empleadoService.eliminarAusencia(
                        id,
                        ausenciaId
                )
        );
    }
}