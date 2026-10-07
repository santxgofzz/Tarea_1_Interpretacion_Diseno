@echo off
cd /d "%~dp0\.."
if not exist "target\classes" (
  echo Primero ejecuta scripts\build.cmd
  exit /b 1
)
mvn -q exec:java -Dexec.mainClass=co.edu.icesi.apexstore.persistencia.PersistenciaServer
