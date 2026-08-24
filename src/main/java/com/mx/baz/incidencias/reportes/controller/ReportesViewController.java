package com.mx.baz.incidencias.reportes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportesViewController {

    @GetMapping("/reportes")
    public String mostrarReportes() {

        return "reportes";
    }
}
