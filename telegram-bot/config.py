import os
from dotenv import load_dotenv

load_dotenv()

TELEGRAM_BOT_TOKEN = os.getenv("TELEGRAM_BOT_TOKEN")
API_BASE_URL = os.getenv("API_BASE_URL", "http://localhost:8081/api")

AUTHORIZED_USERS = [
    user.strip()
    for user in os.getenv("AUTHORIZED_USERS", "").split(",")
    if user.strip()
]