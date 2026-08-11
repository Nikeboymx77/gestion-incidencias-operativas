document.addEventListener("DOMContentLoaded", () => {

    // =========================================================
    // CONFIGURACIÓN GENERAL
    // =========================================================

    const folio =
        window.SGIO?.folio;


    // =========================================================
    // HELPERS GENERALES
    // =========================================================

    function abrirModal(modal, elementoFocus = null) {

        modal?.classList.add("show");

        document.body.classList.add(
            "modal-open"
        );

        elementoFocus?.focus();
    }


    function cerrarModal(modal) {

        modal?.classList.remove("show");

        document.body.classList.remove(
            "modal-open"
        );
    }


    function mostrarError(
        contenedor,
        mensaje
    ) {

        if (!contenedor) {
            return;
        }

        contenedor.textContent =
            mensaje;

        contenedor.classList.add(
            "show"
        );
    }


    function limpiarError(
        contenedor
    ) {

        if (!contenedor) {
            return;
        }

        contenedor.textContent = "";

        contenedor.classList.remove(
            "show"
        );
    }


    function activarLoading(
        boton,
        texto
    ) {

        if (!boton) {
            return;
        }

        boton.disabled = true;

        boton.innerHTML =
            `<span class="spinner-small"></span> ${texto}`;
    }


    function desactivarLoading(
        boton,
        htmlOriginal
    ) {

        if (!boton) {
            return;
        }

        boton.disabled = false;

        boton.innerHTML =
            htmlOriginal;
    }


    async function obtenerMensajeError(
        response
    ) {

        try {

            const body =
                await response.json();

            return (
                body.mensaje
                || body.message
                || body.error
                || "No fue posible completar la operación."
            );

        } catch {

            return (
                "No fue posible completar la operación."
            );
        }
    }


	async function ejecutarOperacion({
	    endpoint,
	    body,
	    boton,
	    textoLoading,
	    htmlOriginal,
	    contenedorError,
	    mensajeError,
	    tituloExito,
	    mensajeExito
	}) {

	    activarLoading(
	        boton,
	        textoLoading
	    );

	    try {

	        const response =
	            await fetch(
	                endpoint,
	                {
	                    method: "PUT",

	                    headers: {
	                        "Content-Type":
	                            "application/json"
	                    },

	                    body:
	                        JSON.stringify(body)
	                }
	            );

	        if (!response.ok) {

	            const mensaje =
	                await obtenerMensajeError(
	                    response
	                );

	            throw new Error(mensaje);
	        }

	        const incidencia =
	            await response.json();


	        mostrarToast(
	            "success",
	            tituloExito,
	            mensajeExito
	                || `La incidencia ${incidencia.folio} fue actualizada correctamente.`
	        );


	        /*
	         * Damos tiempo para que el usuario
	         * alcance a ver el Toast.
	         */
	        setTimeout(
	            () => {
	                window.location.reload();
	            },
	            900
	        );


	    } catch (error) {

	        const mensaje =
	            error.message
	            || mensajeError;

	        mostrarError(
	            contenedorError,
	            mensaje
	        );

	        mostrarToast(
	            "error",
	            "No fue posible completar la operación",
	            mensaje
	        );


	    } finally {

	        desactivarLoading(
	            boton,
	            htmlOriginal
	        );
	    }
	}


    // =========================================================
    // TOMAR
    // =========================================================

    const Tomar = {

        boton:
            document.getElementById(
                "btnTomar"
            ),

        modal:
            document.getElementById(
                "modalTomar"
            ),

        cerrar:
            document.getElementById(
                "btnCerrarModalTomar"
            ),

        cancelar:
            document.getElementById(
                "btnCancelarTomar"
            ),

        confirmar:
            document.getElementById(
                "btnConfirmarTomar"
            ),

        usuario:
            document.getElementById(
                "tomarUsuario"
            ),

        comentario:
            document.getElementById(
                "tomarComentario"
            ),

        error:
            document.getElementById(
                "tomarError"
            )
    };


    function abrirModalTomar() {

        limpiarError(
            Tomar.error
        );

        if (Tomar.comentario) {
            Tomar.comentario.value = "";
        }

        abrirModal(
            Tomar.modal,
            Tomar.usuario
        );
    }


    function cerrarModalTomar() {

        cerrarModal(
            Tomar.modal
        );

        limpiarError(
            Tomar.error
        );
    }


    async function confirmarTomarIncidencia() {

        const usuario =
            Tomar.usuario?.value.trim();

        const comentario =
            Tomar.comentario?.value.trim();


        if (!usuario) {

            mostrarError(
                Tomar.error,
                "Debes indicar el usuario que tomará la incidencia."
            );

            Tomar.usuario?.focus();

            return;
        }


        if (!comentario) {

            mostrarError(
                Tomar.error,
                "Debes agregar un comentario."
            );

            Tomar.comentario?.focus();

            return;
        }


        await ejecutarOperacion({

            endpoint:
                `/api/incidencias/${encodeURIComponent(folio)}/tomar`,

            body: {
                usuario,
                comentario
            },

            boton:
                Tomar.confirmar,

            textoLoading:
                "Procesando...",

            htmlOriginal:
                '<i class="bi bi-play-circle"></i> Tomar incidencia',

            contenedorError:
                Tomar.error,

            mensajeError:
                "No fue posible tomar la incidencia.",
				
			tituloExito:
			    "Incidencia tomada",
	
			mensajeExito:
			    `La incidencia ${folio} quedó EN_PROCESO.`
        });
    }


    // =========================================================
    // RESOLVER
    // =========================================================

    const Resolver = {

        boton:
            document.getElementById(
                "btnResolver"
            ),

        modal:
            document.getElementById(
                "modalResolver"
            ),

        cerrar:
            document.getElementById(
                "btnCerrarModalResolver"
            ),

        cancelar:
            document.getElementById(
                "btnCancelarResolver"
            ),

        confirmar:
            document.getElementById(
                "btnConfirmarResolver"
            ),

        usuario:
            document.getElementById(
                "resolverUsuario"
            ),

        comentario:
            document.getElementById(
                "resolverComentario"
            ),

        error:
            document.getElementById(
                "resolverError"
            )
    };


    function abrirModalResolver() {

        limpiarError(
            Resolver.error
        );

        if (Resolver.comentario) {
            Resolver.comentario.value = "";
        }

        abrirModal(
            Resolver.modal,
            Resolver.usuario
        );
    }


    function cerrarModalResolver() {

        cerrarModal(
            Resolver.modal
        );

        limpiarError(
            Resolver.error
        );
    }


    async function confirmarResolverIncidencia() {

        const usuario =
            Resolver.usuario?.value.trim();

        const comentario =
            Resolver.comentario?.value.trim();


        if (!usuario) {

            mostrarError(
                Resolver.error,
                "Debes indicar el usuario que resuelve la incidencia."
            );

            Resolver.usuario?.focus();

            return;
        }


        if (!comentario) {

            mostrarError(
                Resolver.error,
                "Debes agregar un comentario de resolución."
            );

            Resolver.comentario?.focus();

            return;
        }


        await ejecutarOperacion({

            endpoint:
                `/api/incidencias/${encodeURIComponent(folio)}/resolver`,

            body: {
                usuario,
                comentario
            },

            boton:
                Resolver.confirmar,

            textoLoading:
                "Procesando...",

            htmlOriginal:
                '<i class="bi bi-check-circle"></i> Resolver incidencia',

            contenedorError:
                Resolver.error,

            mensajeError:
                "No fue posible resolver la incidencia.",
			
			tituloExito:
			    "Incidencia resuelta",

			mensajeExito:
			    `La incidencia ${folio} fue resuelta correctamente.`
        });
    }


    // =========================================================
    // CANCELAR
    // =========================================================

    const Cancelar = {

        boton:
            document.getElementById(
                "btnCancelarIncidencia"
            ),

        modal:
            document.getElementById(
                "modalCancelar"
            ),

        cerrar:
            document.getElementById(
                "btnCerrarModalCancelar"
            ),

        cancelar:
            document.getElementById(
                "btnCerrarCancelar"
            ),

        confirmar:
            document.getElementById(
                "btnConfirmarCancelar"
            ),

        usuario:
            document.getElementById(
                "cancelarUsuario"
            ),

        comentario:
            document.getElementById(
                "cancelarComentario"
            ),

        error:
            document.getElementById(
                "cancelarError"
            )
    };


    function abrirModalCancelar() {

        limpiarError(
            Cancelar.error
        );

        if (Cancelar.comentario) {
            Cancelar.comentario.value = "";
        }

        abrirModal(
            Cancelar.modal,
            Cancelar.usuario
        );
    }


    function cerrarModalCancelar() {

        cerrarModal(
            Cancelar.modal
        );

        limpiarError(
            Cancelar.error
        );
    }


    async function confirmarCancelarIncidencia() {

        const usuario =
            Cancelar.usuario?.value.trim();

        const comentario =
            Cancelar.comentario?.value.trim();


        if (!usuario) {

            mostrarError(
                Cancelar.error,
                "Debes indicar el usuario que cancela la incidencia."
            );

            Cancelar.usuario?.focus();

            return;
        }


        if (!comentario) {

            mostrarError(
                Cancelar.error,
                "Debes indicar el motivo de la cancelación."
            );

            Cancelar.comentario?.focus();

            return;
        }


        await ejecutarOperacion({

            endpoint:
                `/api/incidencias/${encodeURIComponent(folio)}/cancelar`,

            body: {
                usuario,
                comentario
            },

            boton:
                Cancelar.confirmar,

            textoLoading:
                "Cancelando...",

            htmlOriginal:
                '<i class="bi bi-x-circle"></i> Cancelar incidencia',

            contenedorError:
                Cancelar.error,

            mensajeError:
                "No fue posible cancelar la incidencia.",
			
			tituloExito:
			    "Incidencia cancelada",

			mensajeExito:
			    `La incidencia ${folio} quedó CANCELADA.`
        });
    }


    // =========================================================
    // REASIGNAR
    // =========================================================

    const Reasignar = {

        boton:
            document.getElementById(
                "btnReasignar"
            ),

        modal:
            document.getElementById(
                "modalReasignar"
            ),

        cerrar:
            document.getElementById(
                "btnCerrarModalReasignar"
            ),

        cancelar:
            document.getElementById(
                "btnCancelarReasignar"
            ),

        confirmar:
            document.getElementById(
                "btnConfirmarReasignar"
            ),

        usuario:
            document.getElementById(
                "reasignarUsuario"
            ),

        empleado:
            document.getElementById(
                "reasignarEmpleado"
            ),

        comentario:
            document.getElementById(
                "reasignarComentario"
            ),

        error:
            document.getElementById(
                "reasignarError"
            )
    };


    async function abrirModalReasignar() {

        limpiarError(
            Reasignar.error
        );

        if (Reasignar.comentario) {
            Reasignar.comentario.value = "";
        }

        /*
         * Abrimos primero el modal para que
         * el usuario vea inmediatamente la acción.
         */
        abrirModal(
            Reasignar.modal,
            Reasignar.usuario
        );

        await cargarEmpleados();
    }


    function cerrarModalReasignar() {

        cerrarModal(
            Reasignar.modal
        );

        limpiarError(
            Reasignar.error
        );
    }


    async function cargarEmpleados() {

        if (!Reasignar.empleado) {
            return;
        }


        Reasignar.empleado.innerHTML =
            '<option value="">Cargando empleados...</option>';


        try {

            const response =
                await fetch(
                    "/api/empleados/resumen"
                );


            if (!response.ok) {

                const mensaje =
                    await obtenerMensajeError(
                        response
                    );

                throw new Error(
                    mensaje
                    || "No fue posible consultar los empleados."
                );
            }


            const empleados =
                await response.json();


            Reasignar.empleado.innerHTML =
                '<option value="">Selecciona un empleado</option>';


            empleados
                .filter(
                    empleado =>
                        empleado.activo === true
                        && empleado.usernameTelegram
                )
                .sort(
                    (a, b) =>
                        (a.nombre || "")
                            .localeCompare(
                                b.nombre || "",
                                "es"
                            )
                )
                .forEach(
                    empleado => {

                        const option =
                            document.createElement(
                                "option"
                            );


                        option.value =
                            empleado.usernameTelegram;


                        /*
                         * Ya tenemos totalActivas
                         * en el resumen operativo.
                         */
                        const totalActivas =
                            empleado.totalActivas
                            ?? (
                                (empleado.pendientes ?? 0)
                                + (empleado.enProceso ?? 0)
                                + (empleado.reabiertas ?? 0)
                            );


                        option.textContent =
                            `${empleado.nombre} `
                            + `(@${empleado.usernameTelegram}) `
                            + `· ${totalActivas} activas`;


                        Reasignar.empleado.appendChild(
                            option
                        );
                    }
                );


            if (
                Reasignar.empleado.options.length
                === 1
            ) {

                Reasignar.empleado.innerHTML =
                    '<option value="">No hay empleados activos disponibles</option>';
            }


        } catch (error) {

            Reasignar.empleado.innerHTML =
                '<option value="">Error al cargar empleados</option>';


            mostrarError(
                Reasignar.error,
                error.message
                || "No fue posible consultar los empleados."
            );
        }
    }


    async function confirmarReasignacion() {

        const usuario =
            Reasignar.usuario?.value.trim();

        const usernameTelegram =
            Reasignar.empleado?.value;

        const comentario =
            Reasignar.comentario?.value.trim();


        if (!usuario) {

            mostrarError(
                Reasignar.error,
                "Debes indicar el usuario que realiza la reasignación."
            );

            Reasignar.usuario?.focus();

            return;
        }


        if (!usernameTelegram) {

            mostrarError(
                Reasignar.error,
                "Debes seleccionar al nuevo responsable."
            );

            Reasignar.empleado?.focus();

            return;
        }


        if (!comentario) {

            mostrarError(
                Reasignar.error,
                "Debes indicar el motivo de la reasignación."
            );

            Reasignar.comentario?.focus();

            return;
        }


        await ejecutarOperacion({

            endpoint:
                `/api/incidencias/${encodeURIComponent(folio)}/reasignar`,

            body: {
                usernameTelegram,
                usuario,
                comentario
            },

            boton:
                Reasignar.confirmar,

            textoLoading:
                "Reasignando...",

            htmlOriginal:
                '<i class="bi bi-arrow-left-right"></i> Reasignar',

            contenedorError:
                Reasignar.error,

            mensajeError:
                "No fue posible reasignar la incidencia.",
			
			tituloExito:
			    "Incidencia reasignada",

			mensajeExito:
			    `La incidencia ${folio} fue reasignada correctamente.`
        });
    }


    // =========================================================
    // REABRIR
    // =========================================================

    const Reabrir = {

        boton:
            document.getElementById(
                "btnReabrir"
            ),

        modal:
            document.getElementById(
                "modalReabrir"
            ),

        cerrar:
            document.getElementById(
                "btnCerrarModalReabrir"
            ),

        cancelar:
            document.getElementById(
                "btnCancelarReabrir"
            ),

        confirmar:
            document.getElementById(
                "btnConfirmarReabrir"
            ),

        usuario:
            document.getElementById(
                "reabrirUsuario"
            ),

        comentario:
            document.getElementById(
                "reabrirComentario"
            ),

        error:
            document.getElementById(
                "reabrirError"
            )
    };


    function abrirModalReabrir() {

        limpiarError(
            Reabrir.error
        );

        if (Reabrir.comentario) {
            Reabrir.comentario.value = "";
        }

        abrirModal(
            Reabrir.modal,
            Reabrir.usuario
        );
    }


    function cerrarModalReabrir() {

        cerrarModal(
            Reabrir.modal
        );

        limpiarError(
            Reabrir.error
        );
    }


    async function confirmarReapertura() {

        const usuario =
            Reabrir.usuario?.value.trim();

        const comentario =
            Reabrir.comentario?.value.trim();


        if (!usuario) {

            mostrarError(
                Reabrir.error,
                "Debes indicar el usuario que reabre la incidencia."
            );

            Reabrir.usuario?.focus();

            return;
        }


        if (!comentario) {

            mostrarError(
                Reabrir.error,
                "Debes indicar el motivo de reapertura."
            );

            Reabrir.comentario?.focus();

            return;
        }


        await ejecutarOperacion({

            endpoint:
                `/api/incidencias/${encodeURIComponent(folio)}/reabrir`,

            body: {
                usuario,
                comentario
            },

            boton:
                Reabrir.confirmar,

            textoLoading:
                "Reabriendo...",

            htmlOriginal:
                '<i class="bi bi-arrow-counterclockwise"></i> Reabrir incidencia',

            contenedorError:
                Reabrir.error,

            mensajeError:
                "No fue posible reabrir la incidencia.",
			
			tituloExito:
			    "Incidencia reabierta",

			mensajeExito:
			    `La incidencia ${folio} quedó REABIERTA.`
        });
    }
	
	// =========================================================
	// TIMELINE
	// =========================================================

	const timelineIncidencia =
	    document.getElementById(
	        "timelineIncidencia"
	    );


	async function cargarTimeline() {

	    if (!timelineIncidencia || !folio) {
	        return;
	    }


	    try {

	        const response =
	            await fetch(
	                `/api/incidencias/${encodeURIComponent(folio)}/historial`
	            );


	        if (!response.ok) {

	            throw new Error(
	                "No fue posible consultar el historial."
	            );
	        }


	        const historial =
	            await response.json();


	        pintarTimeline(
	            historial
	        );


	    } catch (error) {

	        timelineIncidencia.innerHTML = `
	            <div class="timeline-empty">
	                <i class="bi bi-exclamation-circle"></i>
	                <p>
	                    ${escapeHtml(
	                        error.message
	                        || "No fue posible cargar el historial."
	                    )}
	                </p>
	            </div>
	        `;
	    }
	}
	
	function pintarTimeline(historial) {

	    if (
	        !Array.isArray(historial)
	        || historial.length === 0
	    ) {

	        timelineIncidencia.innerHTML = `
	            <div class="timeline-empty">

	                <i class="bi bi-clock-history"></i>

	                <p>
	                    Esta incidencia todavía no tiene
	                    movimientos registrados.
	                </p>

	            </div>
	        `;

	        return;
	    }


	    timelineIncidencia.innerHTML =
	        historial
	            .map(
	                (evento, indice) =>
	                    construirEventoTimeline(
	                        evento,
	                        indice === historial.length - 1
	                    )
	            )
	            .join("");
	}
	
	function construirEventoTimeline(
	    evento,
	    esUltimo
	) {

	    const accion =
	        evento.accion
	        || "MOVIMIENTO";


	    const configuracion =
	        obtenerConfiguracionAccion(
	            accion
	        );


	    const usuario =
	        evento.usuario
	        || "SISTEMA";


	    const comentario =
	        evento.comentario
	        || "Sin comentario";


	    const fecha =
	        formatearFechaEvento(
	            evento.fechaEvento
	        );


	    return `
	        <div class="timeline-item
	                    ${esUltimo ? "timeline-item-last" : ""}">

	            <div class="timeline-marker
	                        ${configuracion.clase}">

	                <i class="bi ${configuracion.icono}"></i>

	            </div>


	            <div class="timeline-content">

	                <div class="timeline-header">

	                    <strong>
	                        ${escapeHtml(
	                            configuracion.titulo
	                        )}
	                    </strong>

	                    <span class="timeline-date">
	                        ${escapeHtml(fecha)}
	                    </span>

	                </div>


	                <div class="timeline-user">

	                    <i class="bi bi-person"></i>

	                    ${escapeHtml(usuario)}

	                </div>


	                <p class="timeline-comment">
	                    ${escapeHtml(comentario)}
	                </p>

	            </div>

	        </div>
	    `;
	}
	
	function obtenerConfiguracionAccion(
	    accion
	) {

	    const configuraciones = {

	        PENDIENTE: {
	            titulo:
	                "Incidencia registrada",

	            icono:
	                "bi-inbox",

	            clase:
	                "timeline-pendiente"
	        },

	        EN_PROCESO: {
	            titulo:
	                "Incidencia tomada",

	            icono:
	                "bi-play-circle",

	            clase:
	                "timeline-proceso"
	        },

	        RESUELTA: {
	            titulo:
	                "Incidencia resuelta",

	            icono:
	                "bi-check-circle",

	            clase:
	                "timeline-resuelta"
	        },

	        CANCELADA: {
	            titulo:
	                "Incidencia cancelada",

	            icono:
	                "bi-x-circle",

	            clase:
	                "timeline-cancelada"
	        },

	        REASIGNADA: {
	            titulo:
	                "Incidencia reasignada",

	            icono:
	                "bi-arrow-left-right",

	            clase:
	                "timeline-reasignada"
	        },

	        REABIERTA: {
	            titulo:
	                "Incidencia reabierta",

	            icono:
	                "bi-arrow-counterclockwise",

	            clase:
	                "timeline-reabierta"
	        }

	    };


	    return configuraciones[accion]
	        || {
	            titulo: accion,

	            icono:
	                "bi-circle",

	            clase:
	                "timeline-default"
	        };
	}
	
	function formatearFechaEvento(
	    fecha
	) {

	    if (!fecha) {
	        return "-";
	    }


	    const valor =
	        new Date(fecha);


	    if (
	        Number.isNaN(
	            valor.getTime()
	        )
	    ) {
	        return fecha;
	    }


	    return valor.toLocaleString(
	        "es-MX",
	        {
	            day: "2-digit",
	            month: "2-digit",
	            year: "numeric",

	            hour: "2-digit",
	            minute: "2-digit"
	        }
	    );
	}
	
	function escapeHtml(
	    valor
	) {

	    return String(valor ?? "")
	        .replaceAll(
	            "&",
	            "&amp;"
	        )
	        .replaceAll(
	            "<",
	            "&lt;"
	        )
	        .replaceAll(
	            ">",
	            "&gt;"
	        )
	        .replaceAll(
	            '"',
	            "&quot;"
	        )
	        .replaceAll(
	            "'",
	            "&#039;"
	        );
	}


    // =========================================================
    // LISTENERS
    // =========================================================


    // TOMAR

    Tomar.boton?.addEventListener(
        "click",
        abrirModalTomar
    );

    Tomar.cerrar?.addEventListener(
        "click",
        cerrarModalTomar
    );

    Tomar.cancelar?.addEventListener(
        "click",
        cerrarModalTomar
    );

    Tomar.confirmar?.addEventListener(
        "click",
        confirmarTomarIncidencia
    );


    // RESOLVER

    Resolver.boton?.addEventListener(
        "click",
        abrirModalResolver
    );

    Resolver.cerrar?.addEventListener(
        "click",
        cerrarModalResolver
    );

    Resolver.cancelar?.addEventListener(
        "click",
        cerrarModalResolver
    );

    Resolver.confirmar?.addEventListener(
        "click",
        confirmarResolverIncidencia
    );


    // CANCELAR

    Cancelar.boton?.addEventListener(
        "click",
        abrirModalCancelar
    );

    Cancelar.cerrar?.addEventListener(
        "click",
        cerrarModalCancelar
    );

    Cancelar.cancelar?.addEventListener(
        "click",
        cerrarModalCancelar
    );

    Cancelar.confirmar?.addEventListener(
        "click",
        confirmarCancelarIncidencia
    );


    // REASIGNAR

    Reasignar.boton?.addEventListener(
        "click",
        abrirModalReasignar
    );

    Reasignar.cerrar?.addEventListener(
        "click",
        cerrarModalReasignar
    );

    Reasignar.cancelar?.addEventListener(
        "click",
        cerrarModalReasignar
    );

    Reasignar.confirmar?.addEventListener(
        "click",
        confirmarReasignacion
    );


    // REABRIR

    Reabrir.boton?.addEventListener(
        "click",
        abrirModalReabrir
    );

    Reabrir.cerrar?.addEventListener(
        "click",
        cerrarModalReabrir
    );

    Reabrir.cancelar?.addEventListener(
        "click",
        cerrarModalReabrir
    );

    Reabrir.confirmar?.addEventListener(
        "click",
        confirmarReapertura
    );


    // =========================================================
    // CLIC SOBRE BACKDROP
    // =========================================================

    [
        [Tomar.modal, cerrarModalTomar],
        [Resolver.modal, cerrarModalResolver],
        [Cancelar.modal, cerrarModalCancelar],
        [Reasignar.modal, cerrarModalReasignar],
        [Reabrir.modal, cerrarModalReabrir]

    ].forEach(
        ([modal, funcionCerrar]) => {

            modal
                ?.querySelector(
                    ".sgio-modal-backdrop"
                )
                ?.addEventListener(
                    "click",
                    funcionCerrar
                );
        }
    );


    // =========================================================
    // ESCAPE
    // =========================================================

    document.addEventListener(
        "keydown",
        (event) => {

            if (event.key !== "Escape") {
                return;
            }


            if (
                Tomar.modal
                    ?.classList.contains("show")
            ) {
                cerrarModalTomar();
                return;
            }


            if (
                Resolver.modal
                    ?.classList.contains("show")
            ) {
                cerrarModalResolver();
                return;
            }


            if (
                Cancelar.modal
                    ?.classList.contains("show")
            ) {
                cerrarModalCancelar();
                return;
            }


            if (
                Reasignar.modal
                    ?.classList.contains("show")
            ) {
                cerrarModalReasignar();
                return;
            }


            if (
                Reabrir.modal
                    ?.classList.contains("show")
            ) {
                cerrarModalReabrir();
            }
        }
    );
	
	cargarTimeline();
	
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
	        document.createElement("div");

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
	                ${escapeHtmlToast(titulo)}
	            </strong>

	            <span>
	                ${escapeHtmlToast(mensaje)}
	            </span>

	        </div>

	        <button type="button"
	                class="sgio-toast-close"
	                aria-label="Cerrar">

	            <i class="bi bi-x-lg"></i>

	        </button>
	    `;

	    toastContainer.appendChild(toast);

	    requestAnimationFrame(() => {
	        toast.classList.add("show");
	    });

	    const cerrar = () => {

	        toast.classList.remove("show");

	        setTimeout(
	            () => toast.remove(),
	            250
	        );
	    };

	    toast
	        .querySelector(".sgio-toast-close")
	        ?.addEventListener(
	            "click",
	            cerrar
	        );

	    setTimeout(
	        cerrar,
	        duracion
	    );
	}


	function escapeHtmlToast(valor) {

	    return String(valor ?? "")
	        .replaceAll("&", "&amp;")
	        .replaceAll("<", "&lt;")
	        .replaceAll(">", "&gt;")
	        .replaceAll('"', "&quot;")
	        .replaceAll("'", "&#039;");
	}

});