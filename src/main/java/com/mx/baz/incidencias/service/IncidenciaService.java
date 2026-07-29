package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.dto.ActualizarEstadoIncidenciaRequest;
import com.mx.baz.incidencias.dto.BalanceEmpleadoResponse;
import com.mx.baz.incidencias.dto.BalanceResponse;
import com.mx.baz.incidencias.dto.EmpleadoResumenOperativoResponse;
import com.mx.baz.incidencias.dto.EstadisticasResponse;
import com.mx.baz.incidencias.dto.IncidenciaAtrasadaResponse;
import com.mx.baz.incidencias.dto.IncidenciaRequest;
import com.mx.baz.incidencias.dto.IncidenciaResponse;
import com.mx.baz.incidencias.dto.RankingEmpleadoResponse;
import com.mx.baz.incidencias.dto.ResolverIncidenciaRequest;
import com.mx.baz.incidencias.entity.Empleado;
import com.mx.baz.incidencias.entity.HistorialIncidencia;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.enums.NivelCarga;
import com.mx.baz.incidencias.events.IncidenciaCreadaEvent;
import com.mx.baz.incidencias.exception.BusinessException;
import com.mx.baz.incidencias.exception.ErrorCodes;
import com.mx.baz.incidencias.mapper.IncidenciaMapper;
import com.mx.baz.incidencias.repository.EmpleadoRepository;
import com.mx.baz.incidencias.repository.HistorialIncidenciaRepository;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import com.mx.baz.incidencias.schedule.AssignmentService;
import com.mx.baz.incidencias.events.IncidenciaEnProcesoEvent;
import com.mx.baz.incidencias.events.IncidenciaResueltaEvent;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final HistorialIncidenciaRepository historialIncidenciaRepository;
    private final AssignmentService assignmentService;
    private final IncidenciaMapper incidenciaMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public IncidenciaResponse crearIncidencia(IncidenciaRequest request) {

        Empleado empleadoAsignado = assignmentService.obtenerEmpleadoDisponible();

        Incidencia incidencia = Incidencia.builder()
                .folio(request.getFolio())
                .asunto(request.getAsunto())
                .remitente(request.getRemitente())
                .fechaCorreo(request.getFechaCorreo())
                .carpetaOrigen(request.getCarpetaOrigen())
                .prioridad(request.getPrioridad())
                .descripcion(request.getDescripcion())
                .estado(EstadoIncidencia.PENDIENTE)
                .empleadoAsignado(empleadoAsignado)
                .fechaAsignacion(LocalDateTime.now())
                .sucursal(request.getSucursal())
                .clienteUnico(request.getClienteUnico())
                .nombreCliente(request.getNombreCliente())
                .equipo(request.getEquipo())
                .motivo(request.getMotivo())
                .build();

        Incidencia incidenciaGuardada = incidenciaRepository.save(incidencia);

        assignmentService.actualizarUltimaAsignacion(empleadoAsignado);

        eventPublisher.publishEvent(new IncidenciaCreadaEvent(incidenciaGuardada));

        return incidenciaMapper.toResponse(incidenciaGuardada);
    }
    
    public IncidenciaResponse resolverIncidencia(
            String folio,
            ResolverIncidenciaRequest request) {

        Incidencia incidencia = incidenciaRepository.findByFolio(folio)
                .orElseThrow(() -> new BusinessException(ErrorCodes.INCIDENCIA_NO_ENCONTRADA,"Incidencia no encontrada"));

        if (incidencia.getEstado() == EstadoIncidencia.PENDIENTE) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_NO_TOMADA,
                    "La incidencia " + folio + " aún no ha sido tomada"
            );
        }

        if (incidencia.getEstado() == EstadoIncidencia.RESUELTA) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_RESUELTA,
                    "La incidencia " + folio + " ya fue resuelta"
            );
        }

        if (incidencia.getEstado() != EstadoIncidencia.EN_PROCESO) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_YA_TOMADA,
                    "La incidencia " + folio + " no puede resolverse porque está en estado "
                            + incidencia.getEstado()
            );
        }

        incidencia.setEstado(EstadoIncidencia.RESUELTA);
        incidencia.setFechaResolucion(LocalDateTime.now());

        incidenciaRepository.save(incidencia);

        HistorialIncidencia historial = HistorialIncidencia.builder()
                .incidencia(incidencia)
                .accion("RESUELTA")
                .usuario(request.getUsuario())
                .comentario(request.getComentario())
                .build();

        historialIncidenciaRepository.save(historial);

        eventPublisher.publishEvent(
                new IncidenciaResueltaEvent(
                        incidencia,
                        request.getUsuario(),
                        request.getComentario()
                )
        );

        return incidenciaMapper.toResponse(incidencia);
    }
    
    public List<IncidenciaResponse> obtenerPendientes() {
    	return incidenciaRepository.findByEstado(EstadoIncidencia.PENDIENTE)
                .stream()
                .map(incidenciaMapper::toResponse)
                .toList();
    }

    public IncidenciaResponse obtenerPorFolio(String folio) {
        return incidenciaRepository.findByFolio(folio)
                .map(incidenciaMapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.INCIDENCIA_NO_ENCONTRADA,
                        "Incidencia no encontrada"
                ));
    }
    
    public IncidenciaResponse tomarIncidencia(
            String folio,
            ActualizarEstadoIncidenciaRequest request) {

        Incidencia incidencia = incidenciaRepository.findByFolio(folio)
                .orElseThrow(() -> new BusinessException(ErrorCodes.INCIDENCIA_NO_ENCONTRADA,"Incidencia no encontrada"));

        if (incidencia.getEstado() == EstadoIncidencia.EN_PROCESO) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_YA_TOMADA,
                    "La incidencia " + folio + " ya fue tomada por "
                            + incidencia.getUsuarioQueLaTomo()
            );
        }

        if (incidencia.getEstado() == EstadoIncidencia.RESUELTA) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_RESUELTA,
                    "La incidencia " + folio + " ya fue resuelta"
            );
        }

        if (incidencia.getEstado() != EstadoIncidencia.PENDIENTE) {
            throw new BusinessException(ErrorCodes.INCIDENCIA_YA_TOMADA,
                    "La incidencia " + folio + " no puede ser tomada porque está en estado "
                            + incidencia.getEstado()
            );
        }

        incidencia.setEstado(EstadoIncidencia.EN_PROCESO);
        incidencia.setUsuarioQueLaTomo(request.getUsuario());
        incidencia.setFechaInicio(LocalDateTime.now());

        incidenciaRepository.save(incidencia);

        HistorialIncidencia historial = HistorialIncidencia.builder()
                .incidencia(incidencia)
                .accion("EN_PROCESO")
                .usuario(request.getUsuario())
                .comentario(request.getComentario())
                .build();

        historialIncidenciaRepository.save(historial);

        eventPublisher.publishEvent(new IncidenciaEnProcesoEvent(
        		        incidencia,
                        request.getUsuario(),
                        request.getComentario()
                )
        );

        return incidenciaMapper.toResponse(incidencia);
    }
    
    @Transactional(readOnly = true)
    public List<IncidenciaResponse> obtenerPendientesPorEmpleado(
            String usernameTelegram) {

    	if (usernameTelegram == null
    	        || usernameTelegram.isBlank()) {

    	    throw new BusinessException(
    	            ErrorCodes.USUARIO_TELEGRAM_INVALIDO,
    	            "No se recibió un usuario de Telegram válido."
    	    );
    	}

        String usernameNormalizado =
                normalizarUsername(usernameTelegram);

        List<EstadoIncidencia> estadosPendientes =
                List.of(
                        EstadoIncidencia.EN_PROCESO,
                        EstadoIncidencia.REABIERTA
                );

        return incidenciaRepository
                .findByEmpleadoAsignadoUsernameTelegramAndEstadoInOrderByFechaCorreoAsc(
                        usernameNormalizado,
                        estadosPendientes
                )
                .stream()
                .map(incidenciaMapper::toResponse)
                .toList();
    }

    private String normalizarUsername(String username) {

        String valor = username.trim();

        return valor.startsWith("@")
                ? valor.substring(1)
                : valor;
    }
    
    @Transactional(readOnly = true)
    public List<IncidenciaResponse> obtenerPendientesPorNombreEmpleado(
            String nombreEmpleado) {

        if (nombreEmpleado == null || nombreEmpleado.isBlank()) {
            throw new BusinessException(
                    ErrorCodes.EMPLEADO_NO_DISPONIBLE,
                    "Debe proporcionar el nombre del empleado."
            );
        }

        return incidenciaRepository
                .findByEmpleadoAsignadoNombreContainingIgnoreCaseAndEstadoOrderByFechaCorreoAsc(
                        nombreEmpleado.trim(),
                        EstadoIncidencia.PENDIENTE
                )
                .stream()
                .map(incidenciaMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<EmpleadoResumenOperativoResponse> obtenerResumenOperativo() {

        return empleadoRepository.findAll()
                .stream()
                .map((Empleado empleado) -> {

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
                            .totalActivas(
                                    pendientes + enProceso + reabiertas
                            )
                            .ultimaAsignacion(
                                    empleado.getUltimaAsignacion()
                            )
                            .build();
                })
                .sorted((
                        EmpleadoResumenOperativoResponse empleado1,
                        EmpleadoResumenOperativoResponse empleado2
                ) -> Long.compare(
                        empleado2.getTotalActivas(),
                        empleado1.getTotalActivas()
                ))
                .toList();
    }
    public EstadisticasResponse obtenerEstadisticas() {

        long pendientes = incidenciaRepository.countByEstado(EstadoIncidencia.PENDIENTE);

        long enProceso = incidenciaRepository.countByEstado(EstadoIncidencia.EN_PROCESO);

        long reabiertas = incidenciaRepository.countByEstado(EstadoIncidencia.REABIERTA);

        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();

        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);

        long resueltasHoy = incidenciaRepository.countByEstadoAndFechaResolucionBetween(
                EstadoIncidencia.RESUELTA,
                inicioHoy,
                finHoy
        );

        long empleadosActivos = empleadoRepository.countByActivoTrue();

        return EstadisticasResponse.builder()
                .pendientes(pendientes)
                .enProceso(enProceso)
                .reabiertas(reabiertas)
                .resueltasHoy(resueltasHoy)
                .empleadosActivos(empleadosActivos)
                .build();
    }
    
    public List<RankingEmpleadoResponse> obtenerRanking() {

        return incidenciaRepository.obtenerRankingEmpleados()
                .stream()
                .map(resultado -> RankingEmpleadoResponse.builder()
                        .empleadoId(resultado.getEmpleadoId())
                        .nombre(resultado.getNombre())
                        .pendientes(resultado.getPendientes())
                        .enProceso(resultado.getEnProceso())
                        .reabiertas(resultado.getReabiertas())
                        .resueltas(resultado.getResueltas())
                        .totalActivas(resultado.getTotalActivas())
                        .build()
                )
                .toList();
    }
    
    public BalanceResponse obtenerBalance() {

        List<BalanceEmpleadoResponse> empleados =
                incidenciaRepository.obtenerRankingEmpleados()
                        .stream()
                        .map(resultado -> {

                            long totalActivas = resultado.getTotalActivas();

                            NivelCarga nivelCarga;

                            if (totalActivas <= 1) {
                                nivelCarga = NivelCarga.BAJA;
                            } else if (totalActivas <= 4) {
                                nivelCarga = NivelCarga.MEDIA;
                            } else {
                                nivelCarga = NivelCarga.ALTA;
                            }

                            return BalanceEmpleadoResponse.builder()
                                    .empleadoId(resultado.getEmpleadoId())
                                    .nombre(resultado.getNombre())
                                    .totalActivas(totalActivas)
                                    .nivelCarga(nivelCarga)
                                    .build();
                        })
                        .sorted(
                                Comparator.comparing(
                                        BalanceEmpleadoResponse::getTotalActivas
                                ).reversed()
                        )
                        .toList();

        List<BalanceEmpleadoResponse> cargaBaja = empleados.stream()
                .filter(e -> e.getNivelCarga() == NivelCarga.BAJA)
                .toList();

        List<BalanceEmpleadoResponse> cargaMedia = empleados.stream()
                .filter(e -> e.getNivelCarga() == NivelCarga.MEDIA)
                .toList();

        List<BalanceEmpleadoResponse> cargaAlta = empleados.stream()
                .filter(e -> e.getNivelCarga() == NivelCarga.ALTA)
                .toList();

        return BalanceResponse.builder()
                .cargaBaja(cargaBaja)
                .cargaMedia(cargaMedia)
                .cargaAlta(cargaAlta)
                .build();
    }
    
    public List<IncidenciaAtrasadaResponse> obtenerIncidenciasAtrasadas(int diasMinimos) {

        int diasFiltro = Math.max(diasMinimos, 1);
        LocalDate hoy = LocalDate.now();

        return incidenciaRepository.obtenerIncidenciasAbiertas()
                .stream()
                .map(incidencia -> {

                    LocalDateTime fechaReferencia =
                            incidencia.getFechaInicio() != null
                                    ? incidencia.getFechaInicio()
                                    : incidencia.getFechaCorreo();

                    if (fechaReferencia == null) {
                        return null;
                    }

                    long diasAbierta = ChronoUnit.DAYS.between(
                            fechaReferencia.toLocalDate(),
                            hoy
                    );

                    if (diasAbierta < diasFiltro) {
                        return null;
                    }

                    String empleado = incidencia.getEmpleadoAsignado() != null
                            ? incidencia.getEmpleadoAsignado().getNombre()
                            : "Sin asignar";

                    return IncidenciaAtrasadaResponse.builder()
                            .folio(incidencia.getFolio())
                            .asunto(incidencia.getAsunto())
                            .empleado(empleado)
                            .estado(incidencia.getEstado())
                            .diasAbierta(diasAbierta)
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(
                        Comparator.comparing(
                                IncidenciaAtrasadaResponse::getDiasAbierta
                        ).reversed()
                )
                .toList();
    }
}