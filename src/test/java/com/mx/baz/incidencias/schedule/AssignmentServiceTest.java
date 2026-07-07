package com.mx.baz.incidencias.schedule;

import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.exception.BusinessException;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private WorkScheduleService workScheduleService;

    @InjectMocks
    private AssignmentService assignmentService;

    @Test
    void debeAsignarEmpleadoConUltimaAsignacionMasAntigua() {
        Empleado cesar = Empleado.builder()
                .id(1L)
                .nombre("Cesar")
                .ultimaAsignacion(LocalDateTime.now().minusHours(1))
                .build();

        Empleado juan = Empleado.builder()
                .id(2L)
                .nombre("Juan")
                .ultimaAsignacion(LocalDateTime.now().minusHours(3))
                .build();

        when(workScheduleService.obtenerDisponiblesHoy())
                .thenReturn(List.of(cesar, juan));

        Empleado resultado = assignmentService.obtenerEmpleadoDisponible();

        assertEquals("Juan", resultado.getNombre());
        verify(workScheduleService, times(1)).obtenerDisponiblesHoy();
    }

    @Test
    void debeAsignarEmpleadoSinUltimaAsignacionPrimero() {
        Empleado cesar = Empleado.builder()
                .id(1L)
                .nombre("Cesar")
                .ultimaAsignacion(LocalDateTime.now().minusHours(1))
                .build();

        Empleado nuevo = Empleado.builder()
                .id(2L)
                .nombre("Nuevo")
                .ultimaAsignacion(null)
                .build();

        when(workScheduleService.obtenerDisponiblesHoy())
                .thenReturn(List.of(cesar, nuevo));

        Empleado resultado = assignmentService.obtenerEmpleadoDisponible();

        assertEquals("Nuevo", resultado.getNombre());
    }

    @Test
    void debeLanzarBusinessExceptionCuandoNoHayEmpleadosDisponibles() {
        when(workScheduleService.obtenerDisponiblesHoy())
                .thenReturn(List.of());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> assignmentService.obtenerEmpleadoDisponible()
        );

        assertEquals("EMP-003", exception.getCodigo());
        assertEquals("No hay empleados disponibles para asignar la incidencia", exception.getMessage());
    }

    @Test
    void debeActualizarUltimaAsignacion() {
        Empleado empleado = Empleado.builder()
                .id(1L)
                .nombre("Cesar")
                .ultimaAsignacion(null)
                .build();

        when(empleadoRepository.save(any(Empleado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Empleado resultado = assignmentService.actualizarUltimaAsignacion(empleado);

        assertNotNull(resultado.getUltimaAsignacion());
        verify(empleadoRepository, times(1)).save(empleado);
    }
}
