package com.mx.baz.incidencias.reportes.service;

import com.mx.baz.incidencias.repository.IncidenciaRepository;
import com.mx.baz.incidencias.repository.projection.ReporteEstadoProjection;
import com.mx.baz.incidencias.reportes.dto.ReporteTiemposResponse;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import com.mx.baz.incidencias.entity.Incidencia;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mx.baz.incidencias.repository.projection.ReporteEmpleadoProjection;

@Service
@RequiredArgsConstructor
public class ReporteExcelService {

    private final IncidenciaRepository incidenciaRepository;

    private final ReporteService reporteService;


    @Transactional(readOnly = true)
    public byte[] generarReporte(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        try (
                Workbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            crearHojaResumen(
                    workbook,
                    fechaDesde,
                    fechaHasta
            );
            
            crearHojaIncidencias(
                    workbook,
                    fechaDesde,
                    fechaHasta
            );
            
            crearHojaPorEmpleado(
                    workbook,
                    fechaDesde,
                    fechaHasta
            );

            workbook.write(
                    outputStream
            );


            return outputStream
                    .toByteArray();


        } catch (Exception error) {

            throw new RuntimeException(
                    "No fue posible generar el reporte Excel.",
                    error
            );
        }
    }


    private void crearHojaResumen(
            Workbook workbook,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        Sheet sheet =
                workbook.createSheet(
                        "Resumen"
                );


        /*
         * ==========================
         * ESTILOS
         * ==========================
         */

        CellStyle tituloStyle =
                workbook.createCellStyle();

        Font tituloFont =
                workbook.createFont();

        tituloFont.setBold(true);

        tituloFont.setFontHeightInPoints(
                (short) 16
        );

        tituloStyle.setFont(
                tituloFont
        );


        CellStyle encabezadoStyle =
                workbook.createCellStyle();

        Font encabezadoFont =
                workbook.createFont();

        encabezadoFont.setBold(true);

        encabezadoStyle.setFont(
                encabezadoFont
        );


        /*
         * ==========================
         * DATOS
         * ==========================
         */

        List<ReporteEstadoProjection> estados =
                reporteService
                        .obtenerIncidenciasPorEstado(
                                fechaDesde,
                                fechaHasta
                        );


        ReporteTiemposResponse tiempos =
                reporteService
                        .obtenerTiemposPromedio(
                                fechaDesde,
                                fechaHasta
                        );


        long total = 0;
        long pendientes = 0;
        long enProceso = 0;
        long resueltas = 0;
        long reabiertas = 0;
        long canceladas = 0;


        for (
                ReporteEstadoProjection estado
                : estados
        ) {

            long cantidad =
                    estado.getTotal() != null
                            ? estado.getTotal()
                            : 0;


            total += cantidad;


            switch (estado.getEstado()) {

                case PENDIENTE ->
                        pendientes = cantidad;

                case EN_PROCESO ->
                        enProceso = cantidad;

                case RESUELTA ->
                        resueltas = cantidad;

                case REABIERTA ->
                        reabiertas = cantidad;

                case CANCELADA ->
                        canceladas = cantidad;

                default -> {
                    // Sin acción.
                }
            }
        }


        double porcentajeResolucion =
                total > 0
                        ? (
                            (double) resueltas
                            / total
                        ) * 100
                        : 0;


        /*
         * ==========================
         * CONTENIDO
         * ==========================
         */

        int rowIndex = 0;


        Row titulo =
                sheet.createRow(
                        rowIndex++
                );

        Cell tituloCell =
                titulo.createCell(0);

        tituloCell.setCellValue(
                "Reporte SGIO"
        );

        tituloCell.setCellStyle(
                tituloStyle
        );


        rowIndex++;


        crearFila(
                sheet,
                rowIndex++,
                "Fecha desde",
                fechaDesde.toString(),
                encabezadoStyle
        );


        crearFila(
                sheet,
                rowIndex++,
                "Fecha hasta",
                fechaHasta.toString(),
                encabezadoStyle
        );


        rowIndex++;


        crearFila(
                sheet,
                rowIndex++,
                "Total",
                total,
                encabezadoStyle
        );


        crearFila(
                sheet,
                rowIndex++,
                "Pendientes",
                pendientes,
                encabezadoStyle
        );


        crearFila(
                sheet,
                rowIndex++,
                "En proceso",
                enProceso,
                encabezadoStyle
        );


        crearFila(
                sheet,
                rowIndex++,
                "Resueltas",
                resueltas,
                encabezadoStyle
        );


        crearFila(
                sheet,
                rowIndex++,
                "Reabiertas",
                reabiertas,
                encabezadoStyle
        );


        crearFila(
                sheet,
                rowIndex++,
                "Canceladas",
                canceladas,
                encabezadoStyle
        );


        crearFila(
                sheet,
                rowIndex++,
                "% resolución",
                porcentajeResolucion,
                encabezadoStyle
        );


        crearFila(
                sheet,
                rowIndex++,
                "Promedio atención (min)",
                tiempos
                        .getTiempoPromedioAtencionMinutos(),
                encabezadoStyle
        );


        crearFila(
                sheet,
                rowIndex++,
                "Promedio resolución (min)",
                tiempos
                        .getTiempoPromedioResolucionMinutos(),
                encabezadoStyle
        );


        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    

    private void crearFila(
            Sheet sheet,
            int rowIndex,
            String etiqueta,
            String valor,
            CellStyle encabezadoStyle
    ) {

        Row row =
                sheet.createRow(
                        rowIndex
                );


        Cell etiquetaCell =
                row.createCell(0);

        etiquetaCell.setCellValue(
                etiqueta
        );

        etiquetaCell.setCellStyle(
                encabezadoStyle
        );


        row.createCell(1)
                .setCellValue(
                        valor
                );
    }


    private void crearFila(
            Sheet sheet,
            int rowIndex,
            String etiqueta,
            double valor,
            CellStyle encabezadoStyle
    ) {

        Row row =
                sheet.createRow(
                        rowIndex
                );


        Cell etiquetaCell =
                row.createCell(0);

        etiquetaCell.setCellValue(
                etiqueta
        );

        etiquetaCell.setCellStyle(
                encabezadoStyle
        );


        row.createCell(1)
                .setCellValue(
                        valor
                );
    }
    
    private void crearHojaIncidencias(
            Workbook workbook,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        Sheet sheet =
                workbook.createSheet(
                        "Incidencias"
                );


        LocalDateTime inicio =
                fechaDesde.atStartOfDay();

        LocalDateTime finExclusivo =
                fechaHasta
                        .plusDays(1)
                        .atStartOfDay();


        List<Incidencia> incidencias =
                incidenciaRepository
                        .obtenerIncidenciasParaReporte(
                                inicio,
                                finExclusivo
                        );


        /*
         * ==========================
         * ESTILO ENCABEZADOS
         * ==========================
         */

        CellStyle headerStyle =
                workbook.createCellStyle();

        Font headerFont =
                workbook.createFont();

        headerFont.setBold(true);

        headerStyle.setFont(
                headerFont
        );


        /*
         * ==========================
         * ENCABEZADOS
         * ==========================
         */

        String[] columnas = {
                "Folio",
                "Fecha creación",
                "Asunto",
                "Prioridad",
                "Estado",
                "Empleado asignado",
                "Origen",
                "Sucursal",
                "Cliente único",
                "Nombre cliente",
                "Fecha inicio",
                "Fecha resolución"
        };


        Row header =
                sheet.createRow(0);


        for (
                int i = 0;
                i < columnas.length;
                i++
        ) {

            Cell cell =
                    header.createCell(i);

            cell.setCellValue(
                    columnas[i]
            );

            cell.setCellStyle(
                    headerStyle
            );
        }


        /*
         * ==========================
         * FORMATO FECHA
         * ==========================
         */

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );


        /*
         * ==========================
         * DATOS
         * ==========================
         */

        int rowIndex = 1;


        for (Incidencia incidencia : incidencias) {

            Row row =
                    sheet.createRow(
                            rowIndex++
                    );


            int columnIndex = 0;


            row.createCell(columnIndex++)
                    .setCellValue(
                            valorSeguro(
                                    incidencia.getFolio()
                            )
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            formatearFecha(
                                    incidencia.getCreatedAt(),
                                    formatter
                            )
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            valorSeguro(
                                    incidencia.getAsunto()
                            )
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            incidencia.getPrioridad() != null
                                    ? incidencia
                                        .getPrioridad()
                                        .name()
                                    : ""
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            incidencia.getEstado() != null
                                    ? incidencia
                                        .getEstado()
                                        .name()
                                    : ""
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            incidencia.getEmpleadoAsignado() != null
                                    ? valorSeguro(
                                        incidencia
                                            .getEmpleadoAsignado()
                                            .getNombre()
                                    )
                                    : "Sin asignar"
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            valorSeguro(
                                    incidencia.getCarpetaOrigen()
                            )
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            valorSeguro(
                                    incidencia.getSucursal()
                            )
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            valorSeguro(
                                    incidencia.getClienteUnico()
                            )
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            valorSeguro(
                                    incidencia.getNombreCliente()
                            )
                    );


            row.createCell(columnIndex++)
                    .setCellValue(
                            formatearFecha(
                                    incidencia.getFechaInicio(),
                                    formatter
                            )
                    );


            row.createCell(columnIndex)
                    .setCellValue(
                            formatearFecha(
                                    incidencia.getFechaResolucion(),
                                    formatter
                            )
                    );
        }


        /*
         * ==========================
         * AJUSTE COLUMNAS
         * ==========================
         */

        for (
                int i = 0;
                i < columnas.length;
                i++
        ) {

            sheet.autoSizeColumn(i);
        }


        /*
         * Dejamos fijo el encabezado
         * al desplazarnos por Excel.
         */

        sheet.createFreezePane(
                0,
                1
        );


        /*
         * Filtro automático.
         */

        if (!incidencias.isEmpty()) {

            sheet.setAutoFilter(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            0,
                            incidencias.size(),
                            0,
                            columnas.length - 1
                    )
            );
        }
    }
    
    private String valorSeguro(
            String valor
    ) {

        return valor != null
                ? valor
                : "";
    }


    private String formatearFecha(
            LocalDateTime fecha,
            DateTimeFormatter formatter
    ) {

        return fecha != null
                ? fecha.format(formatter)
                : "";
    }
    
    private void crearHojaPorEmpleado(
            Workbook workbook,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        Sheet sheet =
                workbook.createSheet(
                        "Por empleado"
                );


        List<ReporteEmpleadoProjection> empleados =
                reporteService
                        .obtenerIncidenciasPorEmpleado(
                                fechaDesde,
                                fechaHasta
                        );


        CellStyle headerStyle =
                workbook.createCellStyle();

        Font headerFont =
                workbook.createFont();

        headerFont.setBold(true);

        headerStyle.setFont(
                headerFont
        );


        String[] columnas = {
                "Empleado",
                "Total de incidencias"
        };


        Row header =
                sheet.createRow(0);


        for (
                int i = 0;
                i < columnas.length;
                i++
        ) {

            Cell cell =
                    header.createCell(i);

            cell.setCellValue(
                    columnas[i]
            );

            cell.setCellStyle(
                    headerStyle
            );
        }


        int rowIndex = 1;


        for (
                ReporteEmpleadoProjection empleado
                : empleados
        ) {

            Row row =
                    sheet.createRow(
                            rowIndex++
                    );


            row.createCell(0)
                    .setCellValue(
                            empleado.getNombre() != null
                                    ? empleado.getNombre()
                                    : "Sin nombre"
                    );


            row.createCell(1)
                    .setCellValue(
                            empleado.getTotal() != null
                                    ? empleado.getTotal()
                                    : 0
                    );
        }


        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);


        sheet.createFreezePane(
                0,
                1
        );


        if (!empleados.isEmpty()) {

            sheet.setAutoFilter(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            0,
                            empleados.size(),
                            0,
                            columnas.length - 1
                    )
            );
        }
    }
}
