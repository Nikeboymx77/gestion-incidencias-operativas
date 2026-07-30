package com.mx.baz.incidencias.dashboard.controller;

import com.mx.baz.incidencias.dashboard.service.DashboardService;
import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
            Model model) {

        Page<Incidencia> paginaIncidencias =
                dashboardService.buscarIncidencias(
                        texto,
                        estado,
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
        model.addAttribute("estadoSeleccionado", estado); model.addAttribute("estados", EstadoIncidencia.values());

        /*
         * Totales generales para las tarjetas.
         * Estos conteos no dependen de la página actual.
         */
        model.addAttribute(
                "total",
                dashboardService.contarTotal()
        );

        model.addAttribute(
                "pendientes",
                dashboardService.contarPendientes()
        );

        model.addAttribute(
                "enProceso",
                dashboardService.contarEnProceso()
        );

        model.addAttribute(
                "resueltas",
                dashboardService.contarResueltas()
        );

        model.addAttribute(
                "reabiertas",
                dashboardService.contarReabiertas()
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
}