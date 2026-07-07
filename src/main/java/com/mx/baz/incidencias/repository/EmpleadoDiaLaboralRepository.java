package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.EmpleadoDiaLaboral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface EmpleadoDiaLaboralRepository extends JpaRepository<EmpleadoDiaLaboral, Long> {

    List<EmpleadoDiaLaboral> findByDiaSemana(DayOfWeek diaSemana);

    List<EmpleadoDiaLaboral> findByEmpleadoId(Long empleadoId);

    boolean existsByEmpleadoIdAndDiaSemana(Long empleadoId, DayOfWeek diaSemana);
}