# ApexStore - Punto 4: Java + ICE

Implementación del **Punto 4 (RA4)** de la Tarea 1 de Ingeniería de Software IV. El proyecto traduce a Java + ZeroC ICE el diagrama corregido del Punto 3, sin agregar servicios financieros reales.

## Qué está implementado

- Los **4 nodos** del diseño se representan mediante procesos separados.
- `ServicioCheckout` es la frontera para WebApp y MobileApp.
- `ProcesadorPagosContexto` utiliza un **Strategy común (`IProcesarPago`)**.
- Stripe, PSE y Cripto implementan el mismo contrato.
- Las 3 estrategias confirman de manera **asíncrona mediante callback**.
- `EstrategiaCripto` **no tiene acceso directo a persistencia**.
- El contexto depende de `IPersistirTransaccion`, no de una interfaz acoplada al nombre PostgreSQL.
- La persistencia está simulada en memoria y agrega controles simples de idempotencia/integridad para la demostración.

Consulta `docs/MAPEO_PUNTO_4.md` para ver la correspondencia uno-a-uno con el diagrama.

## Requisitos

- JDK 17.
- Maven.
- ZeroC Ice **3.7.x** con `slice2java` disponible en `PATH`.

El proyecto usa la dependencia `com.zeroc:ice:3.7.6`, la misma versión que aparece en los ejemplos públicos del curso. Antes de compilar valida:

```powershell
java -version
mvn -version
slice2java -v
```

Si `slice2java` no existe, instala **ZeroC Ice 3.7 para Windows** y vuelve a abrir VS Code/PowerShell para que se actualice el `PATH`.

## Compilar

Desde la raíz del proyecto:

```powershell
.\scripts\build.cmd
```

O directamente:

```powershell
mvn clean compile
```

Durante `generate-sources`, Maven ejecuta `slice2java` sobre `src/main/slice/ApexStore.ice` y genera automáticamente los proxies/interfaces Java en `target/generated-sources/slice`.

## Ejecutar la demo completa

```powershell
.\scripts\run-demo.cmd
```

El script compila, abre tres ventanas para los servidores y luego envía una compra desde WebApp.

### Ejecución manual

Primero compila una sola vez. Después abre tres terminales:

```powershell
.\scripts\run-persistencia.cmd
```

```powershell
.\scripts\run-backend.cmd
```

```powershell
.\scripts\run-estrategias.cmd
```

En una cuarta terminal ejecuta un cliente:

```powershell
.\scripts\run-web.cmd STRIPE 250000 WEB-001
```

```powershell
.\scripts\run-mobile.cmd PSE 180000 MOB-001
```

Para demostrar la tercera estrategia:

```powershell
.\scripts\run-web.cmd CRIPTO 350000 WEB-CRIPTO-001
```

## Qué deberías observar

El cliente obtiene una respuesta rápida similar a:

```text
Respuesta inmediata del Backend: EN_PROCESO: Stripe aceptó la solicitud
La confirmación final continúa de forma asíncrona por callback.
```

Después, en las otras terminales, se observa el flujo:

```text
[ProcesadorPagosContexto] Estrategia seleccionada: STRIPE
[Stripe] Solicitud aceptada...
[Stripe] Pago finalizado... Enviando callback...
[ProcesadorPagosContexto] Callback -> ... EXITOSO
[DB_PostgreSQL_Transacciones] ... estado=EXITOSO
```

Esto evidencia el despacho inicial + confirmación asíncrona y que la persistencia vuelve a estar centralizada en el contexto.

## Puertos usados

- Backend Core: `10000`
- Estrategias: `10001`
- Persistencia: `10002`

Están definidos en `config/*.cfg`. En la demo todos los nodos corren en `localhost`; para desplegarlos en máquinas distintas solo se cambian los hosts de los proxies.

## Importante para la entrega

No es necesario entregar la carpeta `target/`: contiene código generado y compilado. El contrato fuente está en `src/main/slice/ApexStore.ice` y Maven puede regenerar todo con `mvn clean compile`.
