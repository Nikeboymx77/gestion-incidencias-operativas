package com.mx.baz.incidencias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mx.baz.incidencias.entity.ReglaProcesoAfectado;

// Importa ReglaProcesoAfectado desde tu paquete de entidades

public interface ReglaProcesoAfectadoRepository
        extends JpaRepository<ReglaProcesoAfectado, Long> {

    List<ReglaProcesoAfectado>
        findByActivoTrueOrderByPrioridadAscIdAsc();
    
    // Nuevo método: filtrar por origen
    List<ReglaProcesoAfectado>
    findByActivoTrueAndOrigenOrderByPrioridadAscIdAsc(
        String origen
    );
}