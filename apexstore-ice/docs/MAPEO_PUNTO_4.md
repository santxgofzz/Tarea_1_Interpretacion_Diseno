# Mapeo del Punto 3 al Punto 4

La implementación sigue el diagrama arquitectónico corregido y mantiene los cuatro nodos de ejecución de ApexStore. Para la demostración se ejecutan como procesos Java independientes en `localhost`; los hosts se pueden cambiar en los archivos de `config/` sin modificar las clases.

| Diagrama corregido | Implementación Java + ICE |
|---|---|
| WebApp / MobileApp | `WebAppClient` / `MobileAppClient` |
| `IGestionarComprasHttp` | contrato Slice `IGestionarComprasHttp` |
| `ServicioCheckout` | `ServicioCheckoutI` |
| `IIniciarPagoOrden` | contrato Slice `IIniciarPagoOrden` |
| `ProcesadorPagosContexto` | `ProcesadorPagosContextoI` |
| `IProcesarPago` | contrato Slice común consumido por el contexto |
| `EstrategiaStripe` | `EstrategiaStripeI` |
| `EstrategiaPSE` | `EstrategiaPSEI` |
| `EstrategiaCripto` | `EstrategiaCriptoI` |
| `INotificarResultadoPago` | callback Slice `INotificarResultadoPago` |
| `IPersistirTransaccion` | contrato Slice `IPersistirTransaccion` |
| `DB_PostgreSQL_Transacciones` | `DBPostgreSQLTransaccionesI` (simulación en memoria) |

## Correcciones del Punto 3 conservadas en código

1. **Strategy común:** `ProcesadorPagosContextoI` solo utiliza proxies de tipo `EstrategiaPagoPrx`. No invoca operaciones particulares de Stripe, PSE o Cripto.
2. **Cripto no accede a la base de datos:** las tres estrategias notifican por el mismo callback `INotificarResultadoPago` y solamente el contexto utiliza `IPersistirTransaccion`.
3. **Persistencia abstraída:** el contexto depende de `DBPostgreSQLTransaccionesPrx`, no de una interfaz denominada `IPersistirTransaccionPostgres`.
4. **Flujo asíncrono:** `IProcesarPago` retorna la aceptación inicial y cada estrategia programa la confirmación para después. El resultado llega al contexto mediante callback.
5. **Distribución:** Backend, estrategias y persistencia escuchan en puertos ICE diferentes (`10000`, `10001`, `10002`).

## Flujo de una compra

```text
WebApp / MobileApp
        |
        | IGestionarComprasHttp
        v
ServicioCheckout
        |
        | IIniciarPagoOrden
        v
ProcesadorPagosContexto
        |
        | IProcesarPago (misma interfaz)
        v
Stripe / PSE / Cripto
        |
        | callback INotificarResultadoPago
        v
ProcesadorPagosContexto
        |
        | IPersistirTransaccion
        v
DB_PostgreSQL_Transacciones
```

Las pasarelas son simuladas y no se realiza ninguna conexión a servicios financieros reales, tal como exige el enunciado.
