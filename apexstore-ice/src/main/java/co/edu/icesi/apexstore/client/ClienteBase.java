package co.edu.icesi.apexstore.client;

import ApexStore.ServicioCheckoutPrx;
import ApexStore.SolicitudPago;
import co.edu.icesi.apexstore.util.Proxies;
import com.zeroc.Ice.Communicator;

/** Código compartido por las simulaciones WebApp y MobileApp. */
public final class ClienteBase {

    private ClienteBase() {
    }

    public static void enviarCompra(Communicator communicator,
                                    String tipoCliente,
                                    String idOrden,
                                    String metodo,
                                    double monto) {
        ServicioCheckoutPrx checkout = ServicioCheckoutPrx.checkedCast(
                Proxies.require(communicator, "IGestionarComprasHttp.Proxy"));

        if (checkout == null) {
            throw new IllegalStateException("El objeto remoto no implementa IGestionarComprasHttp");
        }

        SolicitudPago solicitud = new SolicitudPago(idOrden, metodo, monto);

        System.out.println("============================================================");
        System.out.println(" " + tipoCliente + " - APEXSTORE");
        System.out.println(" Orden : " + idOrden);
        System.out.println(" Método: " + metodo);
        System.out.println(" Monto : " + monto);
        System.out.println("============================================================");

        String respuesta = checkout.gestionarComprasHttp(solicitud);
        System.out.println("Respuesta inmediata del Backend: " + respuesta);
        System.out.println("La confirmación final continúa de forma asíncrona por callback.");
    }
}
