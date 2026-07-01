from telegram import Update
from telegram.ext import ContextTypes
from api import obtener_pendientes, obtener_incidencia, resolver_incidencia


async def help_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    mensaje = """
🤖 Bot de Gestión de Incidencias

Comandos disponibles:

/pendientes
Consulta incidencias pendientes.

/estado INC-1001
Consulta el estado de una incidencia.

/resuelto INC-1001 comentario
Marca una incidencia como resuelta.
"""
    await update.message.reply_text(mensaje)


async def pendientes_command(update: Update, context: ContextTypes.DEFAULT_TYPE):
    try:
        incidencias = obtener_pendientes()

        if not incidencias:
            await update.message.reply_text("✅ No hay incidencias pendientes.")
            return

        mensajes = []

        for inc in incidencias:
            empleado = inc.get("empleadoAsignado") or {}

            mensajes.append(
                f"🚨 {inc.get('folio')}\n"
                f"📌 Asunto: {inc.get('asunto')}\n"
                f"⚠ Prioridad: {inc.get('prioridad')}\n"
                f"👤 Asignado: {empleado.get('nombre', 'Sin asignar')}\n"
                f"📂 Carpeta: {inc.get('carpetaOrigen')}\n"
                f"📍 Estado: {inc.get('estado')}"
            )

        await update.message.reply_text("\n\n".join(mensajes))

    except Exception as e:
        await update.message.reply_text(f"❌ Error consultando pendientes: {e}")


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