package pt.ipg.mcm.app.grouped.resources;

public class EstadoEncomenda {

    public static String getEstado(int estado) {
        switch (estado) {
            case 1:
                return "À espera de confirmação de um padeiro";
            case 2:
                return "A aguardar";
            case 5:
                return "Cancelado pelo cliente";
            default:
                throw new IllegalStateException("Estado desconhecido");
        }
    }
}
