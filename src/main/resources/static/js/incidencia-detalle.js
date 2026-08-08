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
        mensajeError
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

                throw new Error(
                    mensaje
                );
            }


            /*
             * No necesitamos realmente utilizar
             * el JSON de respuesta.
             *
             * Si el backend responde correctamente,
             * recargamos el detalle.
             */
            await response.json();

            window.location.reload();


        } catch (error) {

            mostrarError(
                contenedorError,
                error.message
                || mensajeError
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
                "No fue posible tomar la incidencia."
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
                "No fue posible resolver la incidencia."
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
                "No fue posible cancelar la incidencia."
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
                "No fue posible reasignar la incidencia."
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
                "No fue posible reabrir la incidencia."
        });
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

});