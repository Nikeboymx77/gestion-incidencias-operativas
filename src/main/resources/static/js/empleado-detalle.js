document.addEventListener("DOMContentLoaded", () => {

    // =========================================================
    // ESTADO GENERAL
    // =========================================================

    const empleadoId = obtenerEmpleadoId();
    let empleadoActual = null;
    let ausenciaSeleccionadaId = null;

    if (!empleadoId) {
        mostrarError("No fue posible identificar al empleado.");
        return;
    }


    // =========================================================
    // REFERENCIAS - EDITAR EMPLEADO
    // =========================================================

    const btnEditarEmpleado = document.getElementById("btnEditarEmpleado");
    const modalEditarEmpleado = document.getElementById("modalEditarEmpleado");
    const btnCerrarEditarEmpleado = document.getElementById("btnCerrarEditarEmpleado");
    const btnCancelarEditarEmpleado = document.getElementById("btnCancelarEditarEmpleado");
    const btnGuardarEditarEmpleado = document.getElementById("btnGuardarEditarEmpleado");
    const editarNombre = document.getElementById("editarNombre");
    const editarTelegram = document.getElementById("editarTelegram");
    const editarEmail = document.getElementById("editarEmail");
    const editarEmpleadoError = document.getElementById("editarEmpleadoError");


    // =========================================================
    // REFERENCIAS - ACTIVAR / DESACTIVAR
    // =========================================================

    const btnCambiarEstadoEmpleado = document.getElementById("btnCambiarEstadoEmpleado");
    const modalEstadoEmpleado = document.getElementById("modalEstadoEmpleado");
    const btnCerrarEstadoEmpleado = document.getElementById("btnCerrarEstadoEmpleado");
    const btnCancelarEstadoEmpleado = document.getElementById("btnCancelarEstadoEmpleado");
    const btnConfirmarEstadoEmpleado = document.getElementById("btnConfirmarEstadoEmpleado");
    const tituloEstadoEmpleado = document.getElementById("tituloEstadoEmpleado");
    const estadoEmpleadoMensajePrincipal = document.getElementById("estadoEmpleadoMensajePrincipal");
    const estadoEmpleadoMensajeSecundario = document.getElementById("estadoEmpleadoMensajeSecundario");
    const estadoEmpleadoError = document.getElementById("estadoEmpleadoError");


    // =========================================================
    // REFERENCIAS - REGISTRAR AUSENCIA
    // =========================================================

    const btnRegistrarAusencia = document.getElementById("btnRegistrarAusencia");
    const modalRegistrarAusencia = document.getElementById("modalRegistrarAusencia");
    const btnCerrarRegistrarAusencia = document.getElementById("btnCerrarRegistrarAusencia");
    const btnCancelarRegistrarAusencia = document.getElementById("btnCancelarRegistrarAusencia");
    const btnGuardarRegistrarAusencia = document.getElementById("btnGuardarRegistrarAusencia");
    const ausenciaFechaInicio = document.getElementById("ausenciaFechaInicio");
    const ausenciaFechaFin = document.getElementById("ausenciaFechaFin");
    const ausenciaMotivo = document.getElementById("ausenciaMotivo");
    const ausenciaObservaciones = document.getElementById("ausenciaObservaciones");
    const registrarAusenciaError = document.getElementById("registrarAusenciaError");


    // =========================================================
    // REFERENCIAS - ELIMINAR AUSENCIA
    // =========================================================

    const modalEliminarAusencia = document.getElementById("modalEliminarAusencia");
    const btnCerrarEliminarAusencia = document.getElementById("btnCerrarEliminarAusencia");
    const btnCancelarEliminarAusencia = document.getElementById("btnCancelarEliminarAusencia");
    const btnConfirmarEliminarAusencia = document.getElementById("btnConfirmarEliminarAusencia");
    const eliminarAusenciaError = document.getElementById("eliminarAusenciaError");


    // =========================================================
    // TOASTS
    // =========================================================

    let toastContainer = document.getElementById("toastContainer");

    if (!toastContainer) {
        toastContainer = document.createElement("div");
        toastContainer.id = "toastContainer";
        toastContainer.className = "toast-container";
        toastContainer.setAttribute("aria-live", "polite");
        toastContainer.setAttribute("aria-atomic", "true");
        document.body.appendChild(toastContainer);
    }

    function mostrarToast(tipo, titulo, mensaje, duracion = 4500) {
        if (!toastContainer) {
            return;
        }

        const iconos = {
            success: "bi-check-circle-fill",
            error: "bi-x-circle-fill",
            warning: "bi-exclamation-triangle-fill",
            info: "bi-info-circle-fill"
        };

        const toast = document.createElement("div");
        toast.className = `sgio-toast sgio-toast-${tipo}`;

        toast.innerHTML = `
            <div class="sgio-toast-icon">
                <i class="bi ${iconos[tipo] || iconos.info}"></i>
            </div>

            <div class="sgio-toast-content">
                <strong>${escapeHtml(titulo)}</strong>
                <span>${escapeHtml(mensaje)}</span>
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

        let cerrado = false;

        const cerrar = () => {
            if (cerrado) {
                return;
            }

            cerrado = true;
            toast.classList.remove("show");

            setTimeout(() => {
                toast.remove();
            }, 250);
        };

        toast.querySelector(".sgio-toast-close")
            ?.addEventListener("click", cerrar);

        setTimeout(cerrar, duracion);
    }


    // =========================================================
    // CARGA DEL EMPLEADO
    // =========================================================

    async function cargarEmpleado() {
        try {
            const response = await fetch(
                `/api/empleados/id/${encodeURIComponent(empleadoId)}`
            );

            if (!response.ok) {
                throw new Error("No fue posible consultar el empleado.");
            }

            empleadoActual = await response.json();
            pintarEmpleado(empleadoActual);

        } catch (error) {
            console.error(error);
            mostrarError(error.message || "No fue posible consultar el empleado.");
        }
    }


    function obtenerEmpleadoId() {
        const partes = window.location.pathname
            .split("/")
            .filter(Boolean);

        return partes[partes.length - 1];
    }


    function pintarEmpleado(empleado) {
        empleadoActual = empleado;

        setTexto("empleadoNombre", empleado.nombre || "Sin nombre");
        setTexto("empleadoTelegram", formatearTelegram(empleado.usernameTelegram));
        setTexto("empleadoEmail", empleado.email || "Sin correo");
        setTexto("empleadoTelegramDetalle", formatearTelegram(empleado.usernameTelegram));
        setTexto("empleadoUltimaAsignacion", formatearFecha(empleado.ultimaAsignacion));
        setTexto("empleadoTotalActivas", empleado.totalActivas ?? 0);
        setTexto("empleadoPendientes", empleado.pendientes ?? 0);
        setTexto("empleadoEnProceso", empleado.enProceso ?? 0);
        setTexto("empleadoReabiertas", empleado.reabiertas ?? 0);

        pintarEstado(empleado.activo);
        pintarDiasLaborales(empleado.diasLaborales || []);
        pintarAusencias(empleado.ausencias || []);
    }


    function pintarEstado(activo) {
        const elemento = document.getElementById("empleadoEstado");

        if (elemento) {
            elemento.textContent = activo ? "ACTIVO" : "INACTIVO";
            elemento.className = activo
                ? "employee-detail-status active"
                : "employee-detail-status inactive";
        }

        if (btnCambiarEstadoEmpleado) {
            btnCambiarEstadoEmpleado.innerHTML = activo
                ? `
                    <i class="bi bi-person-dash"></i>
                    Desactivar empleado
                  `
                : `
                    <i class="bi bi-person-check"></i>
                    Activar empleado
                  `;
        }
    }


    function pintarDiasLaborales(dias) {
        const contenedor = document.getElementById("diasLaborales");

        if (!contenedor) {
            return;
        }

        const semana = [
            ["MONDAY", "L"],
            ["TUESDAY", "M"],
            ["WEDNESDAY", "X"],
            ["THURSDAY", "J"],
            ["FRIDAY", "V"],
            ["SATURDAY", "S"],
            ["SUNDAY", "D"]
        ];

        contenedor.innerHTML = semana
            .map(([valor, etiqueta]) => {
                const activo = dias.includes(valor);

                return `
                    <span class="workday ${activo ? "active" : ""}">
                        ${etiqueta}
                    </span>
                `;
            })
            .join("");
    }


    function pintarAusencias(ausencias) {
        const contenedor = document.getElementById("ausenciasEmpleado");

        if (!contenedor) {
            return;
        }

        if (!ausencias.length) {
            contenedor.innerHTML = `
                <div class="absence-empty">
                    <i class="bi bi-calendar-check"></i>
                    No tiene ausencias registradas.
                </div>
            `;
            return;
        }

        contenedor.innerHTML = ausencias
            .map(ausencia => `
                <div class="absence-item">

                    <div class="absence-item-header">

                        <div class="absence-info">
                            <strong>
                                ${escapeHtml(ausencia.motivo || "AUSENCIA")}
                            </strong>

                            <span>
                                ${formatearFechaSimple(ausencia.fechaInicio)}
                                →
                                ${formatearFechaSimple(ausencia.fechaFin)}
                            </span>
                        </div>

                        <button type="button"
                                class="absence-delete-button"
                                data-ausencia-id="${escapeHtml(ausencia.id)}"
                                title="Eliminar ausencia"
                                aria-label="Eliminar ausencia">
                            <i class="bi bi-trash"></i>
                        </button>

                    </div>

                    <p>
                        ${escapeHtml(ausencia.observaciones || "Sin observaciones")}
                    </p>

                </div>
            `)
            .join("");

        contenedor.querySelectorAll(".absence-delete-button")
            .forEach(boton => {
                boton.addEventListener("click", () => {
                    const ausenciaId = boton.dataset.ausenciaId;
                    abrirModalEliminarAusencia(ausenciaId);
                });
            });
    }


    // =========================================================
    // EDITAR EMPLEADO
    // =========================================================

    function abrirModalEditarEmpleado() {
        if (!empleadoActual) {
            return;
        }

        limpiarErrorEditarEmpleado();

        editarNombre.value = empleadoActual.nombre || "";
        editarTelegram.value = empleadoActual.usernameTelegram || "";
        editarEmail.value = empleadoActual.email || "";

        document.querySelectorAll(".workday-checkbox input")
            .forEach(input => {
                input.checked = empleadoActual.diasLaborales
                    ?.includes(input.value) ?? false;
            });

        abrirModal(modalEditarEmpleado);
        editarNombre?.focus();
    }


    function cerrarModalEditarEmpleado() {
        cerrarModal(modalEditarEmpleado);
        limpiarErrorEditarEmpleado();
    }


    async function guardarCambiosEmpleado() {
        limpiarErrorEditarEmpleado();

        const nombre = editarNombre?.value.trim();
        let usernameTelegram = editarTelegram?.value.trim();
        const email = editarEmail?.value.trim();

        const diasLaborales = Array.from(
            document.querySelectorAll(".workday-checkbox input:checked")
        ).map(input => input.value);

        if (!nombre) {
            mostrarErrorElemento(editarEmpleadoError, "Debes indicar el nombre del empleado.");
            editarNombre?.focus();
            return;
        }

        if (!usernameTelegram) {
            mostrarErrorElemento(editarEmpleadoError, "Debes indicar el usuario de Telegram.");
            editarTelegram?.focus();
            return;
        }

        if (usernameTelegram.startsWith("@")) {
            usernameTelegram = usernameTelegram.substring(1);
        }

        if (!diasLaborales.length) {
            mostrarErrorElemento(
                editarEmpleadoError,
                "Debes seleccionar al menos un día laboral."
            );
            return;
        }

        activarLoading(btnGuardarEditarEmpleado, "Guardando...");

        try {
            const response = await fetch(
                `/api/empleados/${encodeURIComponent(empleadoId)}`,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        nombre,
                        usernameTelegram,
                        email: email || null,
                        activo: null,
                        diasLaborales
                    })
                }
            );

            if (!response.ok) {
                throw new Error(
                    await obtenerMensajeError(
                        response,
                        "No fue posible actualizar el empleado."
                    )
                );
            }

            empleadoActual = await response.json();

            cerrarModalEditarEmpleado();
            pintarEmpleado(empleadoActual);

            mostrarToast(
                "success",
                "Empleado actualizado",
                `${empleadoActual.nombre} fue actualizado correctamente.`
            );

        } catch (error) {
            mostrarErrorElemento(
                editarEmpleadoError,
                error.message || "No fue posible actualizar el empleado."
            );

        } finally {
            restaurarBoton(
                btnGuardarEditarEmpleado,
                '<i class="bi bi-check-circle"></i> Guardar cambios'
            );
        }
    }


    // =========================================================
    // ACTIVAR / DESACTIVAR EMPLEADO
    // =========================================================

    function abrirModalEstadoEmpleado() {
        if (!empleadoActual) {
            return;
        }

        limpiarErrorElemento(estadoEmpleadoError);

        const activo = empleadoActual.activo === true;

        tituloEstadoEmpleado.textContent = activo
            ? "Desactivar empleado"
            : "Activar empleado";

        estadoEmpleadoMensajePrincipal.textContent = activo
            ? `¿Deseas desactivar a ${empleadoActual.nombre}?`
            : `¿Deseas activar a ${empleadoActual.nombre}?`;

        estadoEmpleadoMensajeSecundario.textContent = activo
            ? "El empleado dejará de participar en nuevas asignaciones automáticas."
            : "El empleado volverá a estar disponible para recibir nuevas asignaciones.";

        btnConfirmarEstadoEmpleado.innerHTML = activo
            ? '<i class="bi bi-person-dash"></i> Desactivar'
            : '<i class="bi bi-person-check"></i> Activar';

        btnConfirmarEstadoEmpleado.classList.toggle("activate", !activo);

        abrirModal(modalEstadoEmpleado);
    }


    function cerrarModalEstadoEmpleado() {
        cerrarModal(modalEstadoEmpleado);
        limpiarErrorElemento(estadoEmpleadoError);
    }


    async function confirmarCambioEstadoEmpleado() {
        if (!empleadoActual) {
            return;
        }

        const nuevoEstado = !empleadoActual.activo;

        activarLoading(btnConfirmarEstadoEmpleado, "Procesando...");

        try {
            const response = await fetch(
                `/api/empleados/${encodeURIComponent(empleadoId)}/estado?activo=${nuevoEstado}`,
                {
                    method: "PATCH"
                }
            );

            if (!response.ok) {
                throw new Error(
                    await obtenerMensajeError(
                        response,
                        "No fue posible actualizar el estado del empleado."
                    )
                );
            }

            empleadoActual = await response.json();

            cerrarModalEstadoEmpleado();
            pintarEmpleado(empleadoActual);

            mostrarToast(
                "success",
                nuevoEstado ? "Empleado activado" : "Empleado desactivado",
                nuevoEstado
                    ? `${empleadoActual.nombre} ya puede recibir nuevas asignaciones.`
                    : `${empleadoActual.nombre} ya no recibirá nuevas asignaciones.`
            );

        } catch (error) {
            mostrarErrorElemento(
                estadoEmpleadoError,
                error.message || "No fue posible actualizar el empleado."
            );

        } finally {
            restaurarBoton(
                btnConfirmarEstadoEmpleado,
                empleadoActual?.activo
                    ? '<i class="bi bi-person-dash"></i> Desactivar'
                    : '<i class="bi bi-person-check"></i> Activar'
            );
        }
    }


    // =========================================================
    // REGISTRAR AUSENCIA
    // =========================================================

    function abrirModalRegistrarAusencia() {
        limpiarErrorElemento(registrarAusenciaError);

        ausenciaFechaInicio.value = "";
        ausenciaFechaFin.value = "";
        ausenciaMotivo.value = "";
        ausenciaObservaciones.value = "";

        abrirModal(modalRegistrarAusencia);
        ausenciaFechaInicio?.focus();
    }


    function cerrarModalRegistrarAusencia() {
        cerrarModal(modalRegistrarAusencia);
        limpiarErrorElemento(registrarAusenciaError);
    }


    async function guardarAusenciaEmpleado() {
        limpiarErrorElemento(registrarAusenciaError);

        const fechaInicio = ausenciaFechaInicio?.value;
        const fechaFin = ausenciaFechaFin?.value;
        const motivo = ausenciaMotivo?.value;
        const observaciones = ausenciaObservaciones?.value.trim();

        if (!fechaInicio) {
            mostrarErrorElemento(
                registrarAusenciaError,
                "Debes indicar la fecha de inicio."
            );
            ausenciaFechaInicio?.focus();
            return;
        }

        if (!fechaFin) {
            mostrarErrorElemento(
                registrarAusenciaError,
                "Debes indicar la fecha de fin."
            );
            ausenciaFechaFin?.focus();
            return;
        }

        if (fechaFin < fechaInicio) {
            mostrarErrorElemento(
                registrarAusenciaError,
                "La fecha de fin no puede ser anterior a la fecha de inicio."
            );
            ausenciaFechaFin?.focus();
            return;
        }

        if (!motivo) {
            mostrarErrorElemento(
                registrarAusenciaError,
                "Debes seleccionar un motivo."
            );
            ausenciaMotivo?.focus();
            return;
        }

        activarLoading(btnGuardarRegistrarAusencia, "Registrando...");

        try {
            const response = await fetch(
                `/api/empleados/${encodeURIComponent(empleadoId)}/ausencias`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        fechaInicio,
                        fechaFin,
                        motivo,
                        observaciones: observaciones || null
                    })
                }
            );

            if (!response.ok) {
                throw new Error(
                    await obtenerMensajeError(
                        response,
                        "No fue posible registrar la ausencia."
                    )
                );
            }

            empleadoActual = await response.json();

            cerrarModalRegistrarAusencia();
            pintarEmpleado(empleadoActual);

            mostrarToast(
                "success",
                "Ausencia registrada",
                `La ausencia de ${empleadoActual.nombre} fue registrada correctamente.`
            );

        } catch (error) {
            mostrarErrorElemento(
                registrarAusenciaError,
                error.message || "No fue posible registrar la ausencia."
            );

        } finally {
            restaurarBoton(
                btnGuardarRegistrarAusencia,
                '<i class="bi bi-calendar-check"></i> Registrar ausencia'
            );
        }
    }


    // =========================================================
    // ELIMINAR AUSENCIA
    // =========================================================

    function abrirModalEliminarAusencia(ausenciaId) {
        if (!ausenciaId || ausenciaId === "undefined" || ausenciaId === "null") {
            mostrarToast(
                "error",
                "No fue posible eliminar",
                "No se pudo identificar la ausencia seleccionada."
            );
            return;
        }

        ausenciaSeleccionadaId = ausenciaId;
        limpiarErrorElemento(eliminarAusenciaError);
        abrirModal(modalEliminarAusencia);
    }


    function cerrarModalEliminarAusencia() {
        cerrarModal(modalEliminarAusencia);
        ausenciaSeleccionadaId = null;
        limpiarErrorElemento(eliminarAusenciaError);
    }


    async function eliminarAusenciaEmpleado() {
        if (!ausenciaSeleccionadaId) {
            mostrarErrorElemento(
                eliminarAusenciaError,
                "No fue posible identificar la ausencia."
            );
            return;
        }

        activarLoading(btnConfirmarEliminarAusencia, "Eliminando...");

        try {
            const response = await fetch(
                `/api/empleados/${encodeURIComponent(empleadoId)}/ausencias/${encodeURIComponent(ausenciaSeleccionadaId)}`,
                {
                    method: "DELETE"
                }
            );

            if (!response.ok) {
                throw new Error(
                    await obtenerMensajeError(
                        response,
                        "No fue posible eliminar la ausencia."
                    )
                );
            }

            empleadoActual = await response.json();

            cerrarModalEliminarAusencia();
            pintarEmpleado(empleadoActual);

            mostrarToast(
                "success",
                "Ausencia eliminada",
                `La ausencia de ${empleadoActual.nombre} fue eliminada correctamente.`
            );

        } catch (error) {
            mostrarErrorElemento(
                eliminarAusenciaError,
                error.message || "No fue posible eliminar la ausencia."
            );

        } finally {
            restaurarBoton(
                btnConfirmarEliminarAusencia,
                '<i class="bi bi-trash"></i> Eliminar ausencia'
            );
        }
    }


    // =========================================================
    // HELPERS DE MODALES / ERRORES / FETCH
    // =========================================================

    function abrirModal(modal) {
        modal?.classList.add("show");
        document.body.classList.add("modal-open");
    }


    function cerrarModal(modal) {
        modal?.classList.remove("show");

        const algunModalAbierto = document.querySelector(".sgio-modal.show");

        if (!algunModalAbierto) {
            document.body.classList.remove("modal-open");
        }
    }


    function mostrarErrorElemento(elemento, mensaje) {
        if (!elemento) {
            return;
        }

        elemento.textContent = mensaje;
        elemento.classList.add("show");
    }


    function limpiarErrorElemento(elemento) {
        if (!elemento) {
            return;
        }

        elemento.textContent = "";
        elemento.classList.remove("show");
    }


    function limpiarErrorEditarEmpleado() {
        limpiarErrorElemento(editarEmpleadoError);
    }


    function activarLoading(boton, texto) {
        if (!boton) {
            return;
        }

        boton.disabled = true;
        boton.innerHTML = `
            <span class="spinner-small"></span>
            ${escapeHtml(texto)}
        `;
    }


    function restaurarBoton(boton, html) {
        if (!boton) {
            return;
        }

        boton.disabled = false;
        boton.innerHTML = html;
    }


    async function obtenerMensajeError(response, mensajeDefault) {
        try {
            const body = await response.json();

            return body.mensaje
                || body.message
                || body.error
                || mensajeDefault;

        } catch {
            return mensajeDefault;
        }
    }


    // =========================================================
    // HELPERS DE PRESENTACIÓN
    // =========================================================

    function setTexto(id, valor) {
        const elemento = document.getElementById(id);

        if (elemento) {
            elemento.textContent = valor;
        }
    }


    function formatearTelegram(username) {
        if (!username) {
            return "Sin Telegram";
        }

        return username.startsWith("@")
            ? username
            : `@${username}`;
    }


    function formatearFecha(fecha) {
        if (!fecha) {
            return "Sin asignaciones";
        }

        const valor = new Date(fecha);

        if (Number.isNaN(valor.getTime())) {
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


    function formatearFechaSimple(fecha) {
        if (!fecha) {
            return "-";
        }

        const valor = new Date(`${fecha}T00:00:00`);

        if (Number.isNaN(valor.getTime())) {
            return "-";
        }

        return new Intl.DateTimeFormat(
            "es-MX",
            {
                day: "2-digit",
                month: "2-digit",
                year: "numeric"
            }
        ).format(valor);
    }


    function mostrarError(mensaje) {
        const contenido = document.getElementById("empleadoDetalleContenido");

        if (!contenido) {
            return;
        }

        contenido.innerHTML = `
            <div class="employee-detail-error">
                <i class="bi bi-exclamation-circle"></i>
                ${escapeHtml(mensaje)}
            </div>
        `;
    }


    function escapeHtml(valor) {
        return String(valor ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }


    // =========================================================
    // LISTENERS
    // =========================================================

    btnEditarEmpleado?.addEventListener("click", abrirModalEditarEmpleado);
    btnCerrarEditarEmpleado?.addEventListener("click", cerrarModalEditarEmpleado);
    btnCancelarEditarEmpleado?.addEventListener("click", cerrarModalEditarEmpleado);
    btnGuardarEditarEmpleado?.addEventListener("click", guardarCambiosEmpleado);

    modalEditarEmpleado
        ?.querySelector(".sgio-modal-backdrop")
        ?.addEventListener("click", cerrarModalEditarEmpleado);


    btnCambiarEstadoEmpleado?.addEventListener("click", abrirModalEstadoEmpleado);
    btnCerrarEstadoEmpleado?.addEventListener("click", cerrarModalEstadoEmpleado);
    btnCancelarEstadoEmpleado?.addEventListener("click", cerrarModalEstadoEmpleado);
    btnConfirmarEstadoEmpleado?.addEventListener("click", confirmarCambioEstadoEmpleado);

    modalEstadoEmpleado
        ?.querySelector(".sgio-modal-backdrop")
        ?.addEventListener("click", cerrarModalEstadoEmpleado);


    btnRegistrarAusencia?.addEventListener("click", abrirModalRegistrarAusencia);
    btnCerrarRegistrarAusencia?.addEventListener("click", cerrarModalRegistrarAusencia);
    btnCancelarRegistrarAusencia?.addEventListener("click", cerrarModalRegistrarAusencia);
    btnGuardarRegistrarAusencia?.addEventListener("click", guardarAusenciaEmpleado);

    modalRegistrarAusencia
        ?.querySelector(".sgio-modal-backdrop")
        ?.addEventListener("click", cerrarModalRegistrarAusencia);


    btnCerrarEliminarAusencia?.addEventListener("click", cerrarModalEliminarAusencia);
    btnCancelarEliminarAusencia?.addEventListener("click", cerrarModalEliminarAusencia);
    btnConfirmarEliminarAusencia?.addEventListener("click", eliminarAusenciaEmpleado);

    modalEliminarAusencia
        ?.querySelector(".sgio-modal-backdrop")
        ?.addEventListener("click", cerrarModalEliminarAusencia);


    document.addEventListener("keydown", event => {
        if (event.key !== "Escape") {
            return;
        }

        if (modalEliminarAusencia?.classList.contains("show")) {
            cerrarModalEliminarAusencia();
            return;
        }

        if (modalRegistrarAusencia?.classList.contains("show")) {
            cerrarModalRegistrarAusencia();
            return;
        }

        if (modalEstadoEmpleado?.classList.contains("show")) {
            cerrarModalEstadoEmpleado();
            return;
        }

        if (modalEditarEmpleado?.classList.contains("show")) {
            cerrarModalEditarEmpleado();
        }
    });


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    cargarEmpleado();
});
