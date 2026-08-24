document.addEventListener("DOMContentLoaded", () => {

    const fechaDesde =
        document.getElementById("fechaDesde");

    const fechaHasta =
        document.getElementById("fechaHasta");

    const botonesPeriodo =
        document.querySelectorAll(".period-button");
		
	const btnConsultar =
	    document.getElementById("btnConsultarReporte");

	const reporteTotal =
	    document.getElementById("reporteTotal");

	const reporteResueltas =
	    document.getElementById("reporteResueltas");

	const reportePendientes =
	    document.getElementById("reportePendientes");

	const reporteEnProceso =
	    document.getElementById("reporteEnProceso");
		
	const chartIncidenciasDiaCanvas =
	    document.getElementById(
	        "chartIncidenciasDia"
	    );

	let chartIncidenciasDia = null;
	
	const chartIncidenciasEstadoCanvas =
	    document.getElementById(
	        "chartIncidenciasEstado"
	    );

	let chartIncidenciasEstado = null;
	
	const chartIncidenciasEmpleadoCanvas =
	    document.getElementById(
	        "chartIncidenciasEmpleado"
	    );

	let chartIncidenciasEmpleado = null;
	
	const reporteReabiertas =
	    document.getElementById(
	        "reporteReabiertas"
	    );

	const reporteCanceladas =
	    document.getElementById(
	        "reporteCanceladas"
	    );
		
	const porcentajeResolucion =
	    document.getElementById(
	        "porcentajeResolucion"
	    );

	const porcentajeResolucionBar =
	    document.getElementById(
	        "porcentajeResolucionBar"
	    );
		
	const tiempoPromedioAtencion =
	    document.getElementById(
	        "tiempoPromedioAtencion"
	    );

	const tiempoPromedioResolucion =
	    document.getElementById(
	        "tiempoPromedioResolucion"
	    );
	
	const btnExportarExcel =
	    document.getElementById(
	        "btnExportarExcel"
	    );


    function formatearFecha(fecha) {

        const year =
            fecha.getFullYear();

        const month =
            String(fecha.getMonth() + 1)
                .padStart(2, "0");

        const day =
            String(fecha.getDate())
                .padStart(2, "0");

        return `${year}-${month}-${day}`;
    }


    function limpiarSeleccionPeriodo() {

        botonesPeriodo.forEach(
            boton =>
                boton.classList.remove("active")
        );
    }


    function seleccionarPeriodo(periodo) {

        const hoy =
            new Date();

        let desde =
            new Date(hoy);

        let hasta =
            new Date(hoy);


        switch (periodo) {

            case "today":

                break;


            case "week":

                const diaSemana =
                    hoy.getDay();

                const diferenciaLunes =
                    diaSemana === 0
                        ? -6
                        : 1 - diaSemana;

                desde.setDate(
                    hoy.getDate()
                    + diferenciaLunes
                );

                break;


            case "month":

                desde =
                    new Date(
                        hoy.getFullYear(),
                        hoy.getMonth(),
                        1
                    );

                break;


            case "previous-month":

                desde =
                    new Date(
                        hoy.getFullYear(),
                        hoy.getMonth() - 1,
                        1
                    );

                hasta =
                    new Date(
                        hoy.getFullYear(),
                        hoy.getMonth(),
                        0
                    );

                break;
        }


        fechaDesde.value =
            formatearFecha(desde);

        fechaHasta.value =
            formatearFecha(hasta);
    }


    botonesPeriodo.forEach(boton => {

        boton.addEventListener(
            "click",
            () => {

                limpiarSeleccionPeriodo();

                boton.classList.add(
                    "active"
                );

                seleccionarPeriodo(
                    boton.dataset.period
                );
            }
        );

    });


    fechaDesde.addEventListener(
        "change",
        limpiarSeleccionPeriodo
    );


    fechaHasta.addEventListener(
        "change",
        limpiarSeleccionPeriodo
    );


    /*
     * Periodo inicial:
     * Este mes
     */

    const botonMes =
        document.querySelector(
            '[data-period="month"]'
        );

    if (botonMes) {

        botonMes.classList.add(
            "active"
        );

        seleccionarPeriodo(
            "month"
        );
		
		cargarMetricas();
    }
	
	async function cargarMetricas() {

	    const desde =
	        fechaDesde.value;

	    const hasta =
	        fechaHasta.value;


	    if (!desde || !hasta) {

	        alert(
	            "Selecciona una fecha desde y una fecha hasta."
	        );

	        return;
	    }


	    if (desde > hasta) {

	        alert(
	            "La fecha desde no puede ser mayor que la fecha hasta."
	        );

	        return;
	    }


	    const url =
	        `/api/reportes/incidencias-por-estado`
	        + `?fechaDesde=${encodeURIComponent(desde)}`
	        + `&fechaHasta=${encodeURIComponent(hasta)}`;


	    try {

	        btnConsultar.disabled = true;

	        const response =
	            await fetch(url);


	        if (!response.ok) {

	            throw new Error(
	                `Error HTTP ${response.status}`
	            );
	        }


	        const datos =
	            await response.json();


	        actualizarMetricas(datos);
			
			actualizarGraficaPorEstado(
						    datos
						);
			
			await cargarIncidenciasPorDia();
			
			await cargarIncidenciasPorEmpleado();
			
			await cargarTiemposPromedio();		

	    } catch (error) {

	        console.error(
	            "Error cargando métricas del reporte:",
	            error
	        );

	        alert(
	            "No fue posible consultar el reporte."
	        );


	    } finally {

	        btnConsultar.disabled = false;

	    }
	}



	function actualizarMetricas(datos) {

	    let total = 0;

	    let resueltas = 0;

	    let pendientes = 0;

	    let enProceso = 0;
		
		let reabiertas = 0;
		let canceladas = 0;


	    datos.forEach(item => {

	        const cantidad =
	            Number(item.total || 0);

	        total += cantidad;


	        switch (item.estado) {

	            case "RESUELTA":

	                resueltas =
	                    cantidad;

	                break;


	            case "PENDIENTE":

	                pendientes =
	                    cantidad;

	                break;


	            case "EN_PROCESO":

	                enProceso =
	                    cantidad;

	                break;
					
				case "REABIERTA":

				    reabiertas =
				        cantidad;

				    break;


				case "CANCELADA":

				    canceladas =
				        cantidad;

				    break;

	        }

	    });


	    reporteTotal.textContent =
	        total;

	    reporteResueltas.textContent =
	        resueltas;

	    reportePendientes.textContent =
	        pendientes;

	    reporteEnProceso.textContent =
	        enProceso;
			
		reporteReabiertas.textContent =
		    reabiertas;

		reporteCanceladas.textContent =
		    canceladas;
		actualizarPeriodoAnalizado(total);
		
		const porcentaje =
		    total > 0
		        ? (resueltas / total) * 100
		        : 0;
		porcentajeResolucion.textContent =
		    `${porcentaje.toFixed(1)}%`;

		porcentajeResolucionBar.style.width =
		    `${Math.min(porcentaje, 100)}%`;
	}
	
	btnConsultar.addEventListener(
	    "click",
	    cargarMetricas
	);
	
	async function cargarIncidenciasPorDia() {

	    const desde =
	        fechaDesde.value;

	    const hasta =
	        fechaHasta.value;


	    const url =
	        `/api/reportes/incidencias-por-dia`
	        + `?fechaDesde=${encodeURIComponent(desde)}`
	        + `&fechaHasta=${encodeURIComponent(hasta)}`;


	    const response =
	        await fetch(url);


	    if (!response.ok) {

	        throw new Error(
	            `Error HTTP ${response.status}`
	        );
	    }


	    const datos =
	        await response.json();


	    actualizarGraficaPorDia(
	        datos
	    );
	}
	
	function actualizarGraficaPorDia(datos) {

	    const etiquetas =
	        datos.map(item => {

	            const partes =
	                item.fecha.split("-");

	            return `${partes[2]}/${partes[1]}`;

	        });


	    const valores =
	        datos.map(
	            item =>
	                Number(item.total || 0)
	        );


	    if (chartIncidenciasDia) {

	        chartIncidenciasDia.destroy();

	    }


	    chartIncidenciasDia =
	        new Chart(
	            chartIncidenciasDiaCanvas,
	            {
	                type: "bar",

	                data: {

	                    labels: etiquetas,

	                    datasets: [
	                        {
	                            label: "Incidencias",

	                            data: valores,

	                            backgroundColor:
	                                "rgba(37, 99, 235, 0.75)",

	                            borderColor:
	                                "#2563eb",

	                            borderWidth: 1,

	                            borderRadius: 6
	                        }
	                    ]
	                },

	                options: {

	                    responsive: true,

	                    maintainAspectRatio: false,

	                    plugins: {

	                        legend: {
	                            display: false
	                        }

	                    },

	                    scales: {

	                        y: {

	                            beginAtZero: true,

	                            ticks: {
	                                precision: 0
	                            },

	                            grid: {
	                                color:
	                                    "rgba(148, 163, 184, 0.15)"
	                            }
	                        },

	                        x: {

	                            grid: {
	                                display: false
	                            }
	                        }
	                    }
	                }
	            }
	        );
	}
	
	function actualizarGraficaPorEstado(datos) {

	    const etiquetas =
	        datos.map(
	            item =>
	                item.estado
	        );


	    const valores =
	        datos.map(
	            item =>
	                Number(
	                    item.total || 0
	                )
	        );


	    if (chartIncidenciasEstado) {

	        chartIncidenciasEstado.destroy();

	    }


	    chartIncidenciasEstado =
	        new Chart(
	            chartIncidenciasEstadoCanvas,
	            {
	                type: "doughnut",

	                data: {

	                    labels: etiquetas,

	                    datasets: [
	                        {
	                            data: valores,

	                            backgroundColor: [
	                                "#f59e0b",
	                                "#2563eb",
	                                "#16a34a",
	                                "#7c3aed",
	                                "#64748b",
	                                "#0ea5e9"
	                            ],

	                            borderWidth: 2,

	                            borderColor:
	                                "#ffffff"
	                        }
	                    ]
	                },

	                options: {

	                    responsive: true,

	                    maintainAspectRatio: false,

	                    cutout: "68%",

	                    plugins: {

	                        legend: {

	                            position: "bottom",

	                            labels: {

	                                usePointStyle: true,

	                                padding: 18
	                            }
	                        }
	                    }
	                }
	            }
	        );
	}
	
	async function cargarIncidenciasPorEmpleado() {

	    const desde =
	        fechaDesde.value;

	    const hasta =
	        fechaHasta.value;


	    const url =
	        `/api/reportes/incidencias-por-empleado`
	        + `?fechaDesde=${encodeURIComponent(desde)}`
	        + `&fechaHasta=${encodeURIComponent(hasta)}`;


	    const response =
	        await fetch(url);


	    if (!response.ok) {

	        throw new Error(
	            `Error HTTP ${response.status}`
	        );
	    }


	    const datos =
	        await response.json();


	    actualizarGraficaPorEmpleado(
	        datos
	    );
	}
	
	function actualizarGraficaPorEmpleado(datos) {

	    const etiquetas =
	        datos.map(
	            item =>
	                item.nombre
	                || "Sin nombre"
	        );


	    const valores =
	        datos.map(
	            item =>
	                Number(
	                    item.total || 0
	                )
	        );


	    if (chartIncidenciasEmpleado) {

	        chartIncidenciasEmpleado.destroy();

	    }


	    chartIncidenciasEmpleado =
	        new Chart(
	            chartIncidenciasEmpleadoCanvas,
	            {
	                type: "bar",

	                data: {

	                    labels: etiquetas,

	                    datasets: [
	                        {
	                            label: "Incidencias",

	                            data: valores,

	                            backgroundColor:
	                                "rgba(124, 58, 237, 0.72)",

	                            borderColor:
	                                "#7c3aed",

	                            borderWidth: 1,

	                            borderRadius: 6
	                        }
	                    ]
	                },

	                options: {

	                    indexAxis: "y",

	                    responsive: true,

	                    maintainAspectRatio: false,

	                    plugins: {

	                        legend: {
	                            display: false
	                        }

	                    },

	                    scales: {

	                        x: {

	                            beginAtZero: true,

	                            ticks: {
	                                precision: 0
	                            },

	                            grid: {
	                                color:
	                                    "rgba(148, 163, 184, 0.15)"
	                            }
	                        },


	                        y: {

	                            grid: {
	                                display: false
	                            }
	                        }
	                    }
	                }
	            }
	        );
	}
	
	const periodoAnalizado =
	    document.getElementById(
	        "periodoAnalizado"
	    );
	
		function actualizarPeriodoAnalizado(total) {

		    const desde =
		        fechaDesde.value;

		    const hasta =
		        fechaHasta.value;


		    if (!desde || !hasta) {
		        return;
		    }


		    const formateador =
		        new Intl.DateTimeFormat(
		            "es-MX",
		            {
		                day: "numeric",
		                month: "long",
		                year: "numeric",
		                timeZone: "UTC"
		            }
		        );


		    const fechaInicio =
		        formateador.format(
		            new Date(`${desde}T00:00:00Z`)
		        );


		    const fechaFin =
		        formateador.format(
		            new Date(`${hasta}T00:00:00Z`)
		        );


		    periodoAnalizado.textContent =
		        `Periodo analizado: ${fechaInicio}`
		        + ` al ${fechaFin}`
		        + ` · ${total} incidencia`
		        + `${total === 1 ? "" : "s"}`;
		}
		
		async function cargarTiemposPromedio() {

		    const desde =
		        fechaDesde.value;

		    const hasta =
		        fechaHasta.value;


		    const url =
		        `/api/reportes/tiempos-promedio`
		        + `?fechaDesde=${encodeURIComponent(desde)}`
		        + `&fechaHasta=${encodeURIComponent(hasta)}`;


		    const response =
		        await fetch(url);


		    if (!response.ok) {

		        throw new Error(
		            `Error HTTP ${response.status}`
		        );
		    }


		    const datos =
		        await response.json();


		    tiempoPromedioAtencion.textContent =
		        formatearDuracion(
		            datos.tiempoPromedioAtencionMinutos
		        );


		    tiempoPromedioResolucion.textContent =
		        formatearDuracion(
		            datos.tiempoPromedioResolucionMinutos
		        );
		}
		function formatearDuracion(minutos) {

		    const totalMinutos =
		        Math.round(
		            Number(minutos || 0)
		        );


		    if (totalMinutos <= 0) {

		        return "0 min";
		    }


		    if (totalMinutos < 60) {

		        return `${totalMinutos} min`;
		    }


		    const horas =
		        Math.floor(
		            totalMinutos / 60
		        );

		    const minutosRestantes =
		        totalMinutos % 60;


		    if (horas < 24) {

		        if (minutosRestantes === 0) {

		            return `${horas} h`;
		        }

		        return `${horas} h ${minutosRestantes} min`;
		    }


		    const dias =
		        Math.floor(
		            horas / 24
		        );

		    const horasRestantes =
		        horas % 24;


		    if (horasRestantes === 0) {

		        return `${dias} d`;
		    }


		    return `${dias} d ${horasRestantes} h`;
		}
		
		function exportarExcel() {

		    const desde =
		        fechaDesde.value;

		    const hasta =
		        fechaHasta.value;


		    if (!desde || !hasta) {

		        alert(
		            "Selecciona una fecha desde y una fecha hasta."
		        );

		        return;
		    }


		    if (desde > hasta) {

		        alert(
		            "La fecha desde no puede ser mayor que la fecha hasta."
		        );

		        return;
		    }


		    const url =
		        `/api/reportes/exportar/excel`
		        + `?fechaDesde=${encodeURIComponent(desde)}`
		        + `&fechaHasta=${encodeURIComponent(hasta)}`;


		    window.location.href =
		        url;
		}
		
		btnExportarExcel
		    ?.addEventListener(
		        "click",
		        exportarExcel
		    );
});