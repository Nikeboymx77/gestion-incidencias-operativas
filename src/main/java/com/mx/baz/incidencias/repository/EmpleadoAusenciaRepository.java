package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.EmpleadoAusencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface EmpleadoAusenciaRepository extends JpaRepository<EmpleadoAusencia, Long> {

    boolean existsByEmpleadoIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long empleadoId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}
