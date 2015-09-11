package pt.ipg.mcm.app.assync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import pt.ipg.mcm.app.bd.*;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.AuthBasicUtf8;
import pt.ipg.mcm.calls.RestJsonCall;
import pt.ipg.mcm.calls.WebserviceException;
import pt.ipg.mcm.calls.client.model.categoria.CategoriaRest;
import pt.ipg.mcm.calls.client.model.categoria.GetCategoriaDesyncRest;
import pt.ipg.mcm.calls.client.model.delete.GetRegistosAApagarRest;
import pt.ipg.mcm.calls.client.model.delete.RegistoAApagarRest;
import pt.ipg.mcm.calls.client.model.encomendas.*;
import pt.ipg.mcm.calls.client.model.produtos.GetProdutoDesyncRest;

import java.util.ArrayList;
import java.util.List;

public abstract class AsyncOfflineTables extends AsyncTask<String, String, Boolean> {
    private Context context;
    private String error;
    private long sync;
    private Sincronizador sincronizador;

    public AsyncOfflineTables(Context context, long sync) {
        this.context = context;
        this.sync = sync;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.v("AsyncOfflineTables", "inicio");

        String host = App.get().getSharedPreferences(context, "host").getString("host", "");

        RestJsonCall.Builder builder =
                App.get()
                        .getNewRestJsonCallBuilder()
                        .serverUrl(host);


        sincronizador = new Sincronizador(context);

        long maxSync = 0;

        String urlStr = String.format("/services/rest/delete/desync/%s", sync);
        try {
            GetRegistosAApagarRest registosAApagarRest = builder
                    .get()
                    .path(urlStr)
                    .build()
                    .getResponse(GetRegistosAApagarRest.class);

            publishProgress("Descarregar registos a apagar");
            Log.v("AsyncOfflineTables", "delete desync:" + sync);
            sincronizador.setDeletedEntities(readEntitiesToDelete(registosAApagarRest));

            urlStr = String.format("/services/rest/categoria/desync/%s", sync);
            GetCategoriaDesyncRest categoriasDesyncRest = builder
                    .path(urlStr)
                    .build()
                    .getResponse(GetCategoriaDesyncRest.class);

            publishProgress("Descarregar categorias");
            Log.v("AsyncOfflineTables", "categorias desync:" + sync);
            sincronizador.setCategorias(readCategorias(categoriasDesyncRest));

            maxSync = Math.max(categoriasDesyncRest.getMaxVersao(), maxSync);
            urlStr = String.format("/services/rest/produto/desync/%s", sync);
            GetProdutoDesyncRest getProdutoDesyncRest = builder
                    .path(urlStr)
                    .build()
                    .getResponse(GetProdutoDesyncRest.class);

            publishProgress("A descarregar produtos");
            sincronizador.setProdutos(getProdutoDesyncRest);

            Log.v("AsyncOfflineTables", "minhas encomendas desync:" + sync);
            urlStr = String.format("/services/rest/encomenda/minhas/%s", sync);
            maxSync = Math.max(getProdutoDesyncRest.getVersaoMax(), maxSync);
            GetMinhasEncomendasRest getMinhasEncomendasRest = builder
                    .path(urlStr)
                    .auth(new AuthBasicUtf8(params[0], params[1]))
                    .build()
                    .getResponse(GetMinhasEncomendasRest.class);

            publishProgress("A descarregar minhas encomendas");
            maxSync = Math.max(getMinhasEncomendasRest.getMaxSync(), maxSync);
            sincronizador.setGetMinhasEncomendasRest(getMinhasEncomendasRest);

            final AddAndUpdateEncomendasInRest addAndUpdateEncomendasInRest = getAddAndUpdateEncomendasInRest();

            AddEncomendasOutRest addEncomendasOutRest = builder
                    .path("/services/rest/encomenda/update")
                    .post(addAndUpdateEncomendasInRest)
                    .build()
                    .getResponse(AddEncomendasOutRest.class);

            publishProgress("A carregar encomendas no servidor");
            Log.v("AsyncOfflineTables", "update");

            for (EncomendaOutRest encomendaIdsRest : addEncomendasOutRest.getEncomendaOutRestList()) {
                sincronizador.getIdsMap().get((long) encomendaIdsRest.getClientId())
                        .setServerId(encomendaIdsRest.getServerId());
            }
            sincronizador.sincronizar();
            sync = maxSync;
        } catch (WebserviceException e) {
            if (!e.isAuthenticationProblem()) {
                Log.e("AsyncOfflineTables", e.getMessage(), e);
                error = "Impossível realizar a comunicação com o servidor";
                return false;
            } else {
                error = "Problema de autenticação";
                return false;
            }
        }


        return true;
    }

