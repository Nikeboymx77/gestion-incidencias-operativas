import requests
from config import API_BASE_URL
from urllib.parse import quote

def manejar_error_response(response):
    try:
        error = response.json()
        mensaje = error.get("mensaje", "Error desconocido")
    except Exception:
        mensaje = response.text or "Error desconocido"

    raise Exception(mensaje)

def obtener_pendientes():
    response = requests.get(f"{API_BASE_URL}/incidencias/pendientes", timeout=10)
    response.raise_for_status()
    return response.json()


def obtener_incidencia(folio):
    response = requests.get(f"{API_BASE_URL}/incidencias/{folio}", timeout=10)
    response.raise_for_status()
    return response.json()


def resolver_incidencia(folio, usuario, comentario):
    payload = {
        "usuario": usuario,
        "comentario": comentario
    }

    response = requests.put(
        f"{API_BASE_URL}/incidencias/{folio}/resolver",
        json=payload,
        timeout=10
    )

    if not response.ok:
        manejar_error_response(response)

    return response.json()

def tomar_incidencia(folio, usuario, comentario):
    payload = {
        "usuario": usuario,
        "comentario": comentario
    }

    response = requests.put(
        f"{API_BASE_URL}/incidencias/{folio}/tomar",
        json=payload,
        timeout=10
    )

    if not response.ok:
        manejar_error_response(response)

    return response.json()

def obtener_pendientes_empleado(username: str):
    response = requests.get(
        f"{API_BASE_URL}/incidencias/"
        f"empleado/{username}/pendientes",
        timeout=15
    )

    response.raise_for_status()

    return response.json()




def obtener_pendientes_por_empleado(nombre_empleado: str):
    nombre = quote(nombre_empleado)

    response = requests.get(
        f"{API_BASE_URL}/incidencias/"
        f"empleado/nombre/{nombre}/pendientes",
        timeout=10
    )

    if not response.ok:
        manejar_error_response(response)

    return response.json()

def obtener_resumen_empleados():
    url = f"{API_BASE_URL}/empleados/resumen"

    response = requests.get(url, timeout=30)
    response.raise_for_status()

    return response.json()

def obtener_detalle_empleado(username):
    url = f"{API_BASE_URL}/empleados/{username}"

    response = requests.get(url, timeout=30)
    response.raise_for_status()

    return response.json()