package co.edu.icesi.apexstore.estrategias;

import ApexStore.ProcesadorPagosContextoPrx;

import java.util.concurrent.ScheduledExecutorService;

public class EstrategiaPSEI extends EstrategiaPagoBase {
    public EstrategiaPSEI(ProcesadorPagosContextoPrx notificador, ScheduledExecutorService executor) {
        super("PSE", 1800, notificador, executor);
    }
}
