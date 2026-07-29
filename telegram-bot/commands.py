from telegram import Update
from telegram.ext import ContextTypes
from api import (obtener_pendientes, obtener_incidencia, resolver_incidencia, tomar_incidencia, 
                 obtener_pendientes_empleado,obtener_pendientes_por_empleado,obtener_resumen_empleados,
                 obtener_detalle_empleado,obtener_estadisticas,obtener_ranking)



async def help_command(
        update: Update,
        context: ContextTypes.DEFAULT_TYPE
) -> None:

    mensaje = (
        "🤖 Bot de Gestión de Incidencias\n\n"
        "Comandos disponibles:\n\n"
        "/pendientes\n"
        "Consulta todas las incidencias pendientes.\n\n"
        "/pendientes nombre del empleado\n"
        "Consulta las incidencias pendientes de un empleado.\n"
        "Ejemplo: /pendientes cesar chavez\n\n"
        "/estado INC-1001\n"
        "Consulta el estado de una incidencia.\n\n"
        "/tomar INC-1001\n"
        "Toma una incidencia pendiente y la marca como EN_PROCESO.\n\n"
        "/resuelto INC-1001 comentario\n"
        "Marca una incidencia como resuelta.\n\n"
        "/mis_pendientes\n"
        "Consulta las incidencias EN_PROCESO o REABIERTAS asignadas a tu usuario.\n\n"
        "/empleados - Muestra la carga operativa del equipo.\n\n"
        "/empleado usuarioTelegram - Muestra el detalle de un integrante.\n\n"
        "/estadisticas - Muestra el resumen general de SGIO.\n\n"
        "/ranking - Muestra el ranking operativo del equipo."
    )

    await update.message.reply_text(mensaje)


async def pendientes_command(
        update: Update,
        context: ContextTypes.DEFAULT_TYPE
) -> None:

    try:
        if context.args:
            nombre_empleado = " ".join(context.args).strip()

            incidencias = obtener_pendientes_por_empleado(
                nombre_empleado
            )

            mensaje_sin_resultados = (
                f"✅ No hay incidencias PENDIENTES "
                f"asignadas a {nombre_empleado}."
            )

            encabezado = (
                f"📋 Incidencias PENDIENTES de "
                f"{nombre_empleado}\n\n"
            )

        else:
            incidencias = obtener_pendientes()

            mensaje_sin_resultados = (
                "✅ No hay incidencias pendientes."
            )

            encabezado = (
                "📋 Todas las incidencias PENDIENTES\n\n"
            )

        if not incidencias:
            await update.effective_message.reply_text(
                mensaje_sin_resultados
            )
            return

        mensajes = []

        for incidencia in incidencias:
            empleado = incidencia.get("empleadoAsignado") or {}

            mensajes.append(
                f"🚨 {incidencia.get('folio', 'Sin folio')}\n"
                f"📌 Asunto: "
                f"{incidencia.get('asunto') or 'Sin asunto'}\n"
                f"⚠ Prioridad: "
                f"{incidencia.get('prioridad') or 'Sin prioridad'}\n"
                f"👤 Asignado: "
                f"{empleado.get('nombre', 'Sin asignar')}\n"
                f"📂 Carpeta: "
                f"{incidencia.get('carpetaOrigen') or 'Sin carpeta'}\n"
                f"📍 Estado: "
                f"{incidencia.get('estado') or 'Sin estado'}"
            )

        await update.effective_message.reply_text(
            encabezado + "\n\n".join(mensajes)
        )

    except Exception as error:
        await update.effective_message.reply_text(
            f"❌ Error consultando pendientes: {error}"
        )


async def estado_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    try:
        if not context.args:
            await update.message.reply_text("Uso: /estado INC-1001")
            return

        folio = context.args[0]
        inc = obtener_incidencia(folio)
        empleado = inc.get("empleadoAsignado") or {}

        mensaje = (
            f"📌 Incidencia {inc.get('folio')}\n"
            f"📝 Asunto: {inc.get('asunto')}\n"
            f"⚠ Prioridad: {inc.get('prioridad')}\n"
            f"📍 Estado: {inc.get('estado')}\n"
            f"👤 Asignado: {empleado.get('nombre', 'Sin asignar')}\n"
            f"📂 Carpeta: {inc.get('carpetaOrigen')}\n"
            f"🧾 Descripción: {inc.get('descripcion')}"
        )

        await update.message.reply_text(mensaje)

    except Exception as e:
        await update.message.reply_text(f"❌ Error consultando incidencia: {e}")


