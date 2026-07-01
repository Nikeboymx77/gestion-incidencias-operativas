@echo off
title Bot Gestion Incidencias

echo.
echo ===============================================
echo      BOT GESTION DE INCIDENCIAS
echo ===============================================
echo.

call venv\Scripts\activate.bat

python main.py

echo.
echo ===============================================
echo El bot termino su ejecucion.
echo ===============================================
pause