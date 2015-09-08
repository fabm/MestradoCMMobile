package pt.ipg.mcm.app.assync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import de.greenrobot.dao.query.QueryBuilder;
import pt.ipg.mcm.app.bd.Categoria;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.bd.EncomendaDao;
import pt.ipg.mcm.app.bd.EncomendaProduto;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.AuthBasicUtf8;
import pt.ipg.mcm.calls.RestJsonCall;
import pt.ipg.mcm.calls.WebserviceException;
import pt.ipg.mcm.calls.client.DateHelper;
import pt.ipg.mcm.calls.client.model.categoria.CategoriaRest;
import pt.ipg.mcm.calls.client.model.categoria.GetCategoriaDesyncRest;
import pt.ipg.mcm.calls.client.model.delete.GetRegistosAApagarRest;
import pt.ipg.mcm.calls.client.model.delete.RegistoAApagarRest;
import pt.ipg.mcm.calls.client.model.encomendas.EncomendaIdsRest;
import pt.ipg.mcm.calls.client.model.encomendas.EncomendaRest;
import pt.ipg.mcm.calls.client.model.encomendas.GetMinhasEncomendasRest;
import pt.ipg.mcm.calls.client.model.encomendas.ProdutoEncomendadoRest;
import pt.ipg.mcm.calls.client.model.encomendas.UpdateEncomendasRestIn;
import pt.ipg.mcm.calls.client.model.encomendas.UpdateEncomendasRestOut;
import pt.ipg.mcm.calls.client.model.produtos.GetProdutoDesyncRest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AsyncOfflineTables extends AsyncTask<String, String, Boolean> {
    private Context context;
    private String error;
    private long sync;

    public AsyncOfflineTables(Context context, long sync) {
        this.context = context;
        this.sync = sync;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.v("AsyncOfflineTables", "inicio");

        RestJsonCall.Builder builder =
                App.get()
                        .getNewRestJsonCallBuilder()
                        .serverUrl(Constants.SERVER_URL);


        Sincronizador sincronizador = new Sincronizador(context);

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

            List<Encomenda> encomendas = encomendasDesync();

            Map<Long, Encomenda> encomendasMap = new HashMap();
            UpdateEncomendasRestIn updateEncomendasRestIn = new UpdateEncomendasRestIn();
            List<EncomendaRest> encomendaRestList = updateEncomendasRestIn.createEncomendas();
            for (Encomenda encomenda : encomendas) {
                encomendasMap.put(encomenda.getId(), encomenda);
                EncomendaRest encomendaRest = new EncomendaRest();
                String date = new DateHelper(DateHelper.Format.COMPACT).toString(encomenda.getDataEntrega());
                encomendaRest.setDate(date);
                List<ProdutoEncomendadoRest> produtoEncomendadoRestList = encomendaRest.createProdutoEncomendadoRests();

                for (EncomendaProduto encomendaProduto : encomenda.getEncomendaProdutoList()) {
                    ProdutoEncomendadoRest produtoEncomendadoRest = new ProdutoEncomendadoRest();
                    produtoEncomendadoRest.setIdProduto(encomendaProduto.getIdProduto());
                    produtoEncomendadoRest.setQuandidade(encomendaProduto.getQuantidade());
                    produtoEncomendadoRestList.add(produtoEncomendadoRest);
                }
                encomendaRestList.add(encomendaRest);
            }

            UpdateEncomendasRestOut updateEncomendasRestOut = builder
                    .path("/services/rest/encomenda/update")
                    .post(updateEncomendasRestIn)
                    .build()
                    .getResponse(UpdateEncomendasRestOut.class);

            publishProgress("A carregar encomendas no servidor");
            Log.v("AsyncOfflineTables", "update");
            for (EncomendaIdsRest encomendaIdsRest : updateEncomendasRestOut.getEncomendaIdseRests()) {
                encomendasMap.get((long) encomendaIdsRest.getClientId())
                        .setServerId(encomendaIdsRest.getServerId());
            }

            sincronizador.setEncomendasToUpdateServerId(encomendas);
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

    private List<Encomenda> encomendasDesync() {
        SQLiteDatabase db = App.get().getOpenHelper(context).getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        QueryBuilder<Encomenda> qb = daoMaster
                .newSession()
                .getEncomendaDao()
                .queryBuilder();

        List<Encomenda> list = qb.where(
                qb.or(EncomendaDao.Properties.ServerId.isNull(), EncomendaDao.Properties.Sync.eq(false))
        )
                .build()
                .list();

        for (Encomenda encomenda : list) {
            encomenda.getEncomendaProdutoList();
        }

        db.close();

        return list;
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