async def resuelto_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    try:
        if len(context.args) < 2:
            await update.message.reply_text(
                "Uso: /resuelto INC-1001 comentario de resolución"
            )
            return

        folio = context.args[0]
        comentario = " ".join(context.args[1:])

        usuario = update.effective_user.username or update.effective_user.first_name

        inc = resolver_incidencia(folio, usuario, comentario)

        await update.message.reply_text(
            f"✅ Incidencia {inc.get('folio')} marcada como RESUELTA.\n"
            f"👤 Resuelta por: {usuario}\n"
            f"📝 Comentario: {comentario}"
        )

    except Exception as e:
        await update.message.reply_text(f"❌ Error resolviendo incidencia: {e}")
        
async def chatid_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    chat_id = update.effective_chat.id
    chat_title = update.effective_chat.title or "Chat privado"

    await update.message.reply_text(
        f"🆔 Chat ID: {chat_id}\n"
        f"💬 Chat: {chat_title}"
    )

async def tomar_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    try:
        if len(context.args) < 2:
            await update.message.reply_text(
                "Uso: /tomar INC-1001 comentario"
            )
            return

        folio = context.args[0]
        comentario = " ".join(context.args[1:])
        usuario = update.effective_user.username or update.effective_user.first_name

        tomar_incidencia(folio, usuario, comentario)

        await update.message.reply_text(
            f"✅ Listo, {folio} fue marcada como EN_PROCESO."
        )

    except Exception as e:
        await update.message.reply_text(f"❌ Error tomando incidencia: {e}")
        
async def mis_pendientes_command(
        update: Update,
        context: ContextTypes.DEFAULT_TYPE
) -> None:

    try:
        usuario = update.effective_user

        if usuario is None:
            await update.effective_message.reply_text(
                "❌ No fue posible identificar al usuario."
            )
            return

        username = usuario.username

        if not username:
            await update.effective_message.reply_text(
                "⚠️ Necesitas configurar un nombre de usuario "
                "en Telegram para consultar tus incidencias."
            )
            return

        incidencias = obtener_pendientes_empleado(username)

        if not incidencias:
            await update.effective_message.reply_text(
                "✅ No tienes incidencias pendientes."
            )
            return

        mensajes = []

        for incidencia in incidencias:

            mensajes.append(
                f"🔹 {incidencia.get('folio', 'Sin folio')}\n"
                f"📍 Estado: {incidencia.get('estado', 'Sin estado')}\n"
                f"🏢 Sucursal: "
                f"{incidencia.get('sucursal') or 'No disponible'}\n"
                f"📌 Asunto: "
                f"{incidencia.get('asunto') or 'Sin asunto'}"
            )

        encabezado = (
            f"📋 Incidencias pendientes de @{username}\n\n"
        )

        await update.effective_message.reply_text(
            encabezado + "\n\n".join(mensajes)
        )

    except Exception as error:
        await update.effective_message.reply_text(
            f"❌ Error consultando tus incidencias: {error}"
        )
        
async def empleados(update: Update, context: ContextTypes.DEFAULT_TYPE):

    try:
        empleados = obtener_resumen_empleados()
        
        empleados.sort(
            key=lambda empleado: empleado.get("totalActivas", 0),
            reverse=True
        )

        if not empleados:
            await update.message.reply_text(
                "No hay empleados registrados."
            )
            return

        mensaje = "👥 Estado del Equipo\n\n"

        mensaje = "👥 Estado del Equipo\n\n"

        for indice, empleado in enumerate(empleados, start=1):

            if indice == 1:
                posicion = "🥇"
            elif indice == 2:
                posicion = "🥈"
            elif indice == 3:
                posicion = "🥉"
            else:
                posicion = "🔹"

            estado = "🟢 Activo" if empleado["activo"] else "⚪ Inactivo"

            mensaje += (
                f"{posicion} {empleado['nombre']}\n"
                f"{estado}\n"
                f"📌 Pendientes: {empleado['pendientes']}\n"
                f"🟡 En proceso: {empleado['enProceso']}\n"
                f"🔴 Reabiertas: {empleado['reabiertas']}\n"
                f"📊 Total activas: {empleado['totalActivas']}\n\n"
            )

        await update.message.reply_text(mensaje)

    except requests.RequestException as error:
        await update.message.reply_text(
            f"❌ No fue posible consultar los empleados.\n{error}"
        )
    except Exception as error:
        await update.message.reply_text(
            f"❌ Ocurrió un error inesperado.\n{error}"
        )
        
