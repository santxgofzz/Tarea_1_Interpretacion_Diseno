package co.edu.icesi.apexstore.backend;

import ApexStore.SolicitudPago;
import com.zeroc.Ice.Current;

public class ServicioCheckoutI implements ApexStore.ServicioCheckout {

    private final ProcesadorPagosContextoI procesadorPagos;

    public ServicioCheckoutI(ProcesadorPagosContextoI procesadorPagos) {
        this.procesadorPagos = procesadorPagos;
    }

    @Override
    public String gestionarComprasHttp(
            SolicitudPago solicitud,
            Current current) {

        System.out.printf(
                "[ServicioCheckout] Compra recibida -> orden=%s, metodo=%s, monto=%.2f%n",
                solicitud.idOrden,
                solicitud.metodoPago,
                solicitud.monto
        );

        return procesadorPagos.iniciarPagoOrden(solicitud, current);
    }
}