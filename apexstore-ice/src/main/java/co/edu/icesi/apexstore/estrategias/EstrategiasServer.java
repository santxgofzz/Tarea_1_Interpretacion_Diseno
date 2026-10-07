package co.edu.icesi.apexstore.estrategias;

import ApexStore.ProcesadorPagosContextoPrx;
import co.edu.icesi.apexstore.util.Proxies;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/** Nodo 3: Servidor de Pasarelas y Estrategias de Pago. */
public class EstrategiasServer {

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

        try (Communicator communicator = Util.initialize(args, "config/estrategias.cfg", extraArgs)) {
            if (!extraArgs.isEmpty()) {
                System.err.println("Argumentos no reconocidos: " + extraArgs);
            }

            ProcesadorPagosContextoPrx notificador = ProcesadorPagosContextoPrx.uncheckedCast(
                    Proxies.require(communicator, "INotificarResultadoPago.Proxy"));

            ObjectAdapter adapter = communicator.createObjectAdapter("EstrategiasAdapter");

            // Tres componentes distintos, un único contrato Strategy: IProcesarPago.
            adapter.add(new EstrategiaStripeI(notificador, executor),
                    Util.stringToIdentity("EstrategiaStripe"));
            adapter.add(new EstrategiaPSEI(notificador, executor),
                    Util.stringToIdentity("EstrategiaPSE"));
            adapter.add(new EstrategiaCriptoI(notificador, executor),
                    Util.stringToIdentity("EstrategiaCripto"));

            adapter.activate();

            System.out.println("============================================================");
            System.out.println(" NODO 3 - PASARELAS Y ESTRATEGIAS DE PAGO");
            System.out.println(" EstrategiaStripe -> IProcesarPago");
            System.out.println(" EstrategiaPSE    -> IProcesarPago");
            System.out.println(" EstrategiaCripto -> IProcesarPago");
            System.out.println(" Callback común   -> INotificarResultadoPago");
            System.out.println(" Puerto ICE: 10001");
            System.out.println("============================================================");

            communicator.waitForShutdown();
        } finally {
            executor.shutdownNow();
        }
    }
}