async def empleado_command(update: Update, context: ContextTypes.DEFAULT_TYPE):

    try:

        if len(context.args) != 1:
            await update.message.reply_text(
                "Uso:\n/empleado usuarioTelegram"
            )
            return

        username = context.args[0].replace("@", "")

        empleado = obtener_detalle_empleado(username)

        estado = "🟢 Activo" if empleado["activo"] else "⚪ Inactivo"

        mensaje = (
            f"👤 {empleado['nombre']}\n\n"
            f"{estado}\n\n"
            f"📧 {empleado['email']}\n"
            f"📱 @{empleado['usernameTelegram']}\n\n"
        )

        mensaje += "📅 Días laborales\n"

        for dia in empleado["diasLaborales"]:
            mensaje += f"• {dia}\n"

        mensaje += "\n🏖 Ausencias\n"

        if empleado["ausencias"]:

            for ausencia in empleado["ausencias"]:

                mensaje += (
                    f"\n{ausencia['motivo']}\n"
                    f"{ausencia['fechaInicio']} - {ausencia['fechaFin']}\n"
                )

        else:

            mensaje += "Ninguna\n"

        mensaje += (
            "\n────────────────────\n\n"
            f"📌 Pendientes: {empleado['pendientes']}\n"
            f"🟡 En proceso: {empleado['enProceso']}\n"
            f"🔴 Reabiertas: {empleado['reabiertas']}\n"
            f"📊 Total activas: {empleado['totalActivas']}\n"
        )

        await update.message.reply_text(mensaje)

    except requests.RequestException as error:

        await update.message.reply_text(
            f"❌ {error}"
        )

    except Exception as error:

        await update.message.reply_text(
            f"❌ Error:\n{error}"
        )

async def estadisticas_command(
        update: Update,
        context: ContextTypes.DEFAULT_TYPE
):

    estadisticas = obtener_estadisticas()

    if not estadisticas:
        await update.message.reply_text(
            "❌ No fue posible consultar las estadísticas de SGIO."
        )
        return

    mensaje = (
        "📊 *Estadísticas SGIO*\n\n"
        f"📌 Pendientes: {estadisticas.get('pendientes', 0)}\n"
        f"🟡 En proceso: {estadisticas.get('enProceso', 0)}\n"
        f"🔴 Reabiertas: {estadisticas.get('reabiertas', 0)}\n"
        f"✅ Resueltas hoy: {estadisticas.get('resueltasHoy', 0)}\n\n"
        f"👥 Empleados activos: {estadisticas.get('empleadosActivos', 0)}"
    )

    await update.message.reply_text(
        mensaje,
        parse_mode="Markdown"
    )

async def ranking_command(
        update: Update,
        context: ContextTypes.DEFAULT_TYPE
):

    ranking = obtener_ranking()

    if ranking is None:
        await update.message.reply_text(
            "❌ No fue posible consultar el ranking de SGIO."
        )
        return

    if not ranking:
        await update.message.reply_text(
            "📭 No hay información disponible para generar el ranking."
        )
        return

    medallas = ["🥇", "🥈", "🥉"]

    mensaje = "🏆 *Ranking SGIO*\n\n"

    for posicion, empleado in enumerate(ranking, start=1):

        indicador = (
            medallas[posicion - 1]
            if posicion <= 3
            else f"{posicion}."
        )

        mensaje += (
            f"{indicador} *{empleado.get('nombre', 'Sin nombre')}*\n"
            f"✅ Resueltas: {empleado.get('resueltas', 0)}\n"
            f"📌 Pendientes: {empleado.get('pendientes', 0)}\n"
            f"🟡 En proceso: {empleado.get('enProceso', 0)}\n"
            f"🔴 Reabiertas: {empleado.get('reabiertas', 0)}\n"
            f"📊 Activas: {empleado.get('totalActivas', 0)}\n\n"
        )

    await update.message.reply_text(
        mensaje,
        parse_mode="Markdown"
    )
        
