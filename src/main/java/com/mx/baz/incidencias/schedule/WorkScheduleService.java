package com.mx.baz.incidencias.schedule;

import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.repository.EmpleadoAusenciaRepository;
import com.mx.baz.incidencias.repository.EmpleadoDiaLaboralRepository;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoDiaLaboralRepository empleadoDiaLaboralRepository;
    private final EmpleadoAusenciaRepository empleadoAusenciaRepository;

    public boolean trabajaHoy(Empleado empleado) {
        DayOfWeek hoy = LocalDate.now().getDayOfWeek();

        return empleadoDiaLaboralRepository
                .existsByEmpleadoIdAndDiaSemana(empleado.getId(), hoy);
    }

    public boolean estaAusenteHoy(Empleado empleado) {
        LocalDate hoy = LocalDate.now();

        return empleadoAusenciaRepository
                .existsByEmpleadoIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                        empleado.getId(),
                        hoy,
                        hoy
                );
    }

    public boolean estaDisponibleHoy(Empleado empleado) {
        return Boolean.TRUE.equals(empleado.getActivo())
                && trabajaHoy(empleado)
                && !estaAusenteHoy(empleado);
    }

    public List<Empleado> obtenerDisponiblesHoy() {
        return empleadoRepository.findByActivoTrue()
                .stream()
                .filter(this::estaDisponibleHoy)
                .toList();
    }
}
