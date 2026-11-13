package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.CategoriaProblema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaProblemaRepository
        extends JpaRepository<CategoriaProblema, Long> {

    Optional<CategoriaProblema> findByCodigo(String codigo);
}