(() => {
    "use strict";

    const originalFetch = window.fetch.bind(window);
    let sesionExpirada = false;

    function mostrarModalSesionExpirada() {

        const modalHTML = `
            <div class="modal fade"
                 id="modalSesionExpirada"
                 tabindex="-1"
                 data-bs-backdrop="static"
                 data-bs-keyboard="false"
                 aria-labelledby="tituloSesionExpirada"
                 aria-hidden="true">

                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">

                        <div class="modal-header">
                            <h5 class="modal-title"
                                id="tituloSesionExpirada">
                                <i class="bi bi-shield-lock me-2"></i>
                                Sesión expirada
                            </h5>
                        </div>

                        <div class="modal-body">
                            <p class="mb-0">
                                Tu sesión ha finalizado por seguridad.
                                Para continuar utilizando SGIO,
                                inicia sesión nuevamente.
                            </p>
                        </div>

                        <div class="modal-footer">
                            <button type="button"
                                    class="btn btn-primary"
                                    id="btnIniciarSesion">
                                <i class="bi bi-box-arrow-in-right me-2"></i>
                                Iniciar sesión
                            </button>
                        </div>

                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML("beforeend", modalHTML);

        document.getElementById("btnIniciarSesion")
            .addEventListener("click", () => {
                window.location.replace("/login?expired");
            });

        const modalElement =
            document.getElementById("modalSesionExpirada");

        const modal = new bootstrap.Modal(modalElement);
        modal.show();
    }

    window.fetch = async function (...args) {

        const response = await originalFetch(...args);

        if (response.status === 401 && !sesionExpirada) {

            sesionExpirada = true;

            mostrarModalSesionExpirada();
        }

        return response;
    };

})();