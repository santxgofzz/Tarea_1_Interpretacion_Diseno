package co.edu.icesi.apexstore.backend;

import ApexStore.EstadoPago;
import ApexStore.DBPostgreSQLTransaccionesPrx;
import ApexStore.EstrategiaPagoPrx;
import ApexStore.ResultadoPago;
import ApexStore.SolicitudPago;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.LocalException;

import java.util.Locale;
import java.util.Map;

/**
 * Context del patrón Strategy.
 * Solo conoce la abstracción IProcesarPago; no depende de Stripe, PSE o Cripto concretos.
 */
public class ProcesadorPagosContextoI implements ApexStore.ProcesadorPagosContexto {

    private final Map<String, EstrategiaPagoPrx> estrategias;
    private final DBPostgreSQLTransaccionesPrx persistencia;

    public ProcesadorPagosContextoI(Map<String, EstrategiaPagoPrx> estrategias,
                                    DBPostgreSQLTransaccionesPrx persistencia) {
        this.estrategias = Map.copyOf(estrategias);
        this.persistencia = persistencia;
    }

    @Override
    public String iniciarPagoOrden(SolicitudPago solicitud, Current current) {
        String error = validarSolicitud(solicitud);
        if (error != null) {
            return "RECHAZADO: " + error;
        }

        String metodo = solicitud.metodoPago.trim().toUpperCase(Locale.ROOT);
        EstrategiaPagoPrx estrategia = estrategias.get(metodo);
        if (estrategia == null) {
            return "RECHAZADO: método de pago no soportado: " + solicitud.metodoPago;
        }

        // El contexto centraliza el estado de la transacción.
        persistirSeguro(new ResultadoPago(
                solicitud.idOrden,
                metodo,
                solicitud.monto,
                EstadoPago.PENDIENTE,
                "Pago enviado a procesamiento"));

        try {
            System.out.printf("[ProcesadorPagosContexto] Estrategia seleccionada: %s%n", metodo);
            String aceptacion = estrategia.procesarPago(solicitud);
            return "EN_PROCESO: " + aceptacion;
        } catch (LocalException ex) {
            System.err.printf("[ProcesadorPagosContexto] Falló únicamente la estrategia %s: %s%n",
                    metodo, ex.getClass().getSimpleName());

            persistirSeguro(new ResultadoPago(
                    solicitud.idOrden,
                    metodo,
                    solicitud.monto,
                    EstadoPago.FALLIDO,
                    "No fue posible contactar la estrategia de pago"));

            return "FALLIDO: la pasarela " + metodo + " no está disponible";
        }
    }

    /**
     * Callback asíncrono común para Stripe, PSE y Cripto.
     * Ninguna estrategia escribe directamente en la base de datos.
     */
    @Override
    public void notificarResultadoPago(ResultadoPago resultado, Current current) {
        System.out.printf(
                "[ProcesadorPagosContexto] Callback -> orden=%s, metodo=%s, estado=%s%n",
                resultado.idOrden, resultado.metodoPago, resultado.estado);

        persistirSeguro(resultado);

        // En un sistema real, después de persistir se publicaría la confirmación al cliente.
        System.out.printf("[ProcesadorPagosContexto] Orden %s actualizada. Confirmación lista para el cliente.%n",
                resultado.idOrden);
    }

    private void persistirSeguro(ResultadoPago resultado) {
        try {
            persistencia.persistirTransaccion(resultado);
        } catch (LocalException ex) {
            System.err.printf("[ProcesadorPagosContexto] Error de persistencia para %s: %s%n",
                    resultado.idOrden, ex.getClass().getSimpleName());
        }
    }

    private String validarSolicitud(SolicitudPago solicitud) {
        if (solicitud == null) {
            return "solicitud vacía";
        }
        if (solicitud.idOrden == null || solicitud.idOrden.isBlank()) {
            return "id de orden obligatorio";
        }
        if (solicitud.metodoPago == null || solicitud.metodoPago.isBlank()) {
            return "método de pago obligatorio";
        }
        if (solicitud.monto <= 0) {
            return "el monto debe ser mayor que cero";
        }
        return null;
    }
}
