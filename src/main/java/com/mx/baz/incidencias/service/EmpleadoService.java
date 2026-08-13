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

import com.mx.baz.incidencias.dto.EmpleadoAusenciaRequest;
import com.mx.baz.incidencias.entity.EmpleadoAusencia;
import com.mx.baz.incidencias.repository.EmpleadoAusenciaRepository;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoDiaLaboralRepository empleadoDiaLaboralRepository;
    private final EmpleadoMapper empleadoMapper;
    private final IncidenciaRepository incidenciaRepository;
    private final EmpleadoAusenciaRepository empleadoAusenciaRepository;

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
                		.id(ausencia.getId())
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
    
    @Transactional(readOnly = true)
    public EmpleadoDetalleResponse obtenerDetalleEmpleadoPorId(
            Long id
    ) {

        Empleado empleado =
                empleadoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "EMPLEADO_NO_ENCONTRADO",
                                        "No se encontró el empleado con id "
                                                + id
                                )
                        );


        long pendientes =
                contarIncidencias(
                        empleado.getId(),
                        EstadoIncidencia.PENDIENTE
                );


        long enProceso =
                contarIncidencias(
                        empleado.getId(),
                        EstadoIncidencia.EN_PROCESO
                );


        long reabiertas =
                contarIncidencias(
                        empleado.getId(),
                        EstadoIncidencia.REABIERTA
                );


        return EmpleadoDetalleResponse.builder()
                .id(empleado.getId())
                .nombre(empleado.getNombre())
                .usernameTelegram(
                        empleado.getUsernameTelegram()
                )
                .email(empleado.getEmail())
                .activo(empleado.getActivo())
                .diasLaborales(
                        obtenerDiasLaborales(
                                empleado
                        )
                )
                .ausencias(
                        obtenerAusencias(
                                empleado
                        )
                )
                .pendientes(pendientes)
                .enProceso(enProceso)
                .reabiertas(reabiertas)
                .totalActivas(
                        pendientes
                                + enProceso
                                + reabiertas
                )
                .ultimaAsignacion(
                        empleado.getUltimaAsignacion()
                )
                .build();
    }
    
    @Transactional
    public EmpleadoDetalleResponse actualizarEmpleado(
            Long id,
            EmpleadoRequest request
    ) {

        Empleado empleado =
                empleadoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "EMPLEADO_NO_ENCONTRADO",
                                        "No se encontró el empleado con id " + id
                                )
                        );


        // =====================================================
        // VALIDACIONES
        // =====================================================

        if (
                request.getNombre() == null
                || request.getNombre().isBlank()
        ) {

            throw new BusinessException(
                    "EMPLEADO_NOMBRE_INVALIDO",
                    "El nombre del empleado es obligatorio."
            );
        }


        if (
                request.getUsernameTelegram() == null
                || request.getUsernameTelegram().isBlank()
        ) {

            throw new BusinessException(
                    "EMPLEADO_TELEGRAM_INVALIDO",
                    "El usuario de Telegram es obligatorio."
            );
        }


        String usernameNormalizado =
                normalizarUsernameTelegram(
                        request.getUsernameTelegram()
                );


        /*
         * Evitamos que dos empleados tengan
         * el mismo username de Telegram.
         */
        empleadoRepository
                .findByUsernameTelegramIgnoreCase(
                        usernameNormalizado
                )
                .ifPresent(existente -> {

                    if (!existente.getId().equals(id)) {

                        throw new BusinessException(
                                "EMPLEADO_TELEGRAM_DUPLICADO",
                                "Ya existe otro empleado con el usuario @"
                                        + usernameNormalizado
                        );
                    }
                });


        // =====================================================
        // DATOS GENERALES
        // =====================================================

        empleado.setNombre(
                request.getNombre().trim()
        );

        empleado.setUsernameTelegram(
                usernameNormalizado
        );

        empleado.setEmail(
                normalizarTexto(
                        request.getEmail()
                )
        );


        /*
         * Si posteriormente reutilizamos este DTO
         * para modificar el estado, podrá hacerlo.
         *
         * Desde el modal de Editar no lo enviaremos,
         * por lo que conservará el estado actual.
         */
        if (request.getActivo() != null) {

            empleado.setActivo(
                    request.getActivo()
            );
        }


        // =====================================================
        // DÍAS LABORALES
        // =====================================================

        empleado
                .getDiasLaborales()
                .clear();


        if (request.getDiasLaborales() != null) {

            request.getDiasLaborales()
                    .stream()
                    .distinct()
                    .forEach(dia -> {

                        EmpleadoDiaLaboral diaLaboral =
                                EmpleadoDiaLaboral.builder()
                                        .empleado(empleado)
                                        .diaSemana(dia)
                                        .build();


                        empleado
                                .getDiasLaborales()
                                .add(diaLaboral);
                    });
        }


        empleadoRepository.save(
                empleado
        );


        /*
         * Ya tenemos un método que genera
         * exactamente el DTO que necesita la UI.
         */
        return obtenerDetalleEmpleadoPorId(
                empleado.getId()
        );
    }
    
    private String normalizarUsernameTelegram(
            String username
    ) {

        String valor =
                username.trim();

        return valor.startsWith("@")
                ? valor.substring(1)
                : valor;
    }


    private String normalizarTexto(
            String valor
    ) {

        if (
                valor == null
                || valor.isBlank()
        ) {
            return null;
        }

        return valor.trim();
    }
    
    @Transactional
    public EmpleadoDetalleResponse cambiarEstadoEmpleado(
            Long id,
            Boolean activo
    ) {

        Empleado empleado =
                empleadoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "EMPLEADO_NO_ENCONTRADO",
                                        "No se encontró el empleado con id " + id
                                )
                        );


        if (activo == null) {

            throw new BusinessException(
                    "EMPLEADO_ESTADO_INVALIDO",
                    "Debes indicar el nuevo estado del empleado."
            );
        }


        empleado.setActivo(activo);

        empleadoRepository.save(empleado);


        return obtenerDetalleEmpleadoPorId(
                empleado.getId()
        );
    }
    
    @Transactional
    public EmpleadoDetalleResponse registrarAusencia(
            Long empleadoId,
            EmpleadoAusenciaRequest request
    ) {

        Empleado empleado =
                empleadoRepository
                        .findById(empleadoId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "EMPLEADO_NO_ENCONTRADO",
                                        "No se encontró el empleado con id "
                                                + empleadoId
                                )
                        );


        if (request.getFechaInicio() == null) {

            throw new BusinessException(
                    "AUSENCIA_FECHA_INICIO_INVALIDA",
                    "La fecha de inicio es obligatoria."
            );
        }


        if (request.getFechaFin() == null) {

            throw new BusinessException(
                    "AUSENCIA_FECHA_FIN_INVALIDA",
                    "La fecha de fin es obligatoria."
            );
        }


        if (
                request.getFechaFin()
                        .isBefore(
                                request.getFechaInicio()
                        )
        ) {

            throw new BusinessException(
                    "AUSENCIA_FECHAS_INVALIDAS",
                    "La fecha de fin no puede ser anterior a la fecha de inicio."
            );
        }


        if (request.getMotivo() == null) {

            throw new BusinessException(
                    "AUSENCIA_MOTIVO_INVALIDO",
                    "Debes seleccionar un motivo de ausencia."
            );
        }


        EmpleadoAusencia ausencia =
                EmpleadoAusencia.builder()
                        .empleado(empleado)
                        .fechaInicio(
                                request.getFechaInicio()
                        )
                        .fechaFin(
                                request.getFechaFin()
                        )
                        .motivo(
                                request.getMotivo()
                        )
                        .observaciones(
                                normalizarTexto(
                                        request.getObservaciones()
                                )
                        )
                        .build();


        empleadoAusenciaRepository.save(
                ausencia
        );


        return obtenerDetalleEmpleadoPorId(
                empleadoId
        );
    }
    
    @Transactional
    public EmpleadoDetalleResponse eliminarAusencia(
            Long empleadoId,
            Long ausenciaId
    ) {

        Empleado empleado =
                empleadoRepository
                        .findById(empleadoId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "EMPLEADO_NO_ENCONTRADO",
                                        "No se encontró el empleado con id "
                                                + empleadoId
                                )
                        );


        EmpleadoAusencia ausencia =
                empleadoAusenciaRepository
                        .findById(ausenciaId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "AUSENCIA_NO_ENCONTRADA",
                                        "No se encontró la ausencia indicada."
                                )
                        );


        /*
         * Validamos que la ausencia realmente
         * pertenezca al empleado.
         */
        if (
                ausencia.getEmpleado() == null
                || !ausencia
                        .getEmpleado()
                        .getId()
                        .equals(empleado.getId())
        ) {

            throw new BusinessException(
                    "AUSENCIA_EMPLEADO_INVALIDO",
                    "La ausencia no pertenece al empleado indicado."
            );
        }


        /*
         * IMPORTANTE:
         *
         * Quitamos la ausencia directamente de la colección
         * del empleado.
         *
         * Como la relación tiene orphanRemoval = true,
         * Hibernate eliminará automáticamente el registro
         * de empleado_ausencia.
         *
         * Además, la colección en memoria queda actualizada,
         * por lo que el response ya no devolverá la ausencia
         * que acabamos de eliminar.
         */
        boolean eliminada =
                empleado
                        .getAusencias()
                        .removeIf(
                                item ->
                                        item.getId() != null
                                        && item.getId()
                                                .equals(ausenciaId)
                        );


        if (!eliminada) {

            throw new BusinessException(
                    "AUSENCIA_NO_ENCONTRADA",
                    "No fue posible eliminar la ausencia del empleado."
            );
        }


        /*
         * Forzamos el flush para que el DELETE se ejecute
         * antes de construir la respuesta.
         */
        empleadoRepository.saveAndFlush(
                empleado
        );


        return obtenerDetalleEmpleadoPorId(
                empleadoId
        );
    }
}
