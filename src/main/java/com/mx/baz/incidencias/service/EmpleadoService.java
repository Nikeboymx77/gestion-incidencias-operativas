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
import com.mx.baz.incidencias.dto.EmpleadoResumenOperativoResponse;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.exception.BusinessException;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import java.time.DayOfWeek;
import java.util.List;

import com.mx.baz.incidencias.dto.AusenciaResponse;
import com.mx.baz.incidencias.dto.EmpleadoDetalleResponse;


@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoDiaLaboralRepository empleadoDiaLaboralRepository;
    private final EmpleadoMapper empleadoMapper;
    private final IncidenciaRepository incidenciaRepository;

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
    
    @Transactional(readOnly = true)
    public List<EmpleadoResumenOperativoResponse> obtenerResumenOperativo() {

        return empleadoRepository.findAll()
                .stream()
                .map(empleado -> {

                    long pendientes =
                            incidenciaRepository.countByEmpleadoAsignadoIdAndEstado(
                                    empleado.getId(),
                                    EstadoIncidencia.PENDIENTE
                            );

                    long enProceso =
                            incidenciaRepository.countByEmpleadoAsignadoIdAndEstado(
                                    empleado.getId(),
                                    EstadoIncidencia.EN_PROCESO
                            );

                    long reabiertas =
                            incidenciaRepository.countByEmpleadoAsignadoIdAndEstado(
                                    empleado.getId(),
                                    EstadoIncidencia.REABIERTA
                            );

                    return EmpleadoResumenOperativoResponse.builder()
                            .id(empleado.getId())
                            .nombre(empleado.getNombre())
                            .usernameTelegram(empleado.getUsernameTelegram())
                            .email(empleado.getEmail())
                            .activo(empleado.getActivo())
                            .pendientes(pendientes)
                            .enProceso(enProceso)
                            .reabiertas(reabiertas)
                            .totalActivas(pendientes + enProceso + reabiertas)
                            .ultimaAsignacion(empleado.getUltimaAsignacion())
                            .build();
                })
                .toList();
    }
    
    @Transactional(readOnly = true)
    public EmpleadoDetalleResponse obtenerDetalleEmpleado(
            String usernameTelegram
    ) {

        String usernameNormalizado =
                usernameTelegram.replace("@", "").trim();

        Empleado empleado = empleadoRepository
                .findByUsernameTelegramIgnoreCase(usernameNormalizado)
                .orElseThrow(() -> new BusinessException(
                        "EMPLEADO_NO_ENCONTRADO",
                        "No se encontró un empleado con el usuario @"
                                + usernameNormalizado
                ));

        long pendientes = contarIncidencias(
                empleado.getId(),
                EstadoIncidencia.PENDIENTE
        );

        long enProceso = contarIncidencias(
                empleado.getId(),
                EstadoIncidencia.EN_PROCESO
        );

        long reabiertas = contarIncidencias(
                empleado.getId(),
                EstadoIncidencia.REABIERTA
        );

        return EmpleadoDetalleResponse.builder()
                .id(empleado.getId())
                .nombre(empleado.getNombre())
                .usernameTelegram(empleado.getUsernameTelegram())
                .email(empleado.getEmail())
                .activo(empleado.getActivo())
                .diasLaborales(obtenerDiasLaborales(empleado))
                .ausencias(obtenerAusencias(empleado))
                .pendientes(pendientes)
                .enProceso(enProceso)
                .reabiertas(reabiertas)
                .totalActivas(
                        pendientes + enProceso + reabiertas
                )
                .ultimaAsignacion(empleado.getUltimaAsignacion())
                .build();
    }
    
    private long contarIncidencias(
            Long empleadoId,
            EstadoIncidencia estado
    ) {
        return incidenciaRepository
                .countByEmpleadoAsignadoIdAndEstado(
                        empleadoId,
                        estado
                );
    }
    
    private List<DayOfWeek> obtenerDiasLaborales(
            Empleado empleado
    ) {
        return empleado.getDiasLaborales()
                .stream()
                .map(EmpleadoDiaLaboral::getDiaSemana)
                .sorted()
                .toList();
    }
    
    private List<AusenciaResponse> obtenerAusencias(
            Empleado empleado
    ) {
        return empleado.getAusencias()
                .stream()
                .map(ausencia -> AusenciaResponse.builder()
                        .fechaInicio(ausencia.getFechaInicio())
                        .fechaFin(ausencia.getFechaFin())
                        .motivo(
                                ausencia.getMotivo() != null
                                        ? ausencia.getMotivo().name()
                                        : null
                        )
                        .observaciones(ausencia.getObservaciones())
                        .build())
                .toList();
    }
}
