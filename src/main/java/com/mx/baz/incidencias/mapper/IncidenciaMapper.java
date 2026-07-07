package com.mx.baz.incidencias.mapper;

import com.mx.baz.incidencias.dto.EmpleadoResumenResponse;
import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.entity.Incidencia;
import org.springframework.stereotype.Component;

@Component
public class IncidenciaMapper {

    public IncidenciaResponse toResponse(Incidencia incidencia) {
        return IncidenciaResponse.builder()
                .id(incidencia.getId())
                .folio(incidencia.getFolio())
                .asunto(incidencia.getAsunto())
                .remitente(incidencia.getRemitente())
                .fechaCorreo(incidencia.getFechaCorreo())
                .carpetaOrigen(incidencia.getCarpetaOrigen())
                .prioridad(incidencia.getPrioridad())
                .descripcion(incidencia.getDescripcion())
                .estado(incidencia.getEstado())
                .empleadoAsignado(toEmpleadoResumen(incidencia.getEmpleadoAsignado()))
                .fechaAsignacion(incidencia.getFechaAsignacion())
                .usuarioQueLaTomo(incidencia.getUsuarioQueLaTomo())
                .fechaInicio(incidencia.getFechaInicio())
                .fechaResolucion(incidencia.getFechaResolucion())
                .build();
    }

    private EmpleadoResumenResponse toEmpleadoResumen(Empleado empleado) {
        if (empleado == null) {
            return null;
        }

        return EmpleadoResumenResponse.builder()
                .id(empleado.getId())
                .nombre(empleado.getNombre())
                .usernameTelegram(empleado.getUsernameTelegram())
                .email(empleado.getEmail())
                .build();
    }
}
