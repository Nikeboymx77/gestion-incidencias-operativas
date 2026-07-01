@echo off
echo ========================================
echo Creando entorno virtual...
echo ========================================

python -m venv venv

call venv\Scripts\activate.bat

python -m pip install --upgrade pip

pip install -r requirements.txt

echo.
echo ========================================
echo Entorno creado correctamente.
echo ========================================
pause