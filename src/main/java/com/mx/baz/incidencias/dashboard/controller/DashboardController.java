package com.mx.baz.incidencias.dashboard.controller;

import com.mx.baz.incidencias.dashboard.service.DashboardService;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.mx.baz.incidencias.enums.PrioridadIncidencia;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping({"/", "/dashboard"})
    public String mostrarDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoIncidencia estado,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false)
            PrioridadIncidencia prioridad,
            @RequestParam(required = false)
            String carpetaOrigen,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaHasta,

            Model model
    ) {

    	cargarDashboard(
    	        model,
    	        texto,
    	        estado,
    	        empleadoId,
    	        prioridad,
    	        carpetaOrigen,
    	        fechaDesde,
    	        fechaHasta,
    	        page,
    	        size
    	);

        return "dashboard";
    }

    @GetMapping("/dashboard/incidencias/{folio}")
    public String mostrarDetalle(
            @PathVariable String folio,
            Model model) {

        model.addAttribute(
                "incidencia",
                dashboardService.obtenerDetalle(folio)
        );

        return "incidencia-detalle";
    }

    @GetMapping("/dashboard/fragment/incidencias")
    public String actualizarTabla(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoIncidencia estado,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false)
            PrioridadIncidencia prioridad,
            @RequestParam(required = false)
            String carpetaOrigen,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaHasta,

            Model model
    ) {

    	cargarDashboard(
    	        model,
    	        texto,
    	        estado,
    	        empleadoId,
    	        prioridad,
    	        carpetaOrigen,
    	        fechaDesde,
    	        fechaHasta,
    	        page,
    	        size
    	);

        return "fragments/incidencias-table :: tablaIncidencias";
    }

    @GetMapping("/dashboard/fragment/cards")
    public String actualizarCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoIncidencia estado,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false)
            PrioridadIncidencia prioridad,
            @RequestParam(required = false)
            String carpetaOrigen,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaHasta,

            Model model
    ) {

    	cargarDashboard(
    	        model,
    	        texto,
    	        estado,
    	        empleadoId,
    	        prioridad,
    	        carpetaOrigen,
    	        fechaDesde,
    	        fechaHasta,
    	        page,
    	        size
    	);

        return "fragments/dashboard-cards :: dashboardCards";
    }

    private void cargarDashboard(
            Model model,
            String texto,
            EstadoIncidencia estado,
            Long empleadoId,
            PrioridadIncidencia prioridad,
            String carpetaOrigen,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            int page,
            int size
    ) {

    	Page<Incidencia> paginaIncidencias =
    			dashboardService.buscarIncidencias(
    			        texto,
    			        estado,
    			        empleadoId,
    			        prioridad,
    			        carpetaOrigen,
    			        fechaDesde,
    			        fechaHasta,
    			        page,
    			        size
    			);

        model.addAttribute(
                "incidencias",
                paginaIncidencias.getContent()
        );

        model.addAttribute(
                "paginaActual",
                paginaIncidencias.getNumber()
        );

        model.addAttribute(
                "totalPaginas",
                paginaIncidencias.getTotalPages()
        );

        model.addAttribute(
                "totalRegistros",
                paginaIncidencias.getTotalElements()
        );

        model.addAttribute("texto", texto);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("empleadoSeleccionado", empleadoId);
        
        model.addAttribute(
                "fechaDesde",
                fechaDesde
        );

        model.addAttribute(
                "fechaHasta",
                fechaHasta
        );

        model.addAttribute(
                "estados",
                EstadoIncidencia.values()
        );

        model.addAttribute(
                "empleados",
                dashboardService.obtenerEmpleadosActivos()
        );

        var metricas =
        		dashboardService.obtenerMetricas(
        		        texto,
        		        estado,
        		        empleadoId,
        		        prioridad,
        		        carpetaOrigen,
        		        fechaDesde,
        		        fechaHasta
        		);


        model.addAttribute(
                "total",
                metricas.getTotal()
        );

        model.addAttribute(
                "pendientes",
                metricas.getPendientes()
        );

        model.addAttribute(
                "enProceso",
                metricas.getEnProceso()
        );

        model.addAttribute(
                "resueltas",
                metricas.getResueltas()
        );

        model.addAttribute(
                "reabiertas",
                metricas.getReabiertas()
        );

        model.addAttribute(
                "canceladas",
                metricas.getCanceladas()
        );
        
        model.addAttribute(
                "prioridadSeleccionada",
                prioridad
        );

        model.addAttribute(
                "prioridades",
                PrioridadIncidencia.values()
        );
        
        model.addAttribute(
                "origenSeleccionado",
                carpetaOrigen
        );

        model.addAttribute(
                "origenes",
                dashboardService
                        .obtenerOrigenesDisponibles()
        );
    }
}