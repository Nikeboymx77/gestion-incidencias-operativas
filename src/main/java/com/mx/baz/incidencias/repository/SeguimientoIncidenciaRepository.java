package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.SeguimientoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeguimientoIncidenciaRepository
        extends JpaRepository<SeguimientoIncidencia, Long> {

    List<SeguimientoIncidencia>
            findByIncidenciaIdOrderByFechaCorreoAsc(Long incidenciaId);

    long countByIncidenciaId(Long incidenciaId);
}