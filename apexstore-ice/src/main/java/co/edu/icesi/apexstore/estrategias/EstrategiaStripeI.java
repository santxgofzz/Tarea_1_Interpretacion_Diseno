package co.edu.icesi.apexstore.estrategias;

import ApexStore.ProcesadorPagosContextoPrx;

import java.util.concurrent.ScheduledExecutorService;

public class EstrategiaStripeI extends EstrategiaPagoBase {
    public EstrategiaStripeI(ProcesadorPagosContextoPrx notificador, ScheduledExecutorService executor) {
        super("Stripe", 1200, notificador, executor);
    }
}
