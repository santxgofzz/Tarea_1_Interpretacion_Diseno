@echo off
setlocal
cd /d "%~dp0\.."

echo [1/3] Verificando Java...
where java >nul 2>nul || (
  echo ERROR: Java no esta en PATH.
  exit /b 1
)

echo [2/3] Verificando Maven y slice2java...
where mvn >nul 2>nul || (
  echo ERROR: Maven no esta en PATH.
  exit /b 1
)
where slice2java >nul 2>nul || (
  echo ERROR: slice2java no esta en PATH.
  echo Instala ZeroC Ice 3.7 para Windows y vuelve a abrir la terminal.
  echo Luego valida con: slice2java -v
  exit /b 1
)

echo [3/3] Compilando proyecto y contrato Slice...
call mvn clean compile
if errorlevel 1 exit /b 1

echo.
echo BUILD SUCCESS - ApexStore listo para ejecutar.
endlocal
