package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.HistorialIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialIncidenciaRepository extends JpaRepository<HistorialIncidencia, Long> {
}