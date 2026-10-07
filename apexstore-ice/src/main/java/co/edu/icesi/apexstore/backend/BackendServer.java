package co.edu.icesi.apexstore.backend;

import ApexStore.DBPostgreSQLTransaccionesPrx;
import ApexStore.EstrategiaPagoPrx;
import co.edu.icesi.apexstore.util.Proxies;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Nodo 2: Servidor E-Commerce (Backend Core). */
public class BackendServer {

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (Communicator communicator =
                     Util.initialize(args, "config/backend.cfg", extraArgs)) {

            if (!extraArgs.isEmpty()) {
                System.err.println("Argumentos no reconocidos: " + extraArgs);
            }

            ObjectAdapter adapter =
                    communicator.createObjectAdapter("BackendAdapter");

            // Proxy remoto hacia el componente de persistencia del Nodo 4.
            DBPostgreSQLTransaccionesPrx persistencia =
                    DBPostgreSQLTransaccionesPrx.uncheckedCast(
                            Proxies.require(
                                    communicator,
                                    "IPersistirTransaccion.Proxy"
                            )
                    );

            // Las tres estrategias se consumen mediante
            // el mismo contrato común de procesamiento.
            Map<String, EstrategiaPagoPrx> estrategias = new HashMap<>();

            estrategias.put(
                    "STRIPE",
                    EstrategiaPagoPrx.uncheckedCast(
                            Proxies.require(
                                    communicator,
                                    "IProcesarPago.Stripe.Proxy"
                            )
                    )
            );

            estrategias.put(
                    "PSE",
                    EstrategiaPagoPrx.uncheckedCast(
                            Proxies.require(
                                    communicator,
                                    "IProcesarPago.PSE.Proxy"
                            )
                    )
            );

            estrategias.put(
                    "CRIPTO",
                    EstrategiaPagoPrx.uncheckedCast(
                            Proxies.require(
                                    communicator,
                                    "IProcesarPago.Cripto.Proxy"
                            )
                    )
            );

            // Componente ProcesadorPagosContexto.
            ProcesadorPagosContextoI procesador =
                    new ProcesadorPagosContextoI(
                            estrategias,
                            persistencia
                    );

            adapter.add(
                    procesador,
                    Util.stringToIdentity("ProcesadorPagosContexto")
            );

            // Ambos componentes pertenecen al Nodo 2,
            // por eso la comunicación entre ellos es local.
            ServicioCheckoutI checkout =
                    new ServicioCheckoutI(procesador);

            adapter.add(
                    checkout,
                    Util.stringToIdentity("ServicioCheckout")
            );

            adapter.activate();

            System.out.println("============================================================");
            System.out.println(" NODO 2 - SERVIDOR E-COMMERCE / BACKEND CORE");
            System.out.println(" ServicioCheckout        -> ContratoGestionCompras");
            System.out.println(" ProcesadorPagosContexto -> ContratoInicioPago / ContratoNotificacionPago");
            System.out.println(" Puerto ICE: 10000");
            System.out.println("============================================================");

            communicator.waitForShutdown();
        }
    }
}