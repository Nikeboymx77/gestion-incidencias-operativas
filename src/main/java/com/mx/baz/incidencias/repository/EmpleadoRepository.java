package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    List<Empleado> findByActivoTrue();

    List<Empleado> findByActivoTrueOrderByNombreAsc();

    Optional<Empleado> findByUsernameTelegramIgnoreCase(String usernameTelegram);

    long countByActivoTrue();

}