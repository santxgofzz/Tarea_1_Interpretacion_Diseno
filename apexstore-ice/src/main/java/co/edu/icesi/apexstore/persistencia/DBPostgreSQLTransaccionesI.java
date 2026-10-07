package co.edu.icesi.apexstore.persistencia;

import ApexStore.EstadoPago;
import ApexStore.ResultadoPago;
import com.zeroc.Ice.Current;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simulación del componente DB_PostgreSQL_Transacciones.
 * La tarea exige simular las pasarelas; para el prototipo la BD también se mantiene en memoria,
 * conservando un único punto de persistencia y un registro auditable por consola.
 */
public class DBPostgreSQLTransaccionesI implements ApexStore.DBPostgreSQLTransacciones {

    private final Map<String, ResultadoPago> transacciones = new LinkedHashMap<>();

    @Override
    public synchronized void persistirTransaccion(ResultadoPago resultado, Current current) {
        ResultadoPago anterior = transacciones.get(resultado.idOrden);

        // Idempotencia básica: un callback duplicado exitoso no genera un segundo cambio contable.
        if (anterior != null
                && anterior.estado == EstadoPago.EXITOSO
                && resultado.estado == EstadoPago.EXITOSO) {
            System.out.printf("[DB_PostgreSQL_Transacciones] Callback duplicado ignorado -> %s%n",
                    resultado.idOrden);
            return;
        }

        // Una orden ya exitosa no vuelve a un estado anterior por una llegada tardía.
        if (anterior != null
                && anterior.estado == EstadoPago.EXITOSO
                && resultado.estado != EstadoPago.EXITOSO) {
            System.out.printf("[DB_PostgreSQL_Transacciones] Cambio rechazado por integridad -> %s ya está EXITOSO%n",
                    resultado.idOrden);
            return;
        }

        transacciones.put(resultado.idOrden, copiar(resultado));

        System.out.printf(
                "[DB_PostgreSQL_Transacciones] %s | orden=%s | metodo=%s | monto=%.2f | estado=%s | %s%n",
                LocalDateTime.now(),
                resultado.idOrden,
                resultado.metodoPago,
                resultado.monto,
                resultado.estado,
                resultado.detalle);
    }

    private ResultadoPago copiar(ResultadoPago r) {
        return new ResultadoPago(r.idOrden, r.metodoPago, r.monto, r.estado, r.detalle);
    }
}