    private AddAndUpdateEncomendasInRest getAddAndUpdateEncomendasInRest() {
        AddAndUpdateEncomendasInRest addAndUpdateIn = new AddAndUpdateEncomendasInRest();

        List<EncomendaInRest> encomendaInRestList = new ArrayList<>();


        SQLiteDatabase db = App.get().getOpenHelper(context).getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        DaoSession session = daoMaster.newSession();

        List<Encomenda> encomendas = session
                .getEncomendaDao()
                .queryBuilder()
                .where(EncomendaDao.Properties.ServerId.isNull())
                .build()
                .list();

        for (Encomenda encomenda : encomendas) {
            encomenda.getEncomendaProdutoList();
        }


        for (Encomenda encomenda : encomendas) {
            sincronizador.getIdsMap().put(encomenda.getId(), encomenda);



            EncomendaInRest encomendaRest = new EncomendaInRest();
            encomendaRest.setDataEntrega(encomenda.getDataEntrega());
            List<ProdutoEncomendadoRest> produtoEncomendadoRestList = new ArrayList<>();

            for (EncomendaProduto encomendaProduto : encomenda.getEncomendaProdutoList()) {
                ProdutoEncomendadoRest produtoEncomendadoRest = new ProdutoEncomendadoRest();
                produtoEncomendadoRest.setIdProduto(encomendaProduto.getIdProduto());
                produtoEncomendadoRest.setQuantidade(encomendaProduto.getQuantidade());
                produtoEncomendadoRestList.add(produtoEncomendadoRest);
            }
            encomendaRest.setProdutoEncomendadoRestList(produtoEncomendadoRestList);
            encomendaInRestList.add(encomendaRest);
        }
        addAndUpdateIn.setEncomendaInRestList(encomendaInRestList);


        encomendas = session
                .getEncomendaDao()
                .queryBuilder()
                .where(EncomendaDao.Properties.Sync.eq(false))
                .build()
                .list();

        List<EstadoEncomendaInRest> estadoEncomendaInRestList = new ArrayList<>();

        for(Encomenda encomenda:encomendas){
            EstadoEncomendaInRest estadoEncomenda = new EstadoEncomendaInRest();
            estadoEncomenda.setIdEncomenda(encomenda.getServerId());
            estadoEncomenda.setEstado(encomenda.getEstado());
            estadoEncomendaInRestList.add(estadoEncomenda);
        }

        addAndUpdateIn.setEstadoEncomendaInRestList(estadoEncomendaInRestList);

        db.close();
        return addAndUpdateIn;
    }

    private DeletedEntities readEntitiesToDelete(GetRegistosAApagarRest registosAApagarRest) {
        DeletedEntities deletedEntities = new DeletedEntities();
        if (registosAApagarRest.getRegistoAApagarRestList() == null) {
            return deletedEntities;
        }
        for (RegistoAApagarRest registoAApagarRest : registosAApagarRest.getRegistoAApagarRestList()) {
            deletedEntities.registerDelete(registoAApagarRest.getTabela(), registoAApagarRest.getId());
        }
        return deletedEntities;
    }

    private List<Categoria> readCategorias(GetCategoriaDesyncRest response) {
        List<Categoria> categorias = new ArrayList<Categoria>();

        for (CategoriaRest categoriaRest : response.createCategorias()) {
            Categoria categoria = new Categoria();
            categoria.setId(categoriaRest.getId());
            categoria.setNome(categoriaRest.getNome());
            categoria.setDescricao(categoriaRest.getDescricao());
        }

        return categorias;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (!success) {
            onPostExecute(error);
        } else {
            onPostExecute(sync);
        }
    }

    protected abstract void onPostExecute(long syncIndex);

    protected abstract void onPostExecute(String error);
}
