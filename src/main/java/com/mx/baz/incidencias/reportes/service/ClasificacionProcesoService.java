package com.mx.baz.incidencias.reportes.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mx.baz.incidencias.entity.ProcesoAfectado;
import com.mx.baz.incidencias.entity.ReglaProcesoAfectado;
import com.mx.baz.incidencias.repository.ReglaProcesoAfectadoRepository;
import com.mx.baz.incidencias.service.ExtractorMensajeActualService;

@Service
public class ClasificacionProcesoService {

	private final ReglaProcesoAfectadoRepository reglaRepository;

	private final ExtractorMensajeActualService extractorMensajeActualService;

	public ClasificacionProcesoService(
	        ReglaProcesoAfectadoRepository reglaRepository,
	        ExtractorMensajeActualService extractorMensajeActualService) {

	    this.reglaRepository = reglaRepository;
	    this.extractorMensajeActualService = extractorMensajeActualService;
	}

    public Optional<ReglaProcesoAfectado> buscarRegla(
            String asunto) {

        if (asunto == null || asunto.isBlank()) {
            return Optional.empty();
        }

        String asuntoNormalizado = normalizar(asunto);

        List<ReglaProcesoAfectado> reglas =
                reglaRepository
                .findByActivoTrueAndOrigenOrderByPrioridadAscIdAsc(
                        "ASUNTO"
                    );

        for (ReglaProcesoAfectado regla : reglas) {

            if (regla.getProceso() == null
                    || !Boolean.TRUE.equals(
                            regla.getProceso().getActivo())) {
                continue;
            }

            if (regla.getPatron() == null
                    || regla.getPatron().isBlank()) {
                continue;
            }

            String patronNormalizado =
                    normalizar(regla.getPatron());

            if (asuntoNormalizado.contains(patronNormalizado)) {
                return Optional.of(regla);
            }
        }

        return Optional.empty();
    }

    public Optional<ProcesoAfectado> identificarProceso(
            String asunto) {

        return buscarRegla(asunto)
                .map(ReglaProcesoAfectado::getProceso);
    }

    private String normalizar(String texto) {

        if (texto == null) {
            return "";
        }

        String normalizado = Normalizer.normalize(
                texto,
                Normalizer.Form.NFD
        );

        return normalizado
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }
    
    public Optional<ReglaProcesoAfectado> buscarReglaDescripcion(
            String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            return Optional.empty();
        }

        String mensajeActual =
                extractorMensajeActualService.extraer(descripcion);

        String textoNormalizado = normalizar(mensajeActual);

        List<ReglaProcesoAfectado> reglas =
        		reglaRepository
                    .findByActivoTrueAndOrigenOrderByPrioridadAscIdAsc(
                        "DESCRIPCION"
                    );

        for (ReglaProcesoAfectado regla : reglas) {

            if (regla.getProceso() == null
                    || !Boolean.TRUE.equals(
                            regla.getProceso().getActivo())) {
                continue;
            }

            String patronNormalizado =
                    normalizar(regla.getPatron());

            if (!patronNormalizado.isBlank()
                    && textoNormalizado.contains(patronNormalizado)) {

                return Optional.of(regla);
            }
        }

        return Optional.empty();
    }
}
