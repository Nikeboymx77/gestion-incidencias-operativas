package com.mx.baz.incidencias.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mx.baz.incidencias.dto.UsuarioRequest;
import com.mx.baz.incidencias.entity.Usuario;
import com.mx.baz.incidencias.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.mx.baz.incidencias.dto.UsuarioUpdateRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mx.baz.incidencias.dto.UsuarioPasswordRequest;
import java.security.Principal;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuarioApiController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> crearUsuario(
            @Valid @RequestBody UsuarioRequest request) {

        try {

            Usuario usuario =
                    usuarioService.crearUsuario(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "id", usuario.getId(),
                            "nombre", usuario.getNombre(),
                            "username", usuario.getUsername(),
                            "rol", usuario.getRol(),
                            "activo", usuario.getActivo()
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request,
            Principal principal) {

        try {
        	
        	Usuario usuarioActual =
        	        usuarioService.obtenerPorId(id);

        	boolean esMismoUsuario =
        	        usuarioActual.getUsername()
        	                .equalsIgnoreCase(
        	                        principal.getName()
        	                );

        	boolean intentaCambiarSuRol =
        	        esMismoUsuario
        	        && usuarioActual.getRol()
        	                != request.getRol();

        	if (intentaCambiarSuRol) {

        	    throw new IllegalArgumentException(
        	            "No puedes cambiar el rol de tu propia cuenta"
        	    );
        	}

            Usuario usuario =
                    usuarioService.actualizarUsuario(
                            id,
                            request
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "id", usuario.getId(),
                            "nombre", usuario.getNombre(),
                            "username", usuario.getUsername(),
                            "rol", usuario.getRol(),
                            "activo", usuario.getActivo()
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensaje",
                                    e.getMessage()
                            )
                    );
        }
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo,
            Principal principal) {

        try {
        	
        	Usuario usuarioActual =
        	        usuarioService.obtenerPorId(id);

        	boolean esMismoUsuario =
        	        usuarioActual.getUsername()
        	                .equalsIgnoreCase(
        	                        principal.getName()
        	                );

        	boolean intentaDesactivarse =
        	        esMismoUsuario
        	        && !activo;

        	if (intentaDesactivarse) {

        	    throw new IllegalArgumentException(
        	            "No puedes desactivar tu propia cuenta"
        	    );
        	}

            Usuario usuario =
                    usuarioService.cambiarEstado(
                            id,
                            activo
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "id", usuario.getId(),
                            "nombre", usuario.getNombre(),
                            "username", usuario.getUsername(),
                            "rol", usuario.getRol(),
                            "activo", usuario.getActivo()
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensaje",
                                    e.getMessage()
                            )
                    );
        }
    }
    
    @PatchMapping("/{id}/password")
    public ResponseEntity<?> restablecerPassword(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioPasswordRequest request) {

        try {

            usuarioService.restablecerPassword(
                    id,
                    request
            );

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Contraseña restablecida correctamente"
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "mensaje",
                                    e.getMessage()
                            )
                    );
        }
    }
}
