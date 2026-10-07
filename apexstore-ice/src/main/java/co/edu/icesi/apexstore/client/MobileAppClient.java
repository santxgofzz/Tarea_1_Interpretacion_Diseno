package co.edu.icesi.apexstore.client;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.List;

/** Nodo 1: simulación del componente MobileApp (Flutter iOS/Android) mediante un cliente Java ICE. */
public class MobileAppClient {

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (Communicator communicator = Util.initialize(new String[0], "config/client.cfg", extraArgs)) {
            String metodo = args.length > 0 ? args[0] : "PSE";
            double monto = args.length > 1 ? Double.parseDouble(args[1]) : 180000.0;
            String orden = args.length > 2 ? args[2] : "MOB-001";

            ClienteBase.enviarCompra(communicator, "MobileApp", orden, metodo, monto);
        }
    }
}
