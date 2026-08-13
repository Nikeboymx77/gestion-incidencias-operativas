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


                const empleados =
                    await response.json();


                actualizarMetricas(
                    empleados
                );


                renderizarEmpleados(
                    empleados
                );


            } catch (error) {

                console.error(
                    error
                );


                tbody.innerHTML = `
                    <tr>

                        <td colspan="8"
                            class="employees-loading">

                            No fue posible cargar
                            los empleados.

                        </td>

                    </tr>
                `;
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

                        <td colspan="8"
                            class="employees-loading">

                            No hay empleados registrados.

                        </td>

                    </tr>
                `;

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


        btnActualizar
            ?.addEventListener(
                "click",
                cargarEmpleados
            );


        
		
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

			        const response =
			            await fetch(
			                "/api/empleados",
			                {
			                    method: "POST",

			                    headers: {
			                        "Content-Type":
			                            "application/json"
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
				
				/* =========================================================
				   INICIALIZACIÓN
				   ========================================================= */
				   
				cargarEmpleados();
    }
);