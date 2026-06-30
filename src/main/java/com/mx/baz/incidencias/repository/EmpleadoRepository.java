package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    List<Empleado> findByActivoTrue();

    List<Empleado> findByTrabajaSemanaTrueAndActivoTrue();

    List<Empleado> findByTrabajaFinSemanaTrueAndActivoTrue();
    
    Optional<Empleado> findFirstByTrabajaSemanaTrueAndActivoTrueOrderByOrdenAsignacionAsc();

    Optional<Empleado> findFirstByTrabajaFinSemanaTrueAndActivoTrueOrderByOrdenAsignacionAsc();
}