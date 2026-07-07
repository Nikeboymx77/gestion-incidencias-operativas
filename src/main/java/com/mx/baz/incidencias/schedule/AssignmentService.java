package com.mx.baz.incidencias.schedule;

import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.exception.BusinessException;
import com.mx.baz.incidencias.exception.ErrorCodes;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final EmpleadoRepository empleadoRepository;
    private final WorkScheduleService workScheduleService;

    public Empleado obtenerEmpleadoDisponible() {

        List<Empleado> empleadosDisponibles = workScheduleService.obtenerDisponiblesHoy();

        Empleado empleadoAsignado = empleadosDisponibles.stream()
                .min(Comparator.comparing(
                        Empleado::getUltimaAsignacion,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ))
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.EMPLEADO_NO_DISPONIBLE,
                        "No hay empleados disponibles para asignar la incidencia"
                ));

        log.info("Empleado seleccionado para asignación: {}", empleadoAsignado.getNombre());

        return empleadoAsignado;
    }

    public Empleado actualizarUltimaAsignacion(Empleado empleado) {
        empleado.setUltimaAsignacion(LocalDateTime.now());
        return empleadoRepository.save(empleado);
    }
}
