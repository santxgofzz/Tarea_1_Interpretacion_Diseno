@echo off
setlocal
cd /d "%~dp0\.."

call scripts\build.cmd
if errorlevel 1 exit /b 1

echo.
echo Iniciando los 3 servidores en ventanas separadas...
start "ApexStore - Persistencia" cmd /k scripts\run-persistencia.cmd
timeout /t 1 /nobreak >nul
start "ApexStore - Backend" cmd /k scripts\run-backend.cmd
timeout /t 1 /nobreak >nul
start "ApexStore - Estrategias" cmd /k scripts\run-estrategias.cmd
timeout /t 2 /nobreak >nul

echo.
echo Ejecutando una compra WebApp con STRIPE...
call scripts\run-web.cmd STRIPE 250000 WEB-DEMO-001

echo.
echo Espera unos segundos y revisa las ventanas Backend, Estrategias y Persistencia.
echo Tambien puedes probar:
echo   scripts\run-mobile.cmd PSE 180000 MOB-DEMO-001
echo   scripts\run-web.cmd CRIPTO 350000 WEB-CRIPTO-001
endlocal
