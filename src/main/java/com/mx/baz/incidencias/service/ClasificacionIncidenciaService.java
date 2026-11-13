package com.mx.baz.incidencias.service;

import com.mx.baz.incidencias.entity.CategoriaProblema;
import com.mx.baz.incidencias.entity.ReglaClasificacion;
import com.mx.baz.incidencias.repository.IncidenciaClasificacionRepository;
import com.mx.baz.incidencias.repository.IncidenciaRepository;
import com.mx.baz.incidencias.repository.ReglaClasificacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.entity.IncidenciaClasificacion;
import com.mx.baz.incidencias.enums.EstadoClasificacion;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClasificacionIncidenciaService {

    private final ReglaClasificacionRepository reglaRepository;
    private final IncidenciaRepository incidenciaRepository;

    private final IncidenciaClasificacionRepository
            incidenciaClasificacionRepository;
    
    private final ExtractorMensajeActualService extractorMensajeActualService;

    /**
     * Busca la primera regla aplicable a la descripción.
     */
    public Optional<ReglaClasificacion> buscarRegla(
            String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            return Optional.empty();
        }

        String mensajeActual =
                extractorMensajeActualService.extraer(descripcion);

        String textoNormalizado =
                normalizarTexto(mensajeActual);

        List<ReglaClasificacion> reglas =
                reglaRepository
                    .findByActivoTrueOrderByPrioridadAscIdAsc();

        for (ReglaClasificacion regla : reglas) {

            if (regla.getCategoria() == null
                    || !Boolean.TRUE.equals(
                            regla.getCategoria().getActivo())) {
                continue;
            }

            String patronNormalizado =
                    normalizarTexto(regla.getPatron());

            if (!patronNormalizado.isBlank()
                    && textoNormalizado.contains(patronNormalizado)) {

                log.debug(
                    "Regla de clasificación encontrada: {}",
                    regla.getId()
                );

                return Optional.of(regla);
            }
        }

        return Optional.empty();
    }

    /**
     * Devuelve la categoría identificada, si existe.
     */
    public Optional<CategoriaProblema> identificarCategoria(
            String descripcion) {

        return buscarRegla(descripcion)
                .map(ReglaClasificacion::getCategoria);
    }

    /**
     * Normaliza acentos, mayúsculas y espacios.
     */
    private String normalizarTexto(String texto) {

        if (texto == null) {
            return "";
        }

        String normalizado = Normalizer.normalize(
                texto,
                Normalizer.Form.NFD
        );

        normalizado = Pattern
                .compile("\\p{M}+")
                .matcher(normalizado)
                .replaceAll("");

        return normalizado
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }
    
    @Transactional
    public IncidenciaClasificacion clasificarYGuardar(Long incidenciaId) {

        Incidencia incidencia = incidenciaRepository
                .findById(incidenciaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Incidencia no encontrada: " + incidenciaId
                ));

        // Si ya existe una clasificación, no la sobrescribimos.
        Optional<IncidenciaClasificacion> existente =
                incidenciaClasificacionRepository
                        .findByIncidenciaId(incidenciaId);

        if (existente.isPresent()) {
            return existente.get();
        }

        Optional<ReglaClasificacion> reglaOpt =
                buscarRegla(incidencia.getDescripcion());

        IncidenciaClasificacion clasificacion =
                new IncidenciaClasificacion();

        clasificacion.setIncidencia(incidencia);
        clasificacion.setFechaClasificacion(LocalDateTime.now());

        if (reglaOpt.isPresent()) {

            ReglaClasificacion regla = reglaOpt.get();

            clasificacion.setCategoria(regla.getCategoria());
            clasificacion.setRegla(regla);
            clasificacion.setVersionRegla(regla.getVersion());
            clasificacion.setEstado(EstadoClasificacion.CLASIFICADA);

        } else {

            clasificacion.setEstado(
                    EstadoClasificacion.SIN_CLASIFICAR
            );
        }

        return incidenciaClasificacionRepository.save(clasificacion);
    }
}
