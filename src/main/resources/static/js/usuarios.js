document.addEventListener("DOMContentLoaded", () => {

    const modal =
        document.getElementById("modalNuevoUsuario");

    const btnNuevo =
        document.getElementById("btnNuevoUsuario");

    const btnCerrar =
        document.getElementById("btnCerrarNuevoUsuario");

    const btnCancelar =
        document.getElementById("btnCancelarNuevoUsuario");

    const backdrop =
        modal?.querySelector(".sgio-modal-backdrop");
		
	const btnGuardar =
	    document.getElementById("btnGuardarNuevoUsuario");

	const errorBox =
	    document.getElementById("nuevoUsuarioError");

	const csrfToken = document
	    .querySelector('meta[name="_csrf"]')
	    ?.getAttribute("content");

	const csrfHeader = document
	    .querySelector('meta[name="_csrf_header"]')
	    ?.getAttribute("content");
		
	const modalEditar =
	    document.getElementById("modalEditarUsuario");

	const btnCerrarEditar =
	    document.getElementById("btnCerrarEditarUsuario");

	const btnCancelarEditar =
	    document.getElementById("btnCancelarEditarUsuario");

	const botonesEditar =
	    document.querySelectorAll(".user-edit-button");

	const backdropEditar =
	    modalEditar?.querySelector(".sgio-modal-backdrop");
		
	const btnGuardarEditar =
	    document.getElementById(
	        "btnGuardarEditarUsuario"
	    );
		
	const botonesEstado =
	    document.querySelectorAll(
	        ".user-status-button"
	    );
		
	const modalPassword =
	    document.getElementById(
	        "modalPasswordUsuario"
	    );

	const botonesPassword =
	    document.querySelectorAll(
	        ".user-password-button"
	    );

	const btnCerrarPassword =
	    document.getElementById(
	        "btnCerrarPasswordUsuario"
	    );

	const btnCancelarPassword =
	    document.getElementById(
	        "btnCancelarPasswordUsuario"
	    );

	const backdropPassword =
	    modalPassword?.querySelector(
	        ".sgio-modal-backdrop"
	    );
		
	const btnGuardarPassword =
	    document.getElementById(
	        "btnGuardarPasswordUsuario"
	    );


    // =========================================================
    // ABRIR MODAL
    // =========================================================

    function abrirModal() {

        if (!modal) {
            return;
        }

        modal.classList.add("show");

        document.body.classList.add("modal-open");

        document
            .getElementById("nuevoNombre")
            ?.focus();
    }


    // =========================================================
    // CERRAR MODAL
    // =========================================================

    function cerrarModal() {

        if (!modal) {
            return;
        }

        modal.classList.remove("show");

        document.body.classList.remove("modal-open");
    }
	
	
	function mostrarError(mensaje) {

	    if (!errorBox) {
	        return;
	    }

	    errorBox.textContent = mensaje;
	    errorBox.classList.add("show");
	}


	function limpiarError() {

	    if (!errorBox) {
	        return;
	    }

	    errorBox.textContent = "";
	    errorBox.classList.remove("show");
	}


	function limpiarFormulario() {

	    document.getElementById("nuevoNombre").value = "";
	    document.getElementById("nuevoUsername").value = "";
	    document.getElementById("nuevoPassword").value = "";
	    document.getElementById("confirmarPassword").value = "";
	    document.getElementById("nuevoRol").value = "";

	    limpiarError();
	}
	
	
	async function guardarUsuario() {

	    limpiarError();

	    const nombre =
	        document.getElementById("nuevoNombre")
	            .value.trim();

	    const username =
	        document.getElementById("nuevoUsername")
	            .value.trim();

	    const password =
	        document.getElementById("nuevoPassword")
	            .value;

	    const confirmarPassword =
	        document.getElementById("confirmarPassword")
	            .value;

	    const rol =
	        document.getElementById("nuevoRol")
	            .value;
				

	    // =====================================================
	    // VALIDACIONES
	    // =====================================================

	    if (!nombre) {
	        mostrarError(
	            "El nombre es obligatorio."
	        );
	        return;
	    }

	    if (!username) {
	        mostrarError(
	            "El usuario es obligatorio."
	        );
	        return;
	    }

	    if (!password) {
	        mostrarError(
	            "La contraseña es obligatoria."
	        );
	        return;
	    }

	    if (password.length < 8) {
	        mostrarError(
	            "La contraseña debe tener al menos 8 caracteres."
	        );
	        return;
	    }

	    if (password !== confirmarPassword) {
	        mostrarError(
	            "Las contraseñas no coinciden."
	        );
	        return;
	    }

	    if (!rol) {
	        mostrarError(
	            "Selecciona un rol."
	        );
	        return;
	    }


	    // =====================================================
	    // REQUEST
	    // =====================================================

	    const request = {
	        nombre: nombre,
	        username: username,
	        password: password,
	        rol: rol
	    };


	    try {

	        btnGuardar.disabled = true;

	        btnGuardar.innerHTML =
	            '<span class="spinner-small"></span> Creando...';


	        const response = await fetch(
	            "/api/usuarios",
	            {
	                method: "POST",

	                headers: {
	                    "Content-Type":
	                        "application/json",

	                    [csrfHeader]:
	                        csrfToken
	                },

	                body:
	                    JSON.stringify(request)
	            }
	        );


	        const data = await response.json();


	        if (!response.ok) {

	            mostrarError(
	                data.mensaje ||
	                "No fue posible crear el usuario."
	            );

	            return;
	        }


	        limpiarFormulario();

	        cerrarModal();


	        // Recargamos para mostrar
	        // el nuevo usuario y actualizar métricas.

	        window.location.reload();


	    } catch (error) {

	        console.error(
	            "Error al crear usuario:",
	            error
	        );

	        mostrarError(
	            "Ocurrió un error al comunicarse con el servidor."
	        );

	    } finally {

	        btnGuardar.disabled = false;

	        btnGuardar.innerHTML =
	            '<i class="bi bi-person-plus"></i> Crear usuario';
	    }
	}
	
	function abrirModalEditar(boton) {

					    if (!modalEditar) {
					        return;
					    }

					    const id =
					        boton.dataset.id;

					    const nombre =
					        boton.dataset.nombre;

					    const username =
					        boton.dataset.username;

					    const rol =
					        boton.dataset.rol;


					    document.getElementById(
					        "editarUsuarioId"
					    ).value = id;

					    document.getElementById(
					        "editarNombre"
					    ).value = nombre;

					    document.getElementById(
					        "editarUsername"
					    ).value = username;

					    document.getElementById(
					        "editarRol"
					    ).value = rol;


					    const errorBoxEditar =
					        document.getElementById(
					            "editarUsuarioError"
					        );

					    if (errorBoxEditar) {

					        errorBoxEditar.textContent = "";

					        errorBoxEditar.classList.remove(
					            "show"
					        );
					    }


					    modalEditar.classList.add("show");

					    document.body.classList.add(
					        "modal-open"
					    );

					    document.getElementById(
					        "editarNombre"
					    )?.focus();
					}


					function cerrarModalEditar() {

					    if (!modalEditar) {
					        return;
					    }

					    modalEditar.classList.remove("show");

					    document.body.classList.remove(
					        "modal-open"
					    );
					}
					
					async function guardarEdicionUsuario() {

					    const id =
					        document.getElementById(
					            "editarUsuarioId"
					        ).value;

					    const nombre =
					        document.getElementById(
					            "editarNombre"
					        ).value.trim();

					    const rol =
					        document.getElementById(
					            "editarRol"
					        ).value;

					    const errorBox =
					        document.getElementById(
					            "editarUsuarioError"
					        );


					    // =====================================================
					    // VALIDACIONES
					    // =====================================================

					    if (!nombre) {

					        errorBox.textContent =
					            "El nombre es obligatorio.";

					        errorBox.classList.add("show");

					        return;
					    }

					    if (!rol) {

					        errorBox.textContent =
					            "El rol es obligatorio.";

					        errorBox.classList.add("show");

					        return;
					    }


					    // =====================================================
					    // REQUEST
					    // =====================================================

					    const request = {
					        nombre: nombre,
					        rol: rol
					    };


					    try {

					        btnGuardarEditar.disabled = true;

					        btnGuardarEditar.innerHTML = `
					            <span class="button-spinner"></span>
					            Guardando...
					        `;


					        const response = await fetch(
					            `/api/usuarios/${id}`,
					            {
					                method: "PUT",

					                headers: {
					                    "Content-Type":
					                        "application/json",

					                    [csrfHeader]:
					                        csrfToken
					                },

					                body: JSON.stringify(
					                    request
					                )
					            }
					        );


					        const data =
					            await response.json();


					        if (!response.ok) {

					            errorBox.textContent =
					                data.mensaje ||
					                "No fue posible actualizar el usuario.";

					            errorBox.classList.add("show");

					            return;
					        }


					        // =================================================
					        // ACTUALIZACIÓN CORRECTA
					        // =================================================

					        cerrarModalEditar();

					        window.location.reload();


					    } catch (error) {

					        console.error(
					            "Error al actualizar usuario:",
					            error
					        );

					        errorBox.textContent =
					            "Ocurrió un error al actualizar el usuario.";

					        errorBox.classList.add("show");


					    } finally {

					        btnGuardarEditar.disabled = false;

					        btnGuardarEditar.innerHTML = `
					            <i class="bi bi-check-lg"></i>
					            Guardar cambios
					        `;
					    }
					}
					
					async function cambiarEstadoUsuario(boton) {

					    const id =
					        boton.dataset.id;

					    const nombre =
					        boton.dataset.nombre;

					    const activoActual =
					        boton.dataset.activo === "true";

					    const nuevoEstado =
					        !activoActual;


					    const accion =
					        nuevoEstado
					            ? "activar"
					            : "desactivar";


					    const confirmado =
					        window.confirm(
					            `¿Deseas ${accion} al usuario "${nombre}"?`
					        );

					    if (!confirmado) {
					        return;
					    }


					    try {

					        boton.disabled = true;


					        const response = await fetch(
					            `/api/usuarios/${id}/estado?activo=${nuevoEstado}`,
					            {
					                method: "PATCH",

					                headers: {
					                    [csrfHeader]:
					                        csrfToken
					                }
					            }
					        );


					        const data =
					            await response.json();


					        if (!response.ok) {

					            alert(
					                data.mensaje ||
					                "No fue posible cambiar el estado del usuario."
					            );

					            return;
					        }


					        window.location.reload();


					    } catch (error) {

					        console.error(
					            "Error al cambiar estado del usuario:",
					            error
					        );

					        alert(
					            "Ocurrió un error al cambiar el estado del usuario."
					        );


					    } finally {

					        boton.disabled = false;
					    }
					}
					
					function abrirModalPassword(boton) {

					    if (!modalPassword) {
					        return;
					    }

					    const id =
					        boton.dataset.id;

					    const nombre =
					        boton.dataset.nombre;

					    const username =
					        boton.dataset.username;


					    document.getElementById(
					        "passwordUsuarioId"
					    ).value = id;

					    document.getElementById(
					        "passwordUsuarioNombre"
					    ).textContent = nombre;

					    document.getElementById(
					        "passwordUsuarioUsername"
					    ).textContent = username;


					    document.getElementById(
					        "nuevaPassword"
					    ).value = "";

					    document.getElementById(
					        "confirmarNuevaPassword"
					    ).value = "";


					    const errorBox =
					        document.getElementById(
					            "passwordUsuarioError"
					        );

					    if (errorBox) {
					        errorBox.textContent = "";
					        errorBox.classList.remove("show");
					    }


					    modalPassword.classList.add("show");

					    document.body.classList.add(
					        "modal-open"
					    );


					    document.getElementById(
					        "nuevaPassword"
					    )?.focus();
					}


					function cerrarModalPassword() {

					    if (!modalPassword) {
					        return;
					    }

					    modalPassword.classList.remove("show");

					    document.body.classList.remove(
					        "modal-open"
					    );
					}
					
					async function restablecerPasswordUsuario() {

					    const id =
					        document.getElementById(
					            "passwordUsuarioId"
					        ).value;

					    const password =
					        document.getElementById(
					            "nuevaPassword"
					        ).value;

					    const confirmarPassword =
					        document.getElementById(
					            "confirmarNuevaPassword"
					        ).value;

					    const errorBox =
					        document.getElementById(
					            "passwordUsuarioError"
					        );


					    // =====================================================
					    // LIMPIAR ERROR ANTERIOR
					    // =====================================================

					    errorBox.textContent = "";
					    errorBox.classList.remove("show");


					    // =====================================================
					    // VALIDACIONES
					    // =====================================================

					    if (!password) {

					        errorBox.textContent =
					            "La nueva contraseña es obligatoria.";

					        errorBox.classList.add("show");

					        return;
					    }


					    if (password.length < 8) {

					        errorBox.textContent =
					            "La contraseña debe tener al menos 8 caracteres.";

					        errorBox.classList.add("show");

					        return;
					    }


					    if (password !== confirmarPassword) {

					        errorBox.textContent =
					            "Las contraseñas no coinciden.";

					        errorBox.classList.add("show");

					        return;
					    }


					    // =====================================================
					    // REQUEST
					    // =====================================================

					    const request = {
					        password: password
					    };


					    try {

					        btnGuardarPassword.disabled = true;

					        btnGuardarPassword.innerHTML = `
					            <span class="button-spinner"></span>
					            Restableciendo...
					        `;


					        const response = await fetch(
					            `/api/usuarios/${id}/password`,
					            {
					                method: "PATCH",

					                headers: {
					                    "Content-Type":
					                        "application/json",

					                    [csrfHeader]:
					                        csrfToken
					                },

					                body: JSON.stringify(
					                    request
					                )
					            }
					        );


					        const data =
					            await response.json();


					        if (!response.ok) {

					            errorBox.textContent =
					                data.mensaje ||
					                "No fue posible restablecer la contraseña.";

					            errorBox.classList.add("show");

					            return;
					        }


					        cerrarModalPassword();

					        alert(
					            "Contraseña restablecida correctamente."
					        );


					    } catch (error) {

					        console.error(
					            "Error al restablecer contraseña:",
					            error
					        );

					        errorBox.textContent =
					            "Ocurrió un error al restablecer la contraseña.";

					        errorBox.classList.add("show");


					    } finally {

					        btnGuardarPassword.disabled = false;

					        btnGuardarPassword.innerHTML = `
					            <i class="bi bi-key"></i>
					            Restablecer contraseña
					        `;
					    }
					}


    // =========================================================
    // EVENTOS
    // =========================================================

    btnNuevo?.addEventListener(
        "click",
        abrirModal
    );

    btnCerrar?.addEventListener(
        "click",
        cerrarModal
    );

    btnCancelar?.addEventListener(
        "click",
        cerrarModal
    );

    backdrop?.addEventListener(
        "click",
        cerrarModal
    );


    // Cerrar con ESC

    document.addEventListener(
        "keydown",
        (event) => {

            if (
                event.key === "Escape" &&
                modal?.classList.contains("show")
            ) {
                cerrarModal();
            }
			
			if (
			    event.key === "Escape" &&
			    modalEditar?.classList.contains("show")
			) {
			    cerrarModalEditar();
			}
			
			if (
			    event.key === "Escape" &&
			    modalPassword?.classList.contains("show")
			) {
			    cerrarModalPassword();
			}
        }
    );
	
	btnGuardar?.addEventListener(
	    "click",
	    guardarUsuario
	);
	
	botonesEditar.forEach((boton) => {

	    boton.addEventListener(
	        "click",
	        () => abrirModalEditar(boton)
	    );

	});


	btnCerrarEditar?.addEventListener(
	    "click",
	    cerrarModalEditar
	);


	btnCancelarEditar?.addEventListener(
	    "click",
	    cerrarModalEditar
	);


	backdropEditar?.addEventListener(
	    "click",
	    cerrarModalEditar
	);
	
	btnGuardarEditar?.addEventListener(
	    "click",
	    guardarEdicionUsuario
	);
	
	botonesEstado.forEach((boton) => {

	    boton.addEventListener(
	        "click",
	        () => cambiarEstadoUsuario(boton)
	    );
	});
	
	botonesPassword.forEach((boton) => {

	    boton.addEventListener(
	        "click",
	        () => abrirModalPassword(boton)
	    );
	});


	btnCerrarPassword?.addEventListener(
	    "click",
	    cerrarModalPassword
	);


	btnCancelarPassword?.addEventListener(
	    "click",
	    cerrarModalPassword
	);


	backdropPassword?.addEventListener(
	    "click",
	    cerrarModalPassword
	);
	
	btnGuardarPassword?.addEventListener(
	    "click",
	    restablecerPasswordUsuario
	);

});