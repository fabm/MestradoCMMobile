package pt.ipg.mcm.app.grouped.resources;

public class EstadoEncomenda {

    public static final int AGUARDAR_CONF_PADEIRO = 1;
    public static final int A_AGUARDAR_ENTREGA = 2;
    public static final int CANCELADO_INCOMP_ENTREGA = 3;
    public static final int CANCELADO_CLIENTE = 4;
    public static final int CONF_PADEIRO = 5;
    public static final int CONF_CLIENTE = 6;

    /**
     * Estado atual da encomenda
     * 1: A espera de confirmação de um padeiro
     * 2: Aguarda entrega
     * 3: Cancelado por incompatibilidade na data de entrega, ma nova encomenda pode ser criada com referênca à original que foi cancelada
     * 4: Cancelado pelo cliente
     * 5: Entregua confirmada pelo padeiro
     * 6: Entrega confirmada pelo cliente
     *
     * @param estado
     */

    public static String getEstado(int estado) {
        switch (estado) {
            case AGUARDAR_CONF_PADEIRO:
                return "Falta conf. padeiro";
            case A_AGUARDAR_ENTREGA:
                return "A aguardar entrega";
            case CANCELADO_INCOMP_ENTREGA:
                return "Cancelado incomp. entrega";
            case CANCELADO_CLIENTE:
                return "Cancelado cliente";
            case CONF_PADEIRO:
                return "Entrega conf. padeiro";
            case CONF_CLIENTE:
                return "Entrega conf. cliente";
            case 0:
                return "--";
            default:
                throw new IllegalStateException("Estado desconhecido:"+estado);
        }
    }
}
