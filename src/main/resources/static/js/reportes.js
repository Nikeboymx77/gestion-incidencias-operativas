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
		
	const comparativoTotal =
		    document.getElementById(
		        "comparativoTotal"
		    );
	
	const comparativoResueltas =
	    document.getElementById(
	        "comparativoResueltas"
	    );

	const comparativoPendientes =
	    document.getElementById(
	        "comparativoPendientes"
	    );

	const comparativoEnProceso =
	    document.getElementById(
	        "comparativoEnProceso"
	    );

	const comparativoReabiertas =
	    document.getElementById(
	        "comparativoReabiertas"
	    );

	const comparativoCanceladas =
	    document.getElementById(
	        "comparativoCanceladas"
	    );

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
	
	let chartCargaOperativa;
	
	let chartEvolucionTiempos;
	
	const chartEvolucionTiemposCanvas =
	    document.getElementById("chartEvolucionTiempos");
	
	const chartCargaOperativaCanvas =
	    document.getElementById(
	        "chartCargaOperativa"
	    );
		
	const tablaIncidenciasAntiguas =
	    document.getElementById("tablaIncidenciasAntiguas");

	const totalIncidenciasActivas =
	    document.getElementById("totalIncidenciasActivas");
		
		
	const colores =
			    obtenerColoresTemaGraficas();
		
	

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
			
			await cargarComparativo();
			
			actualizarGraficaPorEstado(
						    datos
						);
			
			await cargarIncidenciasPorDia();
			
			await cargarIncidenciasPorEmpleado();
			
			await cargarTiemposPromedio();
			
			await cargarDesempenoPorEmpleado();
			
			await cargarCargaOperativa();
			
			await cargarEvolucionTiempos();
			
			await cargarIncidenciasAntiguas();

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
	        `/api/reportes/tendencia-comparativa`
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
		
		const colores =
		    obtenerColoresTemaGraficas();

	    const actual =
	        datos.periodoActual || [];

	    const anterior =
	        datos.periodoAnterior || [];


	    const etiquetas =
	        actual.map(
	            item => `Día ${item.dia}`
	        );


	    const valoresActuales =
	        actual.map(
	            item =>
	                Number(item.total || 0)
	        );


	    const valoresAnteriores =
	        anterior.map(
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
	                type: "line",

	                data: {

	                    labels: etiquetas,

	                    datasets: [

	                        {
	                            label:
	                                "Periodo actual",

	                            data:
	                                valoresActuales,

	                            borderColor:
	                                "#2563eb",

	                            backgroundColor:
	                                "rgba(37, 99, 235, 0.08)",

	                            borderWidth: 3,

	                            tension: 0.3,

	                            pointRadius: 3,

	                            pointHoverRadius: 6,

	                            fill: false
	                        },

	                        {
	                            label:
	                                "Periodo anterior",

	                            data:
	                                valoresAnteriores,

	                            borderColor:
	                                "#94a3b8",

	                            backgroundColor:
	                                "rgba(148, 163, 184, 0.08)",

	                            borderWidth: 2,

	                            borderDash:
	                                [6, 6],

	                            tension: 0.3,

	                            pointRadius: 2,

	                            pointHoverRadius: 5,

	                            fill: false
	                        }

	                    ]
	                },

	                options: {

	                    responsive: true,

	                    maintainAspectRatio: false,

	                    interaction: {
	                        mode: "index",
	                        intersect: false
	                    },

						plugins: {

						    legend: {
						        display: true,
						        position: "top",

						        labels: {
						            color: colores.texto
						        }
						    },

	                        tooltip: {

	                            callbacks: {

	                                title: function(context) {

	                                    if (
	                                        !context
	                                        || context.length === 0
	                                    ) {
	                                        return "";
	                                    }


	                                    const index =
	                                        context[0].dataIndex;

	                                    const fechaActual =
	                                        actual[index]?.fecha;

	                                    const fechaAnterior =
	                                        anterior[index]?.fecha;


	                                    return [
	                                        `Día ${index + 1}`,
	                                        `${formatearFechaGrafica(fechaActual)} vs. ${formatearFechaGrafica(fechaAnterior)}`
	                                    ];
	                                }
	                            }
	                        }
	                    },

	                    scales: {

							y: {

							    beginAtZero: true,

							    ticks: {
							        precision: 0,
							        color: colores.texto
							    },

							    grid: {
							        color: colores.grid
							    }
							},

							x: {

							    ticks: {
							        color: colores.texto
							    },

							    grid: {
							        display: false
							    }
							}
	                    }
	                }
	            }
	        );
	}
	
	function formatearFechaGrafica(fecha) {

	    if (!fecha) {
	        return "";
	    }


	    const partes =
	        fecha.split("-");


	    return `${partes[2]}/${partes[1]}/${partes[0]}`;
	}
	
	function actualizarGraficaPorEstado(datos) {
		
		const colores =
		    obtenerColoresTemaGraficas();

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
								    colores.bordeGrafica
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

							        padding: 18,

							        color: colores.texto
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
		
		const colores =
		    obtenerColoresTemaGraficas();

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
							        precision: 0,
							        color: colores.texto
							    },

							    grid: {
							        color: colores.grid
							    }
							},


							y: {

							    ticks: {
							        color: colores.texto
							    },

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
		
		async function cargarComparativo() {

		    const desde =
		        fechaDesde.value;

		    const hasta =
		        fechaHasta.value;


		    const url =
		        `/api/reportes/comparativo`
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


				const actual =
				    datos.periodoActual;

				const anterior =
				    datos.periodoAnterior;


				actualizarComparativo(
				    comparativoTotal,
				    actual.total,
				    anterior.total,
				    "neutral"
				);

				actualizarComparativo(
				    comparativoResueltas,
				    actual.resueltas,
				    anterior.resueltas,
				    "higher-is-better"
				);

				actualizarComparativo(
				    comparativoPendientes,
				    actual.pendientes,
				    anterior.pendientes,
				    "lower-is-better"
				);

				actualizarComparativo(
				    comparativoEnProceso,
				    actual.enProceso,
				    anterior.enProceso,
				    "neutral"
				);

				actualizarComparativo(
				    comparativoReabiertas,
				    actual.reabiertas,
				    anterior.reabiertas,
				    "lower-is-better"
				);

				actualizarComparativo(
				    comparativoCanceladas,
				    actual.canceladas,
				    anterior.canceladas,
				    "lower-is-better"
				);
		}
		
		function actualizarComparativo(
		    elemento,
		    actual,
		    anterior,
		    tipo = "neutral"
		) {

		    actual = Number(actual || 0);
		    anterior = Number(anterior || 0);


		    elemento.classList.remove(
		        "positive",
		        "negative",
		        "neutral"
		    );


		    if (anterior === 0) {

		        if (actual === 0) {

		            elemento.classList.add("neutral");

		            elemento.innerHTML = `
		                <i class="bi bi-dash"></i>
		                <span>
		                    Sin variación vs. periodo anterior
		                </span>
		            `;

		        } else {

		            const clase =
		                obtenerClaseComparativo(
		                    1,
		                    tipo
		                );

		            elemento.classList.add(clase);

		            elemento.innerHTML = `
		                <i class="bi bi-arrow-up"></i>
		                <span>
		                    Nuevo vs. periodo anterior
		                </span>
		            `;
		        }

		        return;
		    }


		    const variacion =
		        ((actual - anterior) / anterior)
		        * 100;


		    if (variacion === 0) {

		        elemento.classList.add("neutral");

		        elemento.innerHTML = `
		            <i class="bi bi-dash"></i>
		            <span>
		                Sin variación vs. periodo anterior
		            </span>
		        `;

		        return;
		    }


		    const direccion =
		        variacion > 0 ? 1 : -1;


		    const clase =
		        obtenerClaseComparativo(
		            direccion,
		            tipo
		        );


		    elemento.classList.add(clase);


		    const icono =
		        variacion > 0
		            ? "bi-arrow-up"
		            : "bi-arrow-down";


		    elemento.innerHTML = `
		        <i class="bi ${icono}"></i>
		        <span>
		            ${Math.abs(variacion).toFixed(1)}%
		            vs. periodo anterior
		        </span>
		    `;
		}
		
		function obtenerClaseComparativo(
		    direccion,
		    tipo
		) {

		    if (tipo === "neutral") {
		        return "neutral";
		    }


		    if (tipo === "higher-is-better") {

		        return direccion > 0
		            ? "positive"
		            : "negative";
		    }


		    if (tipo === "lower-is-better") {

		        return direccion < 0
		            ? "positive"
		            : "negative";
		    }


		    return "neutral";
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
			
			async function cargarDesempenoPorEmpleado() {

			    const desde =
			        fechaDesde.value;

			    const hasta =
			        fechaHasta.value;

			    const url =
			        `/api/reportes/desempeno-por-empleado`
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

			    actualizarTablaDesempeno(
			        datos
			    );
			}
			function actualizarTablaDesempeno(datos) {

			    const tbody =
			        document.getElementById(
			            "tablaDesempenoEmpleados"
			        );

			    if (!tbody) {
			        return;
			    }


			    if (!datos || datos.length === 0) {

			        tbody.innerHTML = `
			            <tr>
			                <td colspan="7"
			                    class="performance-empty">
			                    No hay información de empleados
			                    para el periodo seleccionado.
			                </td>
			            </tr>
			        `;

			        return;
			    }


			    tbody.innerHTML =
			        datos.map(
			            (empleado, index) => {

			                const porcentaje =
			                    Number(
			                        empleado.porcentajeResolucion || 0
			                    );

			                return `
			                    <tr>

									<td class="performance-position">
									    <span class="performance-rank rank-${index + 1}">
									        ${index + 1}
									    </span>
									</td>

			                        <td>
			                            <strong>
			                                ${empleado.nombre}
			                            </strong>
			                        </td>

			                        <td>
			                            ${empleado.totalIncidencias}
			                        </td>

			                        <td>
			                            ${empleado.resueltas}
			                        </td>

									<td>

									    <div class="performance-resolution">

									        <div class="performance-resolution-header">

									            <strong>
									                ${porcentaje.toFixed(1)}%
									            </strong>

									        </div>

									        <div class="performance-resolution-bar">

									            <div class="performance-resolution-value"
									                 style="width: ${Math.min(porcentaje, 100)}%">
									            </div>

									        </div>

									    </div>

									</td>

			                        <td>
			                            ${formatearTiempo(
			                                empleado.tiempoPromedioAtencionMinutos
			                            )}
			                        </td>

			                        <td>
			                            ${formatearTiempo(
			                                empleado.tiempoPromedioResolucionMinutos
			                            )}
			                        </td>

			                    </tr>
			                `;
			            }
			        ).join("");
			}
			
			function formatearTiempo(minutos) {

			    if (
			        minutos === null
			        || minutos === undefined
			    ) {
			        return "—";
			    }


			    const totalMinutos =
			        Math.round(
			            Number(minutos)
			        );


			    if (totalMinutos < 60) {
			        return `${totalMinutos} min`;
			    }


			    const horas =
			        Math.floor(
			            totalMinutos / 60
			        );

			    const minutosRestantes =
			        totalMinutos % 60;


			    return `${horas} h ${minutosRestantes} min`;
			}
			
			async function cargarCargaOperativa() {

			    const response =
			        await fetch(
			            "/api/reportes/carga-operativa"
			        );

			    if (!response.ok) {
			        throw new Error(
			            `Error HTTP ${response.status}`
			        );
			    }

			    const datos =
			        await response.json();

			    actualizarGraficaCargaOperativa(
			        datos
			    );
			}
			
			function actualizarGraficaCargaOperativa(datos) {
				
				const colores =
				    obtenerColoresTemaGraficas();

			    if (!chartCargaOperativaCanvas) {
			        return;
			    }


			    const etiquetas =
			        datos.map(
			            item => item.nombre
			        );

			    const pendientes =
			        datos.map(
			            item =>
			                Number(item.pendientes || 0)
			        );

			    const enProceso =
			        datos.map(
			            item =>
			                Number(item.enProceso || 0)
			        );

			    const reabiertas =
			        datos.map(
			            item =>
			                Number(item.reabiertas || 0)
			        );


			    if (chartCargaOperativa) {
			        chartCargaOperativa.destroy();
			    }


			    chartCargaOperativa =
			        new Chart(
			            chartCargaOperativaCanvas,
			            {
			                type: "bar",

			                data: {

			                    labels: etiquetas,

			                    datasets: [
			                        {
			                            label: "Pendientes",
			                            data: pendientes,
			                            backgroundColor: "#f59e0b",
			                            borderRadius: 4
			                        },
			                        {
			                            label: "En proceso",
			                            data: enProceso,
			                            backgroundColor: "#8b5cf6",
			                            borderRadius: 4
			                        },
			                        {
			                            label: "Reabiertas",
			                            data: reabiertas,
			                            backgroundColor: "#ef4444",
			                            borderRadius: 4
			                        }
			                    ]
			                },

			                options: {

			                    indexAxis: "y",

			                    responsive: true,

			                    maintainAspectRatio: false,

			                    interaction: {
			                        mode: "index",
			                        intersect: false
			                    },

			                    plugins: {

									legend: {
									    display: true,
									    position: "top",

									    labels: {
									        color: colores.texto
									    }
									},

			                        tooltip: {

			                            callbacks: {

			                                footer: function(context) {

			                                    const total =
			                                        context.reduce(
			                                            (suma, item) =>
			                                                suma
			                                                + Number(
			                                                    item.raw || 0
			                                                ),
			                                            0
			                                        );

			                                    return `Total activas: ${total}`;
			                                }
			                            }
			                        }
			                    },

			                    scales: {

									x: {
									    stacked: true,

									    beginAtZero: true,

									    ticks: {
									        precision: 0,
									        color: colores.texto
									    },

									    grid: {
									        color: colores.grid
									    }
									},

									y: {
									    stacked: true,

									    ticks: {
									        color: colores.texto
									    },

									    grid: {
									        display: false
									    }
									}
			                    }
			                }
			            }
			        );
			}
			
			async function cargarEvolucionTiempos() {

			    const desde =
			        fechaDesde.value;

			    const hasta =
			        fechaHasta.value;

			    const response =
			        await fetch(
			            `/api/reportes/evolucion-tiempos`
			            + `?fechaDesde=${encodeURIComponent(desde)}`
			            + `&fechaHasta=${encodeURIComponent(hasta)}`
			        );

			    if (!response.ok) {

			        throw new Error(
			            `Error HTTP ${response.status}`
			        );
			    }

			    const datos =
			        await response.json();

			    actualizarGraficaEvolucionTiempos(
			        datos
			    );
			}
			
			function actualizarGraficaEvolucionTiempos(datos) {
				
				const colores =
				    obtenerColoresTemaGraficas();

			    if (!chartEvolucionTiemposCanvas) {
			        return;
			    }

			    if (chartEvolucionTiempos) {
			        chartEvolucionTiempos.destroy();
			    }


			    const labels = datos.map(item =>
			        formatearFechaGrafica(item.fecha)
			    );


			    const tiemposAtencion = datos.map(item => {

			        if (item.tiempoPromedioAtencionMinutos == null) {
			            return null;
			        }

			        return item.tiempoPromedioAtencionMinutos / 60;
			    });


			    const tiemposResolucion = datos.map(item => {

			        if (item.tiempoPromedioResolucionMinutos == null) {
			            return null;
			        }

			        return item.tiempoPromedioResolucionMinutos / 60;
			    });


			    chartEvolucionTiempos = new Chart(
			        chartEvolucionTiemposCanvas,
			        {
			            type: "line",

			            data: {
			                labels: labels,

			                datasets: [
			                    {
			                        label: "Tiempo promedio de atención",
			                        data: tiemposAtencion,
			                        borderColor: "#2563eb",
			                        backgroundColor: "#2563eb",
			                        borderWidth: 2,
			                        tension: 0.3,
			                        pointRadius: 3,
			                        pointHoverRadius: 5,
			                        spanGaps: true
			                    },
			                    {
			                        label: "Tiempo promedio de resolución",
			                        data: tiemposResolucion,
			                        borderColor: "#16a34a",
			                        backgroundColor: "#16a34a",
			                        borderWidth: 2,
			                        tension: 0.3,
			                        pointRadius: 3,
			                        pointHoverRadius: 5,
			                        spanGaps: true
			                    }
			                ]
			            },

			            options: {
			                responsive: true,
			                maintainAspectRatio: false,

			                interaction: {
			                    mode: "index",
			                    intersect: false
			                },

			                plugins: {
								legend: {
								    position: "top",

								    labels: {
								        color: colores.texto
								    }
								},

			                    tooltip: {
			                        callbacks: {
			                            label: function(context) {

			                                const horas = context.raw;

			                                if (horas == null) {
			                                    return `${context.dataset.label}: Sin datos`;
			                                }

			                                const minutosTotales =
			                                    Math.round(horas * 60);

			                                return `${context.dataset.label}: ${formatearTiempo(minutosTotales)}`;
			                            }
			                        }
			                    }
			                },

			                scales: {
								y: {
								    beginAtZero: true,

								    title: {
								        display: true,
								        text: "Horas",
								        color: colores.texto
								    },

								    ticks: {
								        color: colores.texto,

								        callback: function(value) {
								            return `${value} h`;
								        }
								    },

								    grid: {
								        color: colores.grid
								    }
								},

								x: {
								    ticks: {
								        maxRotation: 45,
								        minRotation: 0,
								        color: colores.texto
								    },

								    grid: {
								        color: colores.grid
								    }
								}
			                }
			            }
			        }
			    );
			}
			
			async function cargarIncidenciasAntiguas() {

			    const response =
			        await fetch("/api/reportes/incidencias-antiguas");

			    if (!response.ok) {
			        throw new Error(
			            `Error HTTP ${response.status}`
			        );
			    }

			    const datos =
			        await response.json();

			    actualizarTablaIncidenciasAntiguas(datos);
			}
			
			function actualizarTablaIncidenciasAntiguas(datos) {

			    if (!tablaIncidenciasAntiguas) {
			        return;
			    }

			    const total =
			        Array.isArray(datos)
			            ? datos.length
			            : 0;


			    if (totalIncidenciasActivas) {

			        totalIncidenciasActivas.textContent =
			            `${total} ${
			                total === 1
			                    ? "incidencia activa"
			                    : "incidencias activas"
			            }`;
			    }


			    if (total === 0) {

			        tablaIncidenciasAntiguas.innerHTML = `
			            <tr>
			                <td colspan="5"
			                    class="performance-empty">
			                    No existen incidencias activas.
			                </td>
			            </tr>
			        `;

			        return;
			    }


			    const incidencias =
			        datos.slice(0, 10);


			    tablaIncidenciasAntiguas.innerHTML =
			        incidencias
			            .map(incidencia => {

			                const empleado =
			                    incidencia.empleado
			                        ?? "Sin asignar";

			                return `
			                    <tr>

			                        <td>
			                            <span class="old-incident-folio">
			                                ${incidencia.folio ?? "—"}
			                            </span>
			                        </td>

			                        <td>
			                            <div
			                                class="old-incident-subject"
			                                title="${incidencia.asunto ?? ""}"
			                            >
			                                ${incidencia.asunto ?? "—"}
			                            </div>
			                        </td>

			                        <td>
			                            ${formatearEstadoIncidencia(
			                                incidencia.estado
			                            )}
			                        </td>

			                        <td>
			                            <span class="old-incident-employee">
			                                ${empleado}
			                            </span>
			                        </td>

			                        <td>
			                            <span class="old-incident-age">

			                                <i class="bi bi-clock-history"></i>

			                                ${formatearAntiguedad(
			                                    incidencia.antiguedadMinutos
			                                )}

			                            </span>
			                        </td>

			                    </tr>
			                `;
			            })
			            .join("");
			}
			
			function formatearAntiguedad(minutos) {

			    if (minutos == null) {
			        return "—";
			    }

			    const totalMinutos =
			        Math.max(
			            0,
			            Math.floor(minutos)
			        );

			    const dias =
			        Math.floor(
			            totalMinutos / 1440
			        );

			    const horas =
			        Math.floor(
			            (totalMinutos % 1440) / 60
			        );

			    const minutosRestantes =
			        totalMinutos % 60;


			    if (dias > 0) {
			        return `${dias} d ${horas} h`;
			    }

			    if (horas > 0) {
			        return `${horas} h ${minutosRestantes} min`;
			    }

			    return `${minutosRestantes} min`;
			}
			
			function formatearEstadoIncidencia(estado) {

			    if (!estado) {
			        return "—";
			    }

			    switch (estado) {

			        case "PENDIENTE":
			            return `
			                <span class="old-incident-status status-pendiente">
			                    Pendiente
			                </span>
			            `;

			        case "EN_PROCESO":
			            return `
			                <span class="old-incident-status status-en-proceso">
			                    En proceso
			                </span>
			            `;

			        case "REABIERTA":
			            return `
			                <span class="old-incident-status status-reabierta">
			                    Reabierta
			                </span>
			            `;

			        default:
			            return estado;
			    }
			}
			
			function obtenerColoresTemaGraficas() {

			    const tema =
			        document.documentElement.dataset.theme;

			    const oscuro =
			        tema === "dark";

			    return {
			        texto: oscuro
			            ? "#cbd5e1"
			            : "#334155",

			        textoFuerte: oscuro
			            ? "#f1f5f9"
			            : "#1e293b",

			        grid: oscuro
			            ? "rgba(148, 163, 184, 0.16)"
			            : "rgba(148, 163, 184, 0.15)",

			        bordeGrafica: oscuro
			            ? "#1e293b"
			            : "#ffffff"
			    };
			}
			
			window.addEventListener(
			    "sgio-theme-changed",
			    () => {

			        const colores =
			            obtenerColoresTemaGraficas();

			        if (chartIncidenciasDia) {

			            chartIncidenciasDia.options.plugins
			                .legend.labels.color =
			                    colores.texto;

			            chartIncidenciasDia.options.scales
			                .x.ticks.color =
			                    colores.texto;

			            chartIncidenciasDia.options.scales
			                .y.ticks.color =
			                    colores.texto;

			            chartIncidenciasDia.options.scales
			                .y.grid.color =
			                    colores.grid;

			            chartIncidenciasDia.update();
			        }


			        if (chartIncidenciasEstado) {

			            chartIncidenciasEstado.options.plugins
			                .legend.labels.color =
			                    colores.texto;

			            chartIncidenciasEstado.data.datasets
			                .forEach(dataset => {

			                    dataset.borderColor =
			                        colores.bordeGrafica;
			                });

			            chartIncidenciasEstado.update();
			        }


			        if (chartIncidenciasEmpleado) {

			            chartIncidenciasEmpleado.options.scales
			                .x.ticks.color =
			                    colores.texto;

			            chartIncidenciasEmpleado.options.scales
			                .x.grid.color =
			                    colores.grid;

			            chartIncidenciasEmpleado.options.scales
			                .y.ticks.color =
			                    colores.texto;

			            chartIncidenciasEmpleado.update();
			        }


			        if (chartCargaOperativa) {

			            chartCargaOperativa.options.plugins
			                .legend.labels.color =
			                    colores.texto;

			            chartCargaOperativa.options.scales
			                .x.ticks.color =
			                    colores.texto;

			            chartCargaOperativa.options.scales
			                .x.grid.color =
			                    colores.grid;

			            chartCargaOperativa.options.scales
			                .y.ticks.color =
			                    colores.texto;

			            chartCargaOperativa.update();
			        }
					
					if (chartEvolucionTiempos) {

					    chartEvolucionTiempos.options.plugins
					        .legend.labels.color =
					            colores.texto;

					    chartEvolucionTiempos.options.scales
					        .x.ticks.color =
					            colores.texto;

					    chartEvolucionTiempos.options.scales
					        .x.grid.color =
					            colores.grid;

					    chartEvolucionTiempos.options.scales
					        .y.ticks.color =
					            colores.texto;

					    chartEvolucionTiempos.options.scales
					        .y.title.color =
					            colores.texto;

					    chartEvolucionTiempos.options.scales
					        .y.grid.color =
					            colores.grid;

					    chartEvolucionTiempos.update();
					}
			    }
			);
});