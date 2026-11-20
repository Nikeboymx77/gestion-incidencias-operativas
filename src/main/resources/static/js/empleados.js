document.addEventListener(
    "DOMContentLoaded",
    () => {

        const tbody =
            document.getElementById(
                "empleadosTableBody"
            );

        const total =
            document.getElementById(
                "empleadosTotal"
            );

        const activos =
            document.getElementById(
                "empleadosActivos"
            );

        const inactivos =
            document.getElementById(
                "empleadosInactivos"
            );

        const incidenciasActivas =
            document.getElementById(
                "incidenciasActivasEquipo"
            );

        const btnActualizar =
            document.getElementById(
                "btnActualizarEmpleados"
            );
			

		const barraAccionesMasivas =
		    document.getElementById("barraAccionesMasivas");

		const totalSeleccionadosMasivo =
		    document.getElementById("totalSeleccionadosMasivo");

		const modalEstadoMasivo =
		    document.getElementById("modalEstadoMasivo");

		const tituloEstadoMasivo =
		    document.getElementById("tituloEstadoMasivo");

		const mensajeEstadoMasivo =
		    document.getElementById("mensajeEstadoMasivo");

		let nuevoEstadoMasivo = null;
		

		/* ==========================================
		   SGIO-002 - REFERENCIAS MODAL DÍAS MASIVOS
		   ========================================== */

		const modalDiasMasivos =
		    document.getElementById("modalDiasMasivos");

		const operacionDiasMasivos =
		    document.getElementById("operacionDiasMasivos");

		const mensajeDiasMasivos =
		    document.getElementById("mensajeDiasMasivos");

		const descripcionOperacionDias =
		    document.getElementById("descripcionOperacionDias");

		const errorDiasMasivos =
		    document.getElementById("errorDiasMasivos");
			
			

		/* ==========================================
		   SGIO-002 - FILTROS Y ORDENAMIENTO
		   ========================================== */

		const filtroEstadoEmpleados =
		    document.getElementById("filtroEstadoEmpleados");

		const ordenEmpleados =
		    document.getElementById("ordenEmpleados");

		const contadorResultadosEmpleados =
		    document.getElementById("contadorResultadosEmpleados");

		// Almacena los datos originales del backend
		let empleadosOriginales = [];


        async function cargarEmpleados() {

            try {

                const response =
                    await fetch(
                        "/api/empleados/resumen"
                    );


                if (!response.ok) {

                    throw new Error(
                        "No fue posible consultar los empleados."
                    );
                }



				const empleados = await response.json();

				// Conservar todos los empleados consultados
				empleadosOriginales = empleados;

				// Las métricas siempre consideran todos los empleados
				actualizarMetricas(empleadosOriginales);

				// Aplicar filtros y ordenamiento actuales
				aplicarFiltrosEmpleados();




				} catch (error) {

				    console.error(
				        "Error al cargar empleados:",
				        error
				    );

				    tbody.innerHTML = `
				        <tr>
				            <td colspan="10"
				                class="employees-loading">

				                No fue posible cargar
				                los empleados.

				            </td>
				        </tr>
				    `;

				    // Propagar el error para que las operaciones
				    // que esperan la recarga puedan detectarlo.
				    throw error;
				}

        }
		

		/* ==========================================
		   SGIO-002 - RECARGA SEGURA DE EMPLEADOS
		   ========================================== */

		async function cargarEmpleadosSeguro() {

		    try {

		        await cargarEmpleados();

		    } catch (error) {

		        console.error(
		            "No fue posible actualizar el listado:",
		            error
		        );

		        mostrarToast(
		            "error",
		            "Error al consultar empleados",
		            "No fue posible cargar la información. " +
		            "Intenta nuevamente."
		        );
		    }
		}



        function actualizarMetricas(
            empleados
        ) {

            const cantidadActivos =
                empleados.filter(
                    empleado =>
                        empleado.activo === true
                ).length;


            const cantidadInactivos =
                empleados.length
                - cantidadActivos;


            const cargaTotal =
                empleados.reduce(
                    (acumulado, empleado) =>
                        acumulado
                        + Number(
                            empleado.totalActivas
                            || 0
                        ),
                    0
                );


            total.textContent =
                empleados.length;


            activos.textContent =
                cantidadActivos;


            inactivos.textContent =
                cantidadInactivos;


            incidenciasActivas.textContent =
                cargaTotal;
        }


        function renderizarEmpleados(
            empleados
        ) {

            if (!empleados.length) {

                tbody.innerHTML = `
                    <tr>

                        <td colspan="10"
                            class="employees-loading">

                            No hay empleados registrados.

                        </td>

                    </tr>
                `;
				
				actualizarSeleccionEmpleados();
                return;
            }


            tbody.innerHTML =
                empleados
                    .map(
                        empleado =>
                            crearFilaEmpleado(
                                empleado
                            )
                    )
                    .join("");
					
			actualizarSeleccionEmpleados();
        }
		

		/* ==========================================
		   SGIO-002 - APLICAR FILTROS Y ORDENAMIENTO
		   ========================================== */

		function aplicarFiltrosEmpleados() {

		    const estado = filtroEstadoEmpleados?.value || "TODOS";
		    const orden = ordenEmpleados?.value || "NOMBRE";

		    // Crear una copia para no modificar los datos originales
		    let empleados = [...empleadosOriginales];

		    // FILTRAR POR ESTADO
		    if (estado === "ACTIVOS") {
		        empleados = empleados.filter(
		            empleado => empleado.activo === true
		        );
		    } else if (estado === "INACTIVOS") {
		        empleados = empleados.filter(
		            empleado => empleado.activo === false
		        );
		    }

		    // Contar días laborales sin duplicados
		    const contarDias = empleado => {
		        const dias = Array.isArray(empleado.diasLaborales)
		            ? empleado.diasLaborales
		            : [];

		        return new Set(dias).size;
		    };

		    const compararNombre = (a, b) =>
		        (a.nombre || "").localeCompare(
		            b.nombre || "",
		            "es",
		            { sensitivity: "base" }
		        );

		    // ORDENAR
		    switch (orden) {

		        case "DIAS_ASC":
		            empleados.sort((a, b) =>
		                contarDias(a) - contarDias(b) ||
		                compararNombre(a, b)
		            );
		            break;

		        case "DIAS_DESC":
		            empleados.sort((a, b) =>
		                contarDias(b) - contarDias(a) ||
		                compararNombre(a, b)
		            );
		            break;

		        case "ACTIVOS_PRIMERO":
		            empleados.sort((a, b) =>
		                Number(b.activo === true) -
		                Number(a.activo === true) ||
		                compararNombre(a, b)
		            );
		            break;

		        case "INACTIVOS_PRIMERO":
		            empleados.sort((a, b) =>
		                Number(a.activo === true) -
		                Number(b.activo === true) ||
		                compararNombre(a, b)
		            );
		            break;

		        default:
		            empleados.sort(compararNombre);
		            break;
		    }

		    // Actualizar contador de resultados
		    if (contadorResultadosEmpleados) {
		        const cantidad = empleados.length;

		        contadorResultadosEmpleados.textContent =
		            `${cantidad} empleado${cantidad === 1 ? "" : "s"}`;
		    }


			// Renderizar resultados
			if (empleados.length === 0) {

			    tbody.innerHTML = `
			        <tr>
			            <td colspan="10"
			                class="employees-empty">

			                <i class="bi bi-search"></i>

			                <p>
			                    No se encontraron empleados
			                    con los filtros seleccionados.
			                </p>

			            </td>
			        </tr>
			    `;

			    // No modificar empleadosSeleccionados:
			    // pueden existir empleados seleccionados
			    // que estén ocultos por los filtros.
			    actualizarSeleccionEmpleados();

			    return;
			}

			renderizarEmpleados(empleados);

			// Sincronizar el estado visual de la selección
			actualizarSeleccionEmpleados();

		}
		
		

		/* ==========================================
		   SGIO-002 - EVENTOS DE FILTROS
		   ========================================== */

		filtroEstadoEmpleados?.addEventListener(
		    "change",
		    aplicarFiltrosEmpleados
		);

		ordenEmpleados?.addEventListener(
		    "change",
		    aplicarFiltrosEmpleados
		);


		

		function formatearDiasLaborales(diasLaborales) {

		    const dias = {
		        MONDAY: "L",
		        TUESDAY: "M",
		        WEDNESDAY: "X",
		        THURSDAY: "J",
		        FRIDAY: "V",
		        SATURDAY: "S",
		        SUNDAY: "D"
		    };

		    const orden = Object.keys(dias);

		    if (!Array.isArray(diasLaborales) ||
		        diasLaborales.length === 0) {
		        return '<span>Sin días asignados</span>';
		    }

		    return orden
		        .filter(dia => diasLaborales.includes(dia))
		        .map(dia => `
		            <span class="employee-workday-badge"
		                  title="${dia}">
		                ${dias[dia]}
		            </span>
		        `)
		        .join("");
		}



        function crearFilaEmpleado(
            empleado
        ) {

            const estadoClass =
                empleado.activo
                    ? "active"
                    : "inactive";


            const estadoTexto =
                empleado.activo
                    ? "Activo"
                    : "Inactivo";


            return `
                <tr>

					<td>

					<input type="checkbox"
					       class="empleado-checkbox"
					       value="${empleado.id}"
					       ${empleadosSeleccionados.has(Number(empleado.id)) ? "checked" : ""}
					       aria-label="Seleccionar empleado">

					</td>


                    <td>

                        <div class="employee-name">

                            <strong>
                                ${escapeHtml(
                                    empleado.nombre
                                    || "Sin nombre"
                                )}
                            </strong>

                            <span>
                                ${escapeHtml(
                                    formatearTelegram(
                                        empleado.usernameTelegram
                                    )
                                )}
                            </span>

                        </div>

                    </td>


                    <td>

                        <span class="
                            employee-status
                            ${estadoClass}
                        ">

                            <span
                                class="employee-status-dot">
                            </span>

                            ${estadoTexto}

                        </span>

                    </td>
					

					<td>
					    <div class="employee-workdays-list">
					        ${formatearDiasLaborales(
					            empleado.diasLaborales
					        )}
					    </div>
					</td>


                    <td>

                        <span class="employee-workload">

                            ${Number(
                                empleado.totalActivas
                                || 0
                            )}

                        </span>

                    </td>


                    <td>
                        ${Number(
                            empleado.pendientes
                            || 0
                        )}
                    </td>


                    <td>
                        ${Number(
                            empleado.enProceso
                            || 0
                        )}
                    </td>


                    <td>
                        ${Number(
                            empleado.reabiertas
                            || 0
                        )}
                    </td>


                    <td>

                        ${formatearFecha(
                            empleado.ultimaAsignacion
                        )}

                    </td>


                    <td>

                        <a href="/empleados/${empleado.id}"
                           class="employee-view-button">

                            <i class="bi bi-eye"></i>

                            Ver

                        </a>

                    </td>

                </tr>
            `;
        }


        function formatearTelegram(
            username
        ) {

            if (!username) {
                return "Sin Telegram";
            }


            return username.startsWith("@")
                ? username
                : `@${username}`;
        }


        function formatearFecha(
            fecha
        ) {

            if (!fecha) {
                return "Sin asignaciones";
            }


            const valor =
                new Date(fecha);


            if (
                Number.isNaN(
                    valor.getTime()
                )
            ) {
                return "-";
            }


            return new Intl.DateTimeFormat(
                "es-MX",
                {
                    day: "2-digit",
                    month: "2-digit",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit"
                }
            ).format(valor);
        }


        function escapeHtml(
            valor
        ) {

            return String(valor ?? "")
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll('"', "&quot;")
                .replaceAll(
                    "'",
                    "&#039;"
                );
        }



		btnActualizar?.addEventListener(
		    "click",
		    cargarEmpleadosSeguro
		);

			

		const seleccionarTodos =
		    document.getElementById(
		        "seleccionarTodosEmpleados"
		    );

		const empleadosSeleccionados = new Set();

		const contadorSeleccionados =
		    document.createElement("span");

		contadorSeleccionados.id =
		    "contadorEmpleadosSeleccionados";

		contadorSeleccionados.textContent =
		    "0 empleados seleccionados";

		contadorSeleccionados.style.marginLeft = "12px";
		contadorSeleccionados.style.fontWeight = "600";

		document.querySelector(
		    ".employees-card-header"
		)?.appendChild(contadorSeleccionados);
		


		function actualizarSeleccionEmpleados() {

		    const checkboxes = Array.from(
		        tbody.querySelectorAll(".empleado-checkbox")
		    );

		    const cantidad = empleadosSeleccionados.size;

		    // Contar únicamente los empleados visibles seleccionados
		    const seleccionadosVisibles = checkboxes.filter(
		        checkbox =>
		            empleadosSeleccionados.has(Number(checkbox.value))
		    ).length;

		    // Actualizar contador principal
		    contadorSeleccionados.textContent =
		        `${cantidad} empleado${cantidad === 1 ? "" : "s"} ` +
		        `seleccionado${cantidad === 1 ? "" : "s"}`;

		    // Actualizar barra de acciones
		    if (barraAccionesMasivas) {
		        barraAccionesMasivas.hidden = cantidad === 0;
		    }

		    if (totalSeleccionadosMasivo) {
		        totalSeleccionadosMasivo.textContent =
		            `${cantidad} empleado${cantidad === 1 ? "" : "s"} ` +
		            `seleccionado${cantidad === 1 ? "" : "s"}`;
		    }

		    // Estado del checkbox Seleccionar todos
		    if (seleccionarTodos) {

		        seleccionarTodos.checked =
		            checkboxes.length > 0 &&
		            seleccionadosVisibles === checkboxes.length;

		        seleccionarTodos.indeterminate =
		            seleccionadosVisibles > 0 &&
		            seleccionadosVisibles < checkboxes.length;
		    }
		}



		// Seleccionar o deseleccionar todos

		seleccionarTodos?.addEventListener("change", () => {

		    const seleccionar = seleccionarTodos.checked;

		    tbody.querySelectorAll(".empleado-checkbox")
		        .forEach(checkbox => {

		            const id = Number(checkbox.value);

		            checkbox.checked = seleccionar;

		            if (seleccionar) {
		                empleadosSeleccionados.add(id);
		            } else {
		                empleadosSeleccionados.delete(id);
		            }
		        });

		    actualizarSeleccionEmpleados();
		});



		// Selección individual

		tbody.addEventListener("change", event => {

		    if (!event.target.matches(".empleado-checkbox")) {
		        return;
		    }

		    const id = Number(event.target.value);

		    if (event.target.checked) {
		        empleadosSeleccionados.add(id);
		    } else {
		        empleadosSeleccionados.delete(id);
		    }

		    actualizarSeleccionEmpleados();
		});

		

		document.getElementById("btnLimpiarSeleccion")
		    ?.addEventListener("click", () => {
				
				empleadosSeleccionados.clear();

		        tbody.querySelectorAll(".empleado-checkbox")
		            .forEach(checkbox => {
		                checkbox.checked = false;
		            });

		        actualizarSeleccionEmpleados();
		    });
			
			

			document.getElementById("btnActivarSeleccionados")
			    ?.addEventListener("click", () => {
			        abrirModalEstadoMasivo(true);
			    });

			document.getElementById("btnDesactivarSeleccionados")
			    ?.addEventListener("click", () => {
			        abrirModalEstadoMasivo(false);
			    });

			document.getElementById("btnCerrarEstadoMasivo")
			    ?.addEventListener("click", cerrarModalEstadoMasivo);

			document.getElementById("btnCancelarEstadoMasivo")
			    ?.addEventListener("click", cerrarModalEstadoMasivo);

			modalEstadoMasivo
			    ?.querySelector(".sgio-modal-backdrop")
			    ?.addEventListener("click", cerrarModalEstadoMasivo);




			function abrirModalEstadoMasivo(activo) {

			    if (empleadosSeleccionados.size === 0) {
			        return;
			    }
				
			    nuevoEstadoMasivo = activo;
				

				const totalSeleccionados = empleadosSeleccionados.size;

				const seleccionadosVisibles = Array.from(
				    tbody.querySelectorAll(".empleado-checkbox")
				).filter(checkbox =>
				    empleadosSeleccionados.has(Number(checkbox.value))
				).length;

				const seleccionadosOcultos =
				    totalSeleccionados - seleccionadosVisibles;


				const accion = activo ? "activar" : "desactivar";

				let mensaje =
				    `¿Confirmas que deseas ${accion} ` +
				    `${totalSeleccionados} empleado(s)?`;

				if (seleccionadosOcultos > 0) {
				    mensaje +=
				        ` Atención: ${seleccionadosOcultos} empleado(s) ` +
				        `seleccionado(s) están ocultos por los filtros actuales.`;
				}
				
				mensajeEstadoMasivo.textContent = mensaje;

			    document.getElementById(
			        "errorEstadoMasivo"
			    ).textContent = "";

			    modalEstadoMasivo.classList.add("active");
			}


			function cerrarModalEstadoMasivo() {

			    modalEstadoMasivo.classList.remove("active");

			    nuevoEstadoMasivo = null;
			}
 
		
		const btnNuevoEmpleado =
		    document.getElementById(
		        "btnNuevoEmpleado"
		    );

		const modalNuevoEmpleado =
		    document.getElementById(
		        "modalNuevoEmpleado"
		    );

		const btnCerrarNuevoEmpleado =
		    document.getElementById(
		        "btnCerrarNuevoEmpleado"
		    );

		const btnCancelarNuevoEmpleado =
		    document.getElementById(
		        "btnCancelarNuevoEmpleado"
		    );

		const btnGuardarNuevoEmpleado =
		    document.getElementById(
		        "btnGuardarNuevoEmpleado"
		    );

		const nuevoNombre =
		    document.getElementById(
		        "nuevoNombre"
		    );

		const nuevoTelegram =
		    document.getElementById(
		        "nuevoTelegram"
		    );

		const nuevoEmail =
		    document.getElementById(
		        "nuevoEmail"
		    );

		const nuevoEmpleadoError =
		    document.getElementById(
		        "nuevoEmpleadoError"
		    );
			function abrirModalNuevoEmpleado() {

			    limpiarErrorNuevoEmpleado();

			    nuevoNombre.value = "";
			    nuevoTelegram.value = "";
			    nuevoEmail.value = "";

			    document
			        .querySelectorAll(
			            "#modalNuevoEmpleado .workday-checkbox input"
			        )
			        .forEach(input => {
			            input.checked = false;
			        });

			    modalNuevoEmpleado
			        ?.classList.add("show");

			    document.body
			        .classList.add("modal-open");

			    nuevoNombre?.focus();
			}


			function cerrarModalNuevoEmpleado() {

			    modalNuevoEmpleado
			        ?.classList.remove("show");

			    document.body
			        .classList.remove("modal-open");

			    limpiarErrorNuevoEmpleado();
			}
			function mostrarErrorNuevoEmpleado(
			    mensaje
			) {

			    if (!nuevoEmpleadoError) {
			        return;
			    }

			    nuevoEmpleadoError.textContent =
			        mensaje;

			    nuevoEmpleadoError
			        .classList.add("show");
			}


			function limpiarErrorNuevoEmpleado() {

			    if (!nuevoEmpleadoError) {
			        return;
			    }

			    nuevoEmpleadoError.textContent = "";

			    nuevoEmpleadoError
			        .classList.remove("show");
			}
			async function guardarNuevoEmpleado() {

			    limpiarErrorNuevoEmpleado();


			    const nombre =
			        nuevoNombre?.value.trim();

			    let usernameTelegram =
			        nuevoTelegram?.value.trim();

			    const email =
			        nuevoEmail?.value.trim();

			    const diasLaborales =
			        Array.from(
			            document.querySelectorAll(
			                "#modalNuevoEmpleado .workday-checkbox input:checked"
			            )
			        )
			        .map(input =>
			            input.value
			        );


			    if (!nombre) {

			        mostrarErrorNuevoEmpleado(
			            "Debes indicar el nombre del empleado."
			        );

			        nuevoNombre?.focus();

			        return;
			    }


			    if (!usernameTelegram) {

			        mostrarErrorNuevoEmpleado(
			            "Debes indicar el usuario de Telegram."
			        );

			        nuevoTelegram?.focus();

			        return;
			    }


			    if (
			        usernameTelegram.startsWith("@")
			    ) {

			        usernameTelegram =
			            usernameTelegram.substring(1);
			    }


			    if (!diasLaborales.length) {

			        mostrarErrorNuevoEmpleado(
			            "Debes seleccionar al menos un día laboral."
			        );

			        return;
			    }


			    btnGuardarNuevoEmpleado.disabled =
			        true;

			    btnGuardarNuevoEmpleado.innerHTML = `
			        <span class="spinner-small"></span>
			        Creando...
			    `;


			    try {
					
					const csrfToken = document
					    .querySelector('meta[name="_csrf"]')
					    ?.getAttribute("content");

					const csrfHeader = document
					    .querySelector('meta[name="_csrf_header"]')
					    ?.getAttribute("content");

			        const response =
			            await fetch(
			                "/api/empleados",
			                {
			                    method: "POST",

								headers: {
								    "Content-Type": "application/json",
								    [csrfHeader]: csrfToken
								},

			                    body:
			                        JSON.stringify({
			                            nombre,
			                            usernameTelegram,
			                            email:
			                                email || null,
			                            activo: true,
			                            diasLaborales
			                        })
			                }
			            );


			        if (!response.ok) {

			            let mensaje =
			                "No fue posible crear el empleado.";

			            try {

			                const body =
			                    await response.json();

			                mensaje =
			                    body.mensaje
			                    || body.message
			                    || body.error
			                    || mensaje;

			            } catch {
			                // mensaje genérico
			            }

			            throw new Error(
			                mensaje
			            );
			        }


			        const empleadoCreado =
			            await response.json();


			        cerrarModalNuevoEmpleado();


			        await cargarEmpleados();


			        mostrarToast(
			            "success",
			            "Empleado creado",
			            `${empleadoCreado.nombre} fue registrado correctamente.`
			        );


			    } catch (error) {

			        mostrarErrorNuevoEmpleado(
			            error.message
			            || "No fue posible crear el empleado."
			        );


			    } finally {

			        btnGuardarNuevoEmpleado.disabled =
			            false;

			        btnGuardarNuevoEmpleado.innerHTML = `
			            <i class="bi bi-person-plus"></i>
			            Crear empleado
			        `;
			    }
			}
			btnNuevoEmpleado
			    ?.addEventListener(
			        "click",
			        abrirModalNuevoEmpleado
			    );


			btnCerrarNuevoEmpleado
			    ?.addEventListener(
			        "click",
			        cerrarModalNuevoEmpleado
			    );


			btnCancelarNuevoEmpleado
			    ?.addEventListener(
			        "click",
			        cerrarModalNuevoEmpleado
			    );


			btnGuardarNuevoEmpleado
			    ?.addEventListener(
			        "click",
			        guardarNuevoEmpleado
			    );


			modalNuevoEmpleado
			    ?.querySelector(
			        ".sgio-modal-backdrop"
			    )
			    ?.addEventListener(
			        "click",
			        cerrarModalNuevoEmpleado
			    );
				const toastContainer =
				    document.getElementById(
				        "toastContainer"
				    );


				function mostrarToast(
				    tipo,
				    titulo,
				    mensaje,
				    duracion = 4500
				) {

				    if (!toastContainer) {
				        return;
				    }


				    const iconos = {
				        success: "bi-check-circle-fill",
				        error: "bi-x-circle-fill",
				        warning: "bi-exclamation-triangle-fill",
				        info: "bi-info-circle-fill"
				    };


				    const toast =
				        document.createElement(
				            "div"
				        );


				    toast.className =
				        `sgio-toast sgio-toast-${tipo}`;


				    const icono =
				        iconos[tipo]
				        || iconos.info;


				    toast.innerHTML = `
				        <div class="sgio-toast-icon">

				            <i class="bi ${icono}"></i>

				        </div>


				        <div class="sgio-toast-content">

				            <strong>
				                ${escapeHtml(titulo)}
				            </strong>

				            <span>
				                ${escapeHtml(mensaje)}
				            </span>

				        </div>


				        <button type="button"
				                class="sgio-toast-close"
				                aria-label="Cerrar">

				            <i class="bi bi-x-lg"></i>

				        </button>
				    `;


				    toastContainer.appendChild(
				        toast
				    );


				    requestAnimationFrame(
				        () => {
				            toast.classList.add(
				                "show"
				            );
				        }
				    );


				    const cerrar = () => {

				        toast.classList.remove(
				            "show"
				        );


				        setTimeout(
				            () => toast.remove(),
				            250
				        );
				    };


				    toast
				        .querySelector(
				            ".sgio-toast-close"
				        )
				        ?.addEventListener(
				            "click",
				            cerrar
				        );


				    setTimeout(
				        cerrar,
				        duracion
				    );
				}
				document.addEventListener(
				    "keydown",
				    event => {

				        if (
				            event.key === "Escape"
				            && modalNuevoEmpleado
				                ?.classList.contains(
				                    "show"
				                )
				        ) {

				            cerrarModalNuevoEmpleado();
				        }
				    }
				);
				
				

				const btnConfirmarEstadoMasivo =
				    document.getElementById("btnConfirmarEstadoMasivo");

				btnConfirmarEstadoMasivo?.addEventListener(
				    "click",
				    async () => {

				        if (nuevoEstadoMasivo === null ||
				            empleadosSeleccionados.size === 0) {
				            return;
				        }

				        const ids = [...empleadosSeleccionados];
						
						if (ids.length === 0) {
						    errorEstadoMasivo.textContent =
						        "No hay empleados pendientes de actualizar.";

						    return;
						}
						
				        const estado = nuevoEstadoMasivo;

				        const csrfToken = document.querySelector(
				            'meta[name="_csrf"]'
				        )?.content;

				        const csrfHeader = document.querySelector(
				            'meta[name="_csrf_header"]'
				        )?.content;

				        const headers = {};

				        if (csrfToken && csrfHeader) {
				            headers[csrfHeader] = csrfToken;
				        }

				        btnConfirmarEstadoMasivo.disabled = true;
				        btnConfirmarEstadoMasivo.textContent = "Procesando...";

				        let exitosos = 0;
				        const errores = [];

						const idsExitosos = new Set();


				        try {

				            for (const id of ids) {

				                try {

				                    const response = await fetch(
				                        `/api/empleados/${id}/estado?activo=${estado}`,
				                        {
				                            method: "PATCH",
				                            headers
				                        }
				                    );

				                    if (!response.ok) {
				                        throw new Error(
				                            `HTTP ${response.status}`
				                        );
				                    }

				                    exitosos++;

									idsExitosos.add(id);


				                } catch (error) {

				                    errores.push({
				                        id,
				                        mensaje: error.message
				                    });
				                }
				            }
							
							// Retirar de la selección únicamente los empleados
							// que se actualizaron correctamente.
							idsExitosos.forEach(id => {
							    empleadosSeleccionados.delete(id);
							});

							if (errores.length === 0) {

							    cerrarModalEstadoMasivo();

							    mostrarToast(
							        "success",
							        "Estado actualizado",
							        `${exitosos} empleado(s) actualizado(s) correctamente.`
							    );


				            } else {

				                document.getElementById(
				                    "errorEstadoMasivo"
				                ).textContent =
				                    `${exitosos} actualizado(s), ` +
				                    `${errores.length} con error. ` +
				                    `IDs fallidos: ${errores.map(e => e.id).join(", ")}`;
									

								// Mostrar cuántos empleados quedaron pendientes.
								if (mensajeEstadoMasivo) {
								    const pendientes = empleadosSeleccionados.size;

								    mensajeEstadoMasivo.textContent =
								        `Se actualizaron ${exitosos} empleado(s). ` +
								        `Quedan ${pendientes} empleado(s) pendientes. ` +
								        `Puedes presionar Confirmar para reintentarlos.`;
								}
								
								if (exitosos > 0) {

								    // Algunos empleados se actualizaron correctamente
								    mostrarToast(
								        "warning",
								        "Actualización parcial",
								        `${exitosos} empleado(s) actualizado(s). ` +
								        `${empleadosSeleccionados.size} pendiente(s) de reintento.`
								    );

								} else {

								    // Ningún empleado pudo actualizarse
								    mostrarToast(
								        "error",
								        "No se pudo completar la operación",
								        `${errores.length} empleado(s) no pudieron actualizarse. ` +
								        `Puedes intentar nuevamente.`
								    );
								}

				            }


							


							// Sincronizar primero la selección real.
							actualizarSeleccionEmpleados();

							// Intentar refrescar la tabla sin confundir
							// un fallo de consulta con un fallo de actualización.


							try {

							    // Recargar el listado después de actualizar estados
							    await cargarEmpleados();

							} catch (errorRecarga) {

							    console.error(
							        "Error al refrescar empleados:",
							        errorRecarga
							    );

							    mostrarToast(
							        "warning",
							        "No se pudo actualizar la tabla",
							        "Los cambios de estado procesados se conservaron, " +
							        "pero no fue posible refrescar el listado. " +
							        "Presiona Actualizar para consultar los cambios."
							    );
							}




							} catch (error) {

							    console.error(
							        "Error inesperado en actualización masiva de estado:",
							        error
							    );

							    mostrarToast(
							        "error",
							        "Error inesperado",
							        "Ocurrió un problema al procesar la operación. " +
							        "Actualiza el listado y verifica los estados " +
							        "antes de volver a intentarlo."
							    );

							} finally {

							    btnConfirmarEstadoMasivo.disabled = false;
							    btnConfirmarEstadoMasivo.textContent = "Confirmar";
							}

				    }
				);
				

				/* ==========================================
				   SGIO-002 - MODAL DÍAS LABORALES
				   ========================================== */

				function abrirModalDiasMasivos() {

				    const cantidad = empleadosSeleccionados.size;

				    if (cantidad === 0) {
				        return;
				    }

				    // Restablecer operación
				    operacionDiasMasivos.value = "REEMPLAZAR";

				    // Limpiar selección de días anterior
				    modalDiasMasivos
				        .querySelectorAll(
				            '#diasMasivosCheckboxes input[type="checkbox"]'
				        )
				        .forEach(checkbox => {
				            checkbox.checked = false;
				        });

				    // Mostrar cantidad de empleados seleccionados
						const totalSeleccionados = empleadosSeleccionados.size;

						const seleccionadosVisibles = Array.from(
						    tbody.querySelectorAll(".empleado-checkbox")
						).filter(checkbox =>
						    empleadosSeleccionados.has(Number(checkbox.value))
						).length;

						const seleccionadosOcultos =
						    totalSeleccionados - seleccionadosVisibles;

						let mensaje =
						    `Se modificarán los días laborales de ` +
						    `${totalSeleccionados} empleado(s).`;

						if (seleccionadosOcultos > 0) {
						    mensaje +=
						        ` Atención: ${seleccionadosOcultos} empleado(s) ` +
						        `seleccionado(s) están ocultos por los filtros actuales.`;
						}

						mensajeDiasMasivos.textContent = mensaje;

				    // Limpiar errores anteriores
				    errorDiasMasivos.textContent = "";

				    // Actualizar descripción
				    actualizarDescripcionOperacionDias();

				    // Mostrar modal
				    modalDiasMasivos.classList.add("active");
				}


				function cerrarModalDiasMasivos() {

				    modalDiasMasivos.classList.remove("active");

				    errorDiasMasivos.textContent = "";
				}


				function actualizarDescripcionOperacionDias() {

				    const operacion = operacionDiasMasivos.value;

				    const descripciones = {
				        REEMPLAZAR:
				            "Los días seleccionados reemplazarán los días laborales actuales de los empleados.",

				        AGREGAR:
				            "Los días seleccionados se agregarán a los existentes, sin eliminar los actuales.",

				        QUITAR:
				            "Únicamente se eliminarán los días seleccionados de los empleados."
				    };

				    descripcionOperacionDias.textContent =
				        descripciones[operacion] || "";
				}
				

				/* ==========================================
				   SGIO-002 - EVENTOS MODAL DÍAS MASIVOS
				   ========================================== */

				// Abrir modal
				document.getElementById("btnEditarDiasSeleccionados")
				    ?.addEventListener("click", abrirModalDiasMasivos);

				// Cerrar con X
				document.getElementById("btnCerrarDiasMasivos")
				    ?.addEventListener("click", cerrarModalDiasMasivos);

				// Cerrar con Cancelar
				document.getElementById("btnCancelarDiasMasivos")
				    ?.addEventListener("click", cerrarModalDiasMasivos);

				// Cerrar al hacer clic fuera
				modalDiasMasivos
				    ?.querySelector(".sgio-modal-backdrop")
				    ?.addEventListener("click", cerrarModalDiasMasivos);

				// Cambiar descripción según operación
				operacionDiasMasivos
				    ?.addEventListener(
				        "change",
				        actualizarDescripcionOperacionDias
				    );
					

					/* ==========================================
					   SGIO-002 - VALIDACIÓN DE DÍAS MASIVOS
					   ========================================== */

					function obtenerDiasMasivosSeleccionados() {

					    return Array.from(
					        modalDiasMasivos.querySelectorAll(
					            '#diasMasivosCheckboxes input[type="checkbox"]:checked'
					        )
					    ).map(checkbox => checkbox.value);
					}


					function validarFormularioDiasMasivos() {

					    errorDiasMasivos.textContent = "";

					    if (empleadosSeleccionados.size === 0) {

					        errorDiasMasivos.textContent =
					            "Debes seleccionar al menos un empleado.";

					        return false;
					    }

					    const operacion = operacionDiasMasivos.value;

					    const operacionesValidas = [
					        "REEMPLAZAR",
					        "AGREGAR",
					        "QUITAR"
					    ];

					    if (!operacionesValidas.includes(operacion)) {

					        errorDiasMasivos.textContent =
					            "Selecciona una operación válida.";

					        return false;
					    }

					    const dias = obtenerDiasMasivosSeleccionados();

					    if (dias.length === 0) {

					        errorDiasMasivos.textContent =
					            "Debes seleccionar al menos un día laboral.";

					        return false;
					    }

					    return true;
					}
					

				   /* ==========================================
				      SGIO-002 - GUARDAR DÍAS LABORALES MASIVOS
				      ========================================== */

				   const btnConfirmarDiasMasivos =
				       document.getElementById("btnConfirmarDiasMasivos");

				   let guardandoDiasMasivos = false;

				   btnConfirmarDiasMasivos?.addEventListener("click", async () => {

				       if (guardandoDiasMasivos) {
				           return;
				       }

				       if (!validarFormularioDiasMasivos()) {
				           return;
				       }

				       const empleadoIds = [...empleadosSeleccionados];
				       const operacion = operacionDiasMasivos.value;
				       const diasLaborales = obtenerDiasMasivosSeleccionados();

				       const nombresOperaciones = {
				           REEMPLAZAR: "reemplazar",
				           AGREGAR: "agregar",
				           QUITAR: "quitar"
				       };

				       const confirmado = window.confirm(
				           `¿Confirmas ${nombresOperaciones[operacion]} los días laborales ` +
				           `de ${empleadoIds.length} empleado(s)?`
				       );

				       if (!confirmado) {
				           return;
				       }

				       const csrfToken = document.querySelector(
				           'meta[name="_csrf"]'
				       )?.content;

				       const csrfHeader = document.querySelector(
				           'meta[name="_csrf_header"]'
				       )?.content;

				       const headers = {
				           "Content-Type": "application/json"
				       };

				       if (csrfToken && csrfHeader) {
				           headers[csrfHeader] = csrfToken;
				       }

				       guardandoDiasMasivos = true;
				       btnConfirmarDiasMasivos.disabled = true;
				       btnConfirmarDiasMasivos.textContent = "Guardando...";
				       errorDiasMasivos.textContent = "";

				       try {

				           const response = await fetch(
				               "/api/empleados/dias-laborales/masivo",
				               {
				                   method: "PATCH",
				                   headers,
				                   body: JSON.stringify({
				                       empleadoIds,
				                       operacion,
				                       diasLaborales
				                   })
				               }
				           );

				           const resultado = await response.json().catch(() => ({}));

				           if (!response.ok) {
				               throw new Error(
				                   resultado.mensaje ||
				                   `Error HTTP ${response.status}`
				               );
				           }



						   cerrarModalDiasMasivos();

						   // Limpiar selección después de guardar correctamente
						   empleadosSeleccionados.clear();

						   // Sincronizar la selección inmediatamente
						   actualizarSeleccionEmpleados();

						   // Refrescar la tabla sin confundir errores de
						   // consulta con errores de actualización.
						   try {

						       await cargarEmpleados();
							   
							   mostrarToast(
							       "success",
							       "Días laborales actualizados",
							       resultado.mensaje ||
							       "Los días laborales se actualizaron correctamente."
							   );

						   } catch (errorRecarga) {

						       console.error(
						           "Error al refrescar los días laborales:",
						           errorRecarga
						       );

						       mostrarToast(
						           "warning",
						           "Cambios guardados",
						           "Los días laborales se actualizaron correctamente, " +
						           "pero no fue posible refrescar la tabla. " +
						           "Presiona Actualizar para consultar los cambios."
						       );
						   }






				       } catch (error) {

				           console.error(
				               "Error al actualizar días laborales:",
				               error
				           );

				           errorDiasMasivos.textContent =
				               error.message ||
				               "No fue posible actualizar los días laborales.";

				       } finally {

				           guardandoDiasMasivos = false;
				           btnConfirmarDiasMasivos.disabled = false;
				           btnConfirmarDiasMasivos.textContent = "Aplicar cambios";
				       }
				   });



				
				/* =========================================================
				   INICIALIZACIÓN
				   ========================================================= */
				   
				   cargarEmpleadosSeguro();

    }
);