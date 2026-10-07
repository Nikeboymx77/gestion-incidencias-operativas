package com.mx.baz.incidencias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mx.baz.incidencias.entity.Usuario;

import com.mx.baz.incidencias.entity.RolUsuario;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsernameIgnoreCase(String username);
    
    long countByRolAndActivoTrue(RolUsuario rol);

}