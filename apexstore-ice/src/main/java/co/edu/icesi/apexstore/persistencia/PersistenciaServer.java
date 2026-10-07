package co.edu.icesi.apexstore.persistencia;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.List;

/** Nodo 4: Servidor Base de Datos Transaccional. */
public class PersistenciaServer {

    public static void main(String[] args) {
        List<String> extraArgs = new ArrayList<>();

        try (Communicator communicator = Util.initialize(args, "config/persistencia.cfg", extraArgs)) {
            if (!extraArgs.isEmpty()) {
                System.err.println("Argumentos no reconocidos: " + extraArgs);
            }

            ObjectAdapter adapter = communicator.createObjectAdapter("PersistenciaAdapter");
            adapter.add(new DBPostgreSQLTransaccionesI(),
                    Util.stringToIdentity("DBPostgreSQLTransacciones"));
            adapter.activate();

            System.out.println("============================================================");
            System.out.println(" NODO 4 - BASE DE DATOS TRANSACCIONAL");
            System.out.println(" DB_PostgreSQL_Transacciones -> IPersistirTransaccion");
            System.out.println(" Puerto ICE: 10002");
            System.out.println("============================================================");

            communicator.waitForShutdown();
        }
    }
}
