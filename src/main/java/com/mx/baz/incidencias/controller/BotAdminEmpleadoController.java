package com.mx.baz.incidencias.controller;

import com.mx.baz.incidencias.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bot/admin/empleados")
@RequiredArgsConstructor
public class BotAdminEmpleadoController {

    private final EmpleadoService empleadoService;

    @Value("${sgio.bot.admin.telegram-id}")
    private String telegramAdminId;

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo,
            @RequestHeader("X-SGIO-TELEGRAM-USER-ID") String telegramUserId) {

        if (!telegramAdminId.equals(telegramUserId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para realizar esta operación.");
        }

        return ResponseEntity.ok(
                empleadoService.cambiarEstadoEmpleado(id, activo)
        );
    }
}
