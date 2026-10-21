from telegram import Update
from telegram.ext import Application, CommandHandler,CallbackQueryHandler

from config import TELEGRAM_BOT_TOKEN
from commands import (help_command, pendientes_command, estado_command, resuelto_command, chatid_command, tomar_command,mis_pendientes_command,
                      empleados,empleado_command,estadisticas_command,ranking_command,atrasadas_command,cancelar_command,reasignar_command,admin_command,admin_callback)
                      

def main():
    if not TELEGRAM_BOT_TOKEN:
        raise ValueError("Falta configurar TELEGRAM_BOT_TOKEN en el archivo .env")

    app = Application.builder().token(TELEGRAM_BOT_TOKEN).build()

    app.add_handler(CommandHandler("help", help_command))
    app.add_handler(CommandHandler("ayuda", help_command))
    app.add_handler(CommandHandler("pendientes", pendientes_command))
    app.add_handler(CommandHandler("estado", estado_command))
    app.add_handler(CommandHandler("resuelto", resuelto_command))
    app.add_handler(CommandHandler("chatid", chatid_command))
    app.add_handler(CommandHandler("tomar", tomar_command))
    app.add_handler(CommandHandler("mis_pendientes", mis_pendientes_command))
    app.add_handler(CommandHandler("empleados", empleados))
    app.add_handler(CommandHandler("empleado", empleado_command))
    app.add_handler(CommandHandler("estadisticas", estadisticas_command))
    app.add_handler(CommandHandler("ranking", ranking_command))
    app.add_handler(CommandHandler("atrasadas", atrasadas_command))
    app.add_handler(CommandHandler("cancelar",cancelar_command))
    app.add_handler(CommandHandler("reasignar",reasignar_command))
    app.add_handler(CommandHandler("admin", admin_command))
    app.add_handler(
        CallbackQueryHandler(
            admin_callback,
            pattern=r"^(admin_empleados|admin_empleado:[A-Za-z0-9_]{5,32}|admin_estado:[0-9]+)$"
        )
    )

    print("🤖 Bot de incidencias iniciado...")
    app.run_polling(
    allowed_updates=Update.ALL_TYPES
    )


if __name__ == "__main__":
    main()