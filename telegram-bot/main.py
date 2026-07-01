from telegram.ext import Application, CommandHandler

from config import TELEGRAM_BOT_TOKEN
from commands import help_command, pendientes_command, estado_command, resuelto_command, chatid_command


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

    print("🤖 Bot de incidencias iniciado...")
    app.run_polling()


if __name__ == "__main__":
    main()