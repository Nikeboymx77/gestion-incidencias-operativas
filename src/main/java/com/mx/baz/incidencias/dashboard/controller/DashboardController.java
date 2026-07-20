package com.mx.baz.incidencias.dashboard.controller;

import com.mx.baz.incidencias.dashboard.service.DashboardService;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping({"/", "/dashboard"})
    public String mostrarDashboard(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoIncidencia estado,
            Model model) {

        model.addAttribute("total", dashboardService.contarTotal());
        model.addAttribute("pendientes", dashboardService.contarPendientes());
        model.addAttribute("enProceso", dashboardService.contarEnProceso());
        model.addAttribute("resueltas", dashboardService.contarResueltas());

        model.addAttribute(
                "incidencias",
                dashboardService.buscarIncidencias(texto, estado)
        );

        model.addAttribute("texto", texto);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("estados", EstadoIncidencia.values());

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