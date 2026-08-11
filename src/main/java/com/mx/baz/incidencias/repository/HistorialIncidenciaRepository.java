package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.HistorialIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialIncidenciaRepository
        extends JpaRepository<HistorialIncidencia, Long> {

    List<HistorialIncidencia>
    findByIncidenciaFolioOrderByFechaEventoAsc(
            String folio
    );
}