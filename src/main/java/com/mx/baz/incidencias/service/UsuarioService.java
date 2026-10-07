package com.mx.baz.incidencias.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mx.baz.incidencias.entity.Usuario;
import com.mx.baz.incidencias.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.mx.baz.incidencias.dto.UsuarioPasswordRequest;
import com.mx.baz.incidencias.dto.UsuarioRequest;

import com.mx.baz.incidencias.dto.UsuarioUpdateRequest;

import com.mx.baz.incidencias.entity.RolUsuario;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }
    
    public boolean existeUsername(String username) {

        return usuarioRepository
                .findByUsernameIgnoreCase(username)
                .isPresent();
    }
    
    public Usuario crearUsuario(UsuarioRequest request) {

        String username = request.getUsername().trim();

        if (existeUsername(username)) {
            throw new IllegalArgumentException(
                    "El nombre de usuario ya está registrado"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(
                request.getNombre().trim()
        );

        usuario.setUsername(username);

        usuario.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        usuario.setRol(
                request.getRol()
        );

        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }
    
    public Usuario obtenerPorId(Long id) {

        return usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario no encontrado"
                        )
                );
    }
    
    public Usuario actualizarUsuario(
            Long id,
            UsuarioUpdateRequest request) {

        Usuario usuario = obtenerPorId(id);

        boolean cambiaDeAdminAOperador =
                usuario.getRol() == RolUsuario.ADMIN
                && request.getRol() == RolUsuario.OPERADOR;

        if (cambiaDeAdminAOperador) {

            long administradoresActivos =
                    usuarioRepository
                            .countByRolAndActivoTrue(
                                    RolUsuario.ADMIN
                            );

            if (Boolean.TRUE.equals(usuario.getActivo())
                    && administradoresActivos <= 1) {

                throw new IllegalArgumentException(
                        "No puedes cambiar el rol del último administrador activo"
                );
            }
        }

        usuario.setNombre(
                request.getNombre().trim()
        );

        usuario.setRol(
                request.getRol()
        );

        return usuarioRepository.save(usuario);
    }
    
    public Usuario cambiarEstado(
            Long id,
            boolean activo) {

        Usuario usuario = obtenerPorId(id);

        boolean intentaDesactivarUltimoAdmin =
                usuario.getRol() == RolUsuario.ADMIN
                && Boolean.TRUE.equals(usuario.getActivo())
                && !activo;

        if (intentaDesactivarUltimoAdmin) {

            long administradoresActivos =
                    usuarioRepository
                            .countByRolAndActivoTrue(
                                    RolUsuario.ADMIN
                            );

            if (administradoresActivos <= 1) {

                throw new IllegalArgumentException(
                        "No puedes desactivar al último administrador activo"
                );
            }
        }

        usuario.setActivo(activo);

        return usuarioRepository.save(usuario);
    }
    
    public Usuario restablecerPassword(
            Long id,
            UsuarioPasswordRequest request) {

        Usuario usuario = obtenerPorId(id);

        String passwordCodificada =
                passwordEncoder.encode(
                        request.getPassword()
                );

        usuario.setPassword(
                passwordCodificada
        );

        return usuarioRepository.save(usuario);
    }
}
