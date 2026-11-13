package com.mx.baz.incidencias.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.entity.ProcesoAfectado;
import com.mx.baz.incidencias.reportes.service.ClasificacionProcesoService;
import com.mx.baz.incidencias.repository.IncidenciaRepository;

@Service
public class ReporteProcesosService {

    private final IncidenciaRepository incidenciaRepository;
    private final ClasificacionProcesoService clasificacionProcesoService;
    private final ExtractorMensajeActualService extractorMensajeActualService;

    public ReporteProcesosService(
            IncidenciaRepository incidenciaRepository,
            ClasificacionProcesoService clasificacionProcesoService,
            ExtractorMensajeActualService extractorMensajeActualService) {

        this.incidenciaRepository = incidenciaRepository;
        this.clasificacionProcesoService = clasificacionProcesoService;
        this.extractorMensajeActualService = extractorMensajeActualService;
    }

    public Map<String, Object> generarReporte(
            LocalDateTime desde,
            LocalDateTime hasta) {

        if (desde == null || hasta == null
                || !desde.isBefore(hasta)) {
            throw new IllegalArgumentException(
                    "El rango de fechas no es válido");
        }

        List<Incidencia> incidencias =
                incidenciaRepository.buscarParaReporte(desde, hasta);

        Map<String, Long> conteos = new HashMap<>();

        int sinDetectar = 0;

        for (Incidencia incidencia : incidencias) {


			Optional<ProcesoAfectado> proceso =
			        clasificacionProcesoService.identificarProceso(
			                incidencia.getAsunto()
			        );
			
			// Si no se identifica por ASUNTO,
			// intentamos clasificar por DESCRIPCION.
			if (proceso.isEmpty()) {
			
			    proceso = clasificacionProcesoService
			            .buscarReglaDescripcion(
			                    incidencia.getDescripcion()
			            )
			            .map(regla -> regla.getProceso());
			}


            if (proceso.isPresent()) {

                String codigo = proceso.get().getCodigo();

                conteos.merge(codigo, 1L, Long::sum);

            } else {
                sinDetectar++;
            }
        }

        List<Map<String, Object>> top10 = new ArrayList<>();

        conteos.entrySet().stream()
                .sorted(
                    Comparator
                        .<Map.Entry<String, Long>, Long>
                            comparing(Map.Entry::getValue)
                        .reversed()
                        .thenComparing(Map.Entry::getKey)
                )
                .limit(10)
                .forEach(entry -> {

                    Map<String, Object> fila =
                            new LinkedHashMap<>();

                    fila.put("proceso", entry.getKey());
                    fila.put("total", entry.getValue());

                    top10.add(fila);
                });

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("desde", desde);
        respuesta.put("hastaExclusivo", hasta);
        respuesta.put("totalIncidencias", incidencias.size());
        respuesta.put(
                "conProcesoDetectado",
                incidencias.size() - sinDetectar
        );
        respuesta.put("sinProcesoDetectado", sinDetectar);
        respuesta.put("top10", top10);

        return respuesta;
    }
    

	public List<Map<String, Object>> obtenerSinDetectar(
	        LocalDateTime desde,
	        LocalDateTime hasta) {
	
	    if (desde == null || hasta == null
	            || !desde.isBefore(hasta)) {
	        throw new IllegalArgumentException(
	                "El rango de fechas no es válido");
	    }
	
	    List<Incidencia> incidencias =
	            incidenciaRepository.buscarParaReporte(desde, hasta);
	
	    List<Map<String, Object>> resultado = new ArrayList<>();
	
	    for (Incidencia incidencia : incidencias) {
	
	        Optional<ProcesoAfectado> proceso =
	                clasificacionProcesoService.identificarProceso(
	                        incidencia.getAsunto());
	
	        if (proceso.isEmpty()) {
	            proceso = clasificacionProcesoService
	                    .buscarReglaDescripcion(
	                            incidencia.getDescripcion())
	                    .map(regla -> regla.getProceso());
	        }
	
	        if (proceso.isPresent()) {
	            continue;
	        }
	
	        Map<String, Object> fila = new LinkedHashMap<>();
	

			fila.put("incidenciaId", incidencia.getId());
			fila.put("folio", incidencia.getFolio());
			fila.put("asunto", incidencia.getAsunto());
			
			// Obtener únicamente el mensaje actual,
			// eliminando el historial de correos.
			String mensajeActual =
			        extractorMensajeActualService
			                .extraer(
			                        incidencia.getDescripcion()
			                );
			
			fila.put("mensajeActual", mensajeActual);
			
			resultado.add(fila);

	    }
	
	    return resultado;
	}

}
