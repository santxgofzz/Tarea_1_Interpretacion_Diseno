package co.edu.icesi.apexstore.estrategias;

import ApexStore.EstadoPago;
import ApexStore.ProcesadorPagosContextoPrx;
import ApexStore.ResultadoPago;
import ApexStore.SolicitudPago;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.LocalException;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Implementación común de la simulación asíncrona de una pasarela. */
public abstract class EstrategiaPagoBase implements ApexStore.EstrategiaPago {

    private final String nombre;
    private final long demoraMs;
    private final ProcesadorPagosContextoPrx notificador;
    private final ScheduledExecutorService executor;

    protected EstrategiaPagoBase(String nombre,
                                 long demoraMs,
                                 ProcesadorPagosContextoPrx notificador,
                                 ScheduledExecutorService executor) {
        this.nombre = nombre;
        this.demoraMs = demoraMs;
        this.notificador = notificador;
        this.executor = executor;
    }

    @Override
    public String procesarPago(SolicitudPago solicitud, Current current) {
        System.out.printf("[%s] Solicitud aceptada para orden %s. La confirmación llegará por callback.%n",
                nombre, solicitud.idOrden);

        // La llamada inicial retorna rápido; la confirmación final ocurre después.
        executor.schedule(() -> finalizarPago(solicitud), demoraMs, TimeUnit.MILLISECONDS);

        return nombre + " aceptó la solicitud";
    }

    private void finalizarPago(SolicitudPago solicitud) {
        ResultadoPago resultado = new ResultadoPago(
                solicitud.idOrden,
                solicitud.metodoPago.toUpperCase(),
                solicitud.monto,
                EstadoPago.EXITOSO,
                "Pago aprobado por " + nombre + " (simulado)");

        try {
            System.out.printf("[%s] Pago finalizado para %s. Enviando callback...%n",
                    nombre, solicitud.idOrden);
            notificador.notificarResultadoPago(resultado);
        } catch (LocalException ex) {
            // El fallo del callback no tumba el servidor ni a las otras estrategias.
            System.err.printf("[%s] No se pudo notificar el resultado de %s: %s%n",
                    nombre, solicitud.idOrden, ex.getClass().getSimpleName());
        }
    }
}
