package co.edu.icesi.apexstore.util;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;

/** Utilidad mínima para leer referencias ICE desde los archivos de configuración. */
public final class Proxies {

    private Proxies() {
    }

    public static ObjectPrx require(Communicator communicator, String propertyName) {
        ObjectPrx proxy = communicator.propertyToProxy(propertyName);
        if (proxy == null) {
            throw new IllegalStateException("No se encontró la propiedad ICE: " + propertyName);
        }
        return proxy;
    }
}
