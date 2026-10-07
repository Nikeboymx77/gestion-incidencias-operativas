import requests
from config import API_BASE_URL, BOT_API_KEY
from urllib.parse import quote

def manejar_error_response(response):
    try:
        error = response.json()
        mensaje = error.get("mensaje", "Error desconocido")
    except Exception:
        mensaje = response.text or "Error desconocido"

    raise Exception(mensaje)

def obtener_pendientes():
    url = f"{API_BASE_URL}/bot/incidencias/pendientes"

    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    response = requests.get(
        url,
        headers=headers,
        timeout=10
    )

    response.raise_for_status()

    return response.json()


def obtener_incidencia(folio):
    url = f"{API_BASE_URL}/bot/incidencias/{folio}"

    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    response = requests.get(
        url,
        headers=headers,
        timeout=10
    )
    return response.json()


def resolver_incidencia(folio, usuario, comentario):
    payload = {
        "usuario": usuario,
        "comentario": comentario
    }
    
    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    response = requests.put(
        f"{API_BASE_URL}/bot/incidencias/{folio}/resolver",
        headers=headers,
        json=payload,
        timeout=10
    )

    if not response.ok:
        manejar_error_response(response)

    return response.json()

def cancelar_incidencia(
        folio: str,
        usuario: str,
        comentario: str
) -> dict:

    payload = {
        "usuario": usuario,
        "comentario": comentario
    }
    
    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    response = requests.put(
        f"{API_BASE_URL}/bot/incidencias/{folio}/cancelar",
        headers=headers,
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
    
    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    response = requests.put(
        f"{API_BASE_URL}/bot/incidencias/{folio}/tomar",
        headers=headers,
        json=payload,
        timeout=10
    )

    if not response.ok:
        manejar_error_response(response)

    return response.json()

def obtener_pendientes_empleado(username: str):

    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    response = requests.get(
        f"{API_BASE_URL}/bot/incidencias/"
        f"empleado/{username}/pendientes",
        headers=headers,
        timeout=15
    )

    response.raise_for_status()

    return response.json()




def obtener_pendientes_por_empleado(nombre_empleado: str):
    nombre = quote(nombre_empleado)
    
    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    response = requests.get(
        f"{API_BASE_URL}/bot/incidencias/"
        f"empleado/nombre/{nombre}/pendientes",
        headers=headers,
        timeout=10
    )

    if not response.ok:
        manejar_error_response(response)

    return response.json()

def obtener_resumen_empleados():
    url = f"{API_BASE_URL}/bot/empleados/resumen"
    
    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    response = requests.get(url,headers=headers, timeout=30)
    response.raise_for_status()

    return response.json()

def obtener_detalle_empleado(username):
    url = f"{API_BASE_URL}/bot/empleados/{username}"
    
    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    response = requests.get(url, headers=headers, timeout=30)
    response.raise_for_status()

    return response.json()

def obtener_estadisticas():
    url = f"{API_BASE_URL}/bot/incidencias/estadisticas"
    
    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    try:
        response = requests.get(url,headers=headers, timeout=30)
        response.raise_for_status()
        return response.json()

    except requests.RequestException as error:
        print(f"Error al consultar estadísticas: {error}")
        return None
    
def obtener_ranking():
    url = f"{API_BASE_URL}/bot/incidencias/ranking"
    
    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    try:
        response = requests.get(url,headers=headers, timeout=30)
        response.raise_for_status()
        return response.json()

    except requests.RequestException as error:
        print(f"Error al consultar el ranking: {error}")
        return None
    
def obtener_incidencias_atrasadas(dias=3):
    url = f"{API_BASE_URL}/bot/incidencias/atrasadas"
    
    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    try:
        response = requests.get(
            url,
            headers=headers,
            params={"dias": dias},
            timeout=30
        )
        response.raise_for_status()
        return response.json()

    except requests.RequestException as error:
        print(f"Error al consultar incidencias atrasadas: {error}")
        return None
    
def reasignar_incidencia(
        folio: str,
        username_telegram: str,
        usuario: str,
        comentario: str
) -> dict:

    headers = {
        "X-SGIO-API-KEY": BOT_API_KEY
    }

    payload = {
        "usernameTelegram": username_telegram,
        "usuario": usuario,
        "comentario": comentario
    }

    response = requests.put(
        f"{API_BASE_URL}/bot/incidencias/{folio}/reasignar",
        headers=headers,
        json=payload,
        timeout=10
    )

    if not response.ok:
        manejar_error_response(response)

    return response.json()