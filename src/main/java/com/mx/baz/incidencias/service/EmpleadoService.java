package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.dto.EmpleadoRequest;
import com.mx.baz.incidencias.dto.EmpleadoResponse;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.entity.EmpleadoDiaLaboral;
import com.mx.baz.incidencias.mapper.EmpleadoMapper;
import com.mx.baz.incidencias.repository.EmpleadoDiaLaboralRepository;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoDiaLaboralRepository empleadoDiaLaboralRepository;
    private final EmpleadoMapper empleadoMapper;

    @Transactional
    public EmpleadoResponse crearEmpleado(EmpleadoRequest request) {

    	Empleado empleado = empleadoMapper.toEntity(request);

        Empleado empleadoGuardado = empleadoRepository.save(empleado);

        if (request.getDiasLaborales() != null) {
            request.getDiasLaborales().forEach(dia -> {
                EmpleadoDiaLaboral diaLaboral = EmpleadoDiaLaboral.builder()
                        .empleado(empleadoGuardado)
                        .diaSemana(dia)
                        .build();

                empleadoDiaLaboralRepository.save(diaLaboral);
            });
        }

        return empleadoMapper.toResponse(empleadoGuardado, request);
    }

    public List<Empleado> obtenerEmpleados() {
        return empleadoRepository.findAll();
    }
}
