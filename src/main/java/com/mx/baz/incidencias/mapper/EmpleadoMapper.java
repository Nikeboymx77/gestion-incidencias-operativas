package com.mx.baz.incidencias.mapper;

import com.mx.baz.incidencias.dto.EmpleadoRequest;
import com.mx.baz.incidencias.dto.EmpleadoResponse;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.entity.EmpleadoDiaLaboral;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper {

    public Empleado toEntity(EmpleadoRequest request) {
        return Empleado.builder()
                .nombre(request.getNombre())
                .usernameTelegram(request.getUsernameTelegram())
                .email(request.getEmail())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .ultimaAsignacion(null)
                .build();
    }

    public EmpleadoResponse toResponse(Empleado empleado) {
        return EmpleadoResponse.builder()
                .id(empleado.getId())
                .nombre(empleado.getNombre())
                .usernameTelegram(empleado.getUsernameTelegram())
                .email(empleado.getEmail())
                .activo(empleado.getActivo())
                .ultimaAsignacion(empleado.getUltimaAsignacion())
                .diasLaborales(
                        empleado.getDiasLaborales()
                                .stream()
                                .map(EmpleadoDiaLaboral::getDiaSemana)
                                .toList()
                )
                .build();
    }

    public EmpleadoResponse toResponse(Empleado empleado, EmpleadoRequest request) {
        return EmpleadoResponse.builder()
                .id(empleado.getId())
                .nombre(empleado.getNombre())
                .usernameTelegram(empleado.getUsernameTelegram())
                .email(empleado.getEmail())
                .activo(empleado.getActivo())
                .ultimaAsignacion(empleado.getUltimaAsignacion())
                .diasLaborales(request.getDiasLaborales())
                .build();
    }
}
