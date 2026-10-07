package co.edu.icesi.apexstore.estrategias;

import ApexStore.ProcesadorPagosContextoPrx;

import java.util.concurrent.ScheduledExecutorService;

public class EstrategiaCriptoI extends EstrategiaPagoBase {
    public EstrategiaCriptoI(ProcesadorPagosContextoPrx notificador, ScheduledExecutorService executor) {
        super("Cripto", 2200, notificador, executor);
    }
}
