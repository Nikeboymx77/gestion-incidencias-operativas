(() => {

    const STORAGE_KEY = "sgio-theme";

    const mediaQuery =
        window.matchMedia("(prefers-color-scheme: dark)");


    function obtenerPreferencia() {

        const guardada =
            localStorage.getItem(STORAGE_KEY);

        if (
            guardada === "light" ||
            guardada === "dark" ||
            guardada === "system"
        ) {
            return guardada;
        }

        return "system";
    }


    function resolverTema(preferencia) {

        if (preferencia === "system") {
            return mediaQuery.matches
                ? "dark"
                : "light";
        }

        return preferencia;
    }


	function aplicarTema(preferencia) {

	    const temaReal =
	        resolverTema(preferencia);

	    document.documentElement
	        .setAttribute(
	            "data-theme",
	            temaReal
	        );

	    document.documentElement
	        .setAttribute(
	            "data-theme-preference",
	            preferencia
	        );

	    actualizarBotones(preferencia);

	    window.dispatchEvent(
	        new CustomEvent(
	            "sgio-theme-changed",
	            {
	                detail: {
	                    preference: preferencia,
	                    theme: temaReal
	                }
	            }
	        )
	    );
	}


    function actualizarBotones(preferencia) {

        const botones =
            document.querySelectorAll(
                "[data-theme-option]"
            );

        botones.forEach(boton => {

            const activo =
                boton.dataset.themeOption ===
                preferencia;

            boton.classList.toggle(
                "active",
                activo
            );

            boton.setAttribute(
                "aria-pressed",
                activo
                    ? "true"
                    : "false"
            );
        });
    }


    function cambiarTema(preferencia) {

        localStorage.setItem(
            STORAGE_KEY,
            preferencia
        );

        aplicarTema(preferencia);
    }


    function inicializarSelector() {

        const botones =
            document.querySelectorAll(
                "[data-theme-option]"
            );

        botones.forEach(boton => {

            boton.addEventListener(
                "click",
                () => {

                    cambiarTema(
                        boton.dataset.themeOption
                    );
                }
            );
        });

        aplicarTema(
            obtenerPreferencia()
        );
    }


    mediaQuery.addEventListener(
        "change",
        () => {

            const preferencia =
                obtenerPreferencia();

            if (preferencia === "system") {
                aplicarTema("system");
            }
        }
    );


    /*
     * Aplicamos el tema inmediatamente.
     * El selector se inicializa cuando
     * el DOM esté disponible.
     */

    aplicarTema(
        obtenerPreferencia()
    );


    if (
        document.readyState === "loading"
    ) {

        document.addEventListener(
            "DOMContentLoaded",
            inicializarSelector
        );

    } else {

        inicializarSelector();
    }

})();