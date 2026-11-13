package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.IncidenciaClasificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidenciaClasificacionRepository
        extends JpaRepository<IncidenciaClasificacion, Long> {

    Optional<IncidenciaClasificacion>
        findByIncidenciaId(Long incidenciaId);

    boolean existsByIncidenciaId(Long incidenciaId);
}
