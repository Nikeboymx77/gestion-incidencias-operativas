package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.ReglaClasificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReglaClasificacionRepository
        extends JpaRepository<ReglaClasificacion, Long> {

    List<ReglaClasificacion>
        findByActivoTrueOrderByPrioridadAscIdAsc();
}