package com.mx.baz.incidencias.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mx.baz.incidencias.entity.ProcesoAfectado;

public interface ProcesoAfectadoRepository
        extends JpaRepository<ProcesoAfectado, Long> {

    Optional<ProcesoAfectado> findByCodigo(String codigo);

    List<ProcesoAfectado> findByActivoTrueOrderByNombreAsc();
}
