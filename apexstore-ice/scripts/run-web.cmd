@echo off
cd /d "%~dp0\.."
if not exist "target\classes" (
  echo Primero ejecuta scripts\build.cmd
  exit /b 1
)
set METODO=%1
set MONTO=%2
set ORDEN=%3
if "%METODO%"=="" set METODO=STRIPE
if "%MONTO%"=="" set MONTO=250000
if "%ORDEN%"=="" set ORDEN=WEB-001
mvn -q exec:java -Dexec.mainClass=co.edu.icesi.apexstore.client.WebAppClient -Dexec.args="%METODO% %MONTO% %ORDEN%"
