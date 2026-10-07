module ApexStore
{
    enum EstadoPago
    {
        PENDIENTE,
        EXITOSO,
        FALLIDO
    }

    struct SolicitudPago
    {
        string idOrden;
        string metodoPago;
        double monto;
    }

    struct ResultadoPago
    {
        string idOrden;
        string metodoPago;
        double monto;
        EstadoPago estado;
        string detalle;
    }

    // Contratos del diseño arquitectónico corregido.

    interface ContratoGestionCompras
    {
        string gestionarComprasHttp(SolicitudPago solicitud);
    }

    interface ContratoInicioPago
    {
        string iniciarPagoOrden(SolicitudPago solicitud);
    }

    interface ContratoProcesamientoPago
    {
        string procesarPago(SolicitudPago solicitud);
    }

    interface ContratoNotificacionPago
    {
        void notificarResultadoPago(ResultadoPago resultado);
    }

    interface ContratoPersistencia
    {
        void persistirTransaccion(ResultadoPago resultado);
    }

    // Componentes del diagrama.

    interface ServicioCheckout extends ContratoGestionCompras
    {
    }

    interface ProcesadorPagosContexto
        extends ContratoInicioPago, ContratoNotificacionPago
    {
    }

    interface EstrategiaPago extends ContratoProcesamientoPago
    {
    }

    interface DBPostgreSQLTransacciones extends ContratoPersistencia
    {
    }
}