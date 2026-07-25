package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.entity.SeguimientoIncidencia;
import com.mx.baz.incidencias.enums.TipoSeguimiento;
import com.mx.baz.incidencias.integration.mail.dto.CorreoDTO;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import com.mx.baz.incidencias.repository.SeguimientoIncidenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeguimientoIncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final SeguimientoIncidenciaRepository seguimientoRepository;
    private final ClasificadorCorreoService clasificadorCorreoService;
    private final IncidenciaEstadoService incidenciaEstadoService;

    /**
     * Busca una incidencia por folio.
     *
     * Si existe, guarda el correo como seguimiento y devuelve true.
     * Si no existe, devuelve false para que MailProcessor cree
     * una nueva incidencia.
     */
    @Transactional
    public boolean registrarSiExiste(
            String folio,
            CorreoDTO correo) {

        if (folio == null || folio.isBlank()) {
            return false;
        }

        Optional<Incidencia> incidenciaExistente =
                incidenciaRepository.findByFolio(folio);

        if (incidenciaExistente.isEmpty()) {
            return false;
        }

        Incidencia incidencia = incidenciaExistente.get();
        
        TipoSeguimiento tipoSeguimiento =
                clasificadorCorreoService.clasificar(correo);
        
        boolean requiereAtencion =
                requiereAtencion(tipoSeguimiento);

        SeguimientoIncidencia seguimiento =
                SeguimientoIncidencia.builder()
                        .incidencia(incidencia)
                        .asunto(correo.getAsunto())
                        .remitente(correo.getRemitente())
                        .contenido(correo.getDescripcion())
                        .fechaCorreo(correo.getFechaCorreo())
                        .tipo(tipoSeguimiento)
                        .requiereAtencion(requiereAtencion)
                        .build();

        seguimientoRepository.save(seguimiento);

        boolean estadoModificado =
                incidenciaEstadoService.evaluarCambioEstado(
                        incidencia,
                        tipoSeguimiento
                );

        log.info(
                "Correo registrado como seguimiento. " +
                "Folio: {}, idCorreo: {}, estadoActual: {}, " +
                "tipoSeguimiento: {}, requiereAtencion: {}, " +
                "estadoModificado: {}",
                folio,
                correo.getIdCorreo(),
                incidencia.getEstado(),
                tipoSeguimiento,
                requiereAtencion,
                estadoModificado
        );

        return true;
    }
    
    private boolean requiereAtencion(
            TipoSeguimiento tipoSeguimiento) {

        return switch (tipoSeguimiento) {
            case PERSISTE_ERROR,
                 REASIGNACION,
                 ESCALAMIENTO -> true;

            case RESPUESTA,
                 CONFIRMACION_SOLUCION,
                 INFORMATIVO -> false;
        };
    }
}