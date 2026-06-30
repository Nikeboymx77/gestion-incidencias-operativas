package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.dto.EmpleadoRequest;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public Empleado crearEmpleado(EmpleadoRequest request) {
        Empleado empleado = Empleado.builder()
                .nombre(request.getNombre())
                .usernameTelegram(request.getUsernameTelegram())
                .email(request.getEmail())
                .activo(true)
                .trabajaSemana(request.getTrabajaSemana())
                .trabajaFinSemana(request.getTrabajaFinSemana())
                .ordenAsignacion(request.getOrdenAsignacion())
                .build();

        return empleadoRepository.save(empleado);
    }

    public List<Empleado> obtenerEmpleados() {
        return empleadoRepository.findAll();
    }
}
