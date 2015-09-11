package pt.ipg.mcm.app.grouped.resources;


import pt.ipg.mcm.calls.AuthBasicUtf8;

public interface Constants {
    AuthBasicUtf8 AUTH_BASIC_CONVIDADO = new AuthBasicUtf8("convidado", "convidado");
    String SHARED_SYNC = "shared-sync";
    String SYNC_INDEX = "sync-index";

    interface Localidade {
        String ID = "LOCALIDADE_ID", CODIGO_POSTAL = "CODIGO_POSTAL", LOCALIDADE = "LOCALIDADE";
    }

    interface User {
        String LOGIN = "USER_LOGIN", PASSWORD = "USER_PASSWORD", GUARDAR_LOGIN_PASSWORD = "CB_VALUE_LOGIN_PASSWORD";
    }

    interface Encomenda {
        String ID = "ENCOMENDA_ID";
        String DATA_CRIACAO = "DATA_CRIACAO";
        String DATA_ENTREGA = "DATA_ENTREGA";
        String TOTAL = "TOTAL";
        String ESTADO = "ESTADO";
        String OBSERVACOES = "OBSERVACOES";
    }
}
