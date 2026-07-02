import requests
from config import API_BASE_URL


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

    response.raise_for_status()
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

    response.raise_for_status()
    return response.json()