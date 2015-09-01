package pt.ipg.mcm.app.tests;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import pt.ipg.mcm.app.JsonTestFile;
import pt.ipg.mcm.app.RobolectricGradleTestRunner;
import pt.ipg.mcm.app.assync.AsyncOfflineTables;
import pt.ipg.mcm.app.assync.AsyncUserSign;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.bd.EncomendaProduto;
import pt.ipg.mcm.app.call.RestJsonCallMock;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.app.instances.DelegatedApp4RETests;
import pt.ipg.mcm.calls.client.DateHelper;
import pt.ipg.mcm.calls.client.model.CheckException;
import pt.ipg.mcm.calls.client.model.RetornoRest;
import pt.ipg.mcm.calls.client.model.categoria.CategoriaRest;
import pt.ipg.mcm.calls.client.model.categoria.GetCategoriaDesyncRest;
import pt.ipg.mcm.calls.client.model.delete.GetRegistosAApagarRest;
import pt.ipg.mcm.calls.client.model.delete.RegistoAApagarRest;
import pt.ipg.mcm.calls.client.model.encomendas.EncomendaDetalheRest;
import pt.ipg.mcm.calls.client.model.encomendas.EncomendaIdsRest;
import pt.ipg.mcm.calls.client.model.encomendas.GetMinhasEncomendasRest;
import pt.ipg.mcm.calls.client.model.encomendas.ProdutoEncomendadoComPrecoRest;
import pt.ipg.mcm.calls.client.model.encomendas.UpdateEncomendasRestOut;
import pt.ipg.mcm.calls.client.model.produtos.GetProdutoDesyncRest;
import pt.ipg.mcm.calls.client.model.produtos.ProdutoRest;
import pt.ipg.mcm.calls.client.model.user.CreateUserClientRestIn;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricGradleTestRunner.class)
public class ApplicationTest {

    private DelegatedApp4RETests app;

    @Before
    public void setup() {
        if (app == null) {
            app = (DelegatedApp4RETests) App.get();
        }
    }

    @Test
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void createUser() throws InterruptedException, IOException, CheckException {

        CreateUserClientRestIn createUserClientRestIn = new CreateUserClientRestIn();
        createUserClientRestIn.setAndCheckLogin("login");
        createUserClientRestIn.setAndCheckPassword("pass");
        createUserClientRestIn.setAndCheckContribuinte("123123123");
        createUserClientRestIn.setContacto("1234");
        createUserClientRestIn.setAndCheckPorta("123");
        createUserClientRestIn.setAndCheckMorada("minha avenida");
        createUserClientRestIn.setAndCheckEmail("sapo@sapo.pt");
        createUserClientRestIn.setAndCheckNome("Francisco");
        createUserClientRestIn.setDataNascimento(new DateHelper(DateHelper.Format.COMPACT).toString(2015, 1, 1));
        createUserClientRestIn.checkDataDeNascimento();
        createUserClientRestIn.setLocalidade(123);
        createUserClientRestIn.checkLocalidade();

        RetornoRest retornoRest = new RetornoRest();
        retornoRest.setMensagem("Ok");
        retornoRest.setCodigo(1);

        JsonTestFile.writeRequest("response1-registar.json", retornoRest);

        RestJsonCallMock restJsonMock = new RestJsonCallMock("response1-registar.json");
        app.setRestJsonCallMock(restJsonMock);
        AsyncUserSign asyncUserSign = new AsyncUserSign(Robolectric.application.getApplicationContext()) {

            @Override
            protected void onPostExecute() {

            }

            @Override
            protected void onPostExecute(String error) {
                Assert.fail(error);
            }
        };

        asyncUserSign.execute(createUserClientRestIn);

    }

    public void testCreateEncomendas() {

    }

    @Test
    public void testJson() {


    }

    @Test
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void testSynchronise() throws IOException, URISyntaxException {
        createFiles1();

        RestJsonCallMock restJsonCallMock = new RestJsonCallMock();
        Map<String, String> map = restJsonCallMock.getMapRequest();
        map.put("/services/rest/categoria/desync/0", "response1-categorias.json");
        map.put("/services/rest/encomenda/minhas/0", "response1-encomendas.json");
        map.put("/services/rest/produto/desync/0", "response1-produtos.json");
        map.put("/services/rest/delete/desync/0", "response1-aapagar.json");
        map.put("/services/rest/encomenda/update", "response1-post-encomendas.json");

        app.setRestJsonCallMock(restJsonCallMock);

        AsyncOfflineTables asyncOfflineTables = new AsyncOfflineTables(Robolectric.application.getApplicationContext(), 0) {

            @Override
            protected void onPostExecute(long syncIndex) {

            }

            @Override
            protected void onPostExecute(String error) {
                Assert.fail();
            }
        };

        asyncOfflineTables.execute("francisco", "francisco");

        SQLiteDatabase db = App.get().getOpenHelper(Robolectric.application).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        final DaoSession session = daoMaster.newSession();

        session.runInTx(new Runnable() {
            @Override
            public void run() {
                Encomenda encomenda = new Encomenda();

                Calendar data = new GregorianCalendar(2015, 1, 2);
                encomenda.setDataEntrega(data.getTime());
                session.getEncomendaDao().insert(encomenda);
                EncomendaProduto encomendaProduto = new EncomendaProduto();
                encomendaProduto.setIdEncomenda(encomenda.getId());
                encomendaProduto.setPrecoUnitario(20);
                encomendaProduto.setIdProduto(1L);
                encomendaProduto.setQuantidade(1);
                session.getEncomendaProdutoDao().insert(encomendaProduto);

                encomenda = new Encomenda();
                data.add(Calendar.DAY_OF_MONTH, 1);
                encomenda.setDataEntrega(data.getTime());
                session.getEncomendaDao().insert(encomenda);
                encomendaProduto = new EncomendaProduto();
                encomendaProduto.setPrecoUnitario(20);
                encomendaProduto.setIdProduto(1L);
                encomendaProduto.setQuantidade(1);
                session.getEncomendaProdutoDao().insert(encomendaProduto);
            }
        });

        createFiles2();

        restJsonCallMock = new RestJsonCallMock();
        map = restJsonCallMock.getMapRequest();
        map.put("/services/rest/authentication/login", "response1-login.json");
        map.put("/services/rest/categoria/desync/10", "response2-categorias.json");
        map.put("/services/rest/encomenda/minhas/10", "response2-encomendas.json");
        map.put("/services/rest/produto/desync/10", "response2-produtos.json");
        map.put("/services/rest/delete/desync/10", "response2-aapagar.json");

        asyncOfflineTables = new AsyncOfflineTables(Robolectric.application.getApplicationContext(), 0) {


            @Override
            protected void onPostExecute(long syncIndex) {

            }

            @Override
            protected void onPostExecute(String error) {
                Assert.fail(error);
            }
        };

        asyncOfflineTables.execute("francisco", "francisco");

    }

    private void createFiles1() throws IOException, URISyntaxException {
        GetCategoriaDesyncRest getCategoriaDesyncRest = new GetCategoriaDesyncRest();
        CategoriaRest categoriaRest = new CategoriaRest();
        categoriaRest.setNome("pão centeio");
        categoriaRest.setDescricao("pão com farinha de centeio");
        categoriaRest.setId(1L);
        List<CategoriaRest> categoriasList = getCategoriaDesyncRest.createCategorias();
        categoriasList.add(categoriaRest);

        categoriaRest = new CategoriaRest();
        categoriaRest.setNome("pão de trigo");
        categoriaRest.setDescricao("pão com farinha de trigo");
        categoriaRest.setId(2L);
        categoriasList.add(categoriaRest);

        getCategoriaDesyncRest.setMaxVersao(10);

        String fileName = "response1-categorias.json";

        JsonTestFile.writeRequest(fileName, getCategoriaDesyncRest);

        GetMinhasEncomendasRest getMinhasEncomendasRest = new GetMinhasEncomendasRest();
        List<EncomendaDetalheRest> encomendaDetalheRestList = getMinhasEncomendasRest.createEncomendaDetalheRestList();
        EncomendaDetalheRest encomendaDetalheRest = new EncomendaDetalheRest();
        encomendaDetalheRest.setId(1);
        encomendaDetalheRest.setDataCriacao(new DateHelper(DateHelper.Format.COMPACT).toString(new GregorianCalendar(2015, 1, 1).getTime()));
        encomendaDetalheRest.setDataEntrega(new DateHelper(DateHelper.Format.COMPACT).toString(new GregorianCalendar(2015, 1, 2).getTime()));
        encomendaDetalheRest.setEstado(0);
        List<ProdutoEncomendadoComPrecoRest> produtosEncomendadosList = encomendaDetalheRest.createProdutosEncomendados();
        ProdutoEncomendadoComPrecoRest produtoEncomendadoComPrecoRest = new ProdutoEncomendadoComPrecoRest();
        produtoEncomendadoComPrecoRest.setPreco(50);
        produtoEncomendadoComPrecoRest.setQuandidade(2);
        produtoEncomendadoComPrecoRest.setIdProduto(1);
        produtosEncomendadosList.add(produtoEncomendadoComPrecoRest);

        produtoEncomendadoComPrecoRest = new ProdutoEncomendadoComPrecoRest();
        produtoEncomendadoComPrecoRest.setPreco(50);
        produtoEncomendadoComPrecoRest.setIdProduto(2);
        produtoEncomendadoComPrecoRest.setQuandidade(2);
        produtosEncomendadosList.add(produtoEncomendadoComPrecoRest);

        encomendaDetalheRestList.add(encomendaDetalheRest);

        fileName = "response1-encomendas.json";

        JsonTestFile.writeRequest(fileName, getMinhasEncomendasRest);

        GetProdutoDesyncRest getProdutoDesyncRest = new GetProdutoDesyncRest();
        List<ProdutoRest> createProdutoRestList = getProdutoDesyncRest.createProdutoRestList();

        ProdutoRest produtoRest = new ProdutoRest();
        produtoRest.setId(1L);
        produtoRest.setNome("Broa de centeio");
        produtoRest.setCategoria(1);
        InputStreamReader inputStreamReader = new InputStreamReader(getClass().getResourceAsStream("imagens.json"));
        List<String> imageB64 = new Gson().<Map<String, List<String>>>fromJson(inputStreamReader, Map.class).get("imagens");
        produtoRest.setFoto(imageB64.get(0));
        produtoRest.setPrecoUnitario(50);

        createProdutoRestList.add(produtoRest);

        getProdutoDesyncRest.setVersaoMax(10L);

        fileName = "response1-produtos.json";
        JsonTestFile.writeRequest(fileName, getProdutoDesyncRest);

        GetRegistosAApagarRest registosAApagarRest = new GetRegistosAApagarRest();
        List<RegistoAApagarRest> listaRegistosAApagar = registosAApagarRest.createRegistoAApagarList();

        RegistoAApagarRest registoAApagarRest = new RegistoAApagarRest();
        registoAApagarRest.setTabela("ENCOMENDA");
        registoAApagarRest.setId(2L);
        listaRegistosAApagar.add(registoAApagarRest);

        registoAApagarRest = new RegistoAApagarRest();
        registoAApagarRest.setId(8L);
        registoAApagarRest.setTabela("PRODUTO");
        listaRegistosAApagar.add(registoAApagarRest);

        fileName = "response1-aapagar.json";
        JsonTestFile.writeRequest(fileName, registosAApagarRest);

        UpdateEncomendasRestOut updateEncomendasRestOut = new UpdateEncomendasRestOut();
        updateEncomendasRestOut.createEncomendaIdseRests();

        fileName = "response1-post-encomendas.json";
        JsonTestFile.writeRequest(fileName, updateEncomendasRestOut);
    }

    private void createFiles2() throws IOException, URISyntaxException {

        UpdateEncomendasRestOut updateEncomendasRestOut = new UpdateEncomendasRestOut();
        List<EncomendaIdsRest> list = updateEncomendasRestOut.createEncomendaIdseRests();

        EncomendaIdsRest encomendaIdsRest = new EncomendaIdsRest();
        encomendaIdsRest.setDate(new DateHelper(DateHelper.Format.COMPACT).toString(new GregorianCalendar(2015, 1, 2).getTime()));
        encomendaIdsRest.setClientId(2);
        encomendaIdsRest.setServerId(5);
        list.add(encomendaIdsRest);

        JsonTestFile.writeRequest("response1-post-encomendas.json", updateEncomendasRestOut);

    }


    @Test
    public void testEncomendasOut() {

        SQLiteDatabase db = App.get().getOpenHelper(Robolectric.application).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();

        Encomenda encomenda1 = new Encomenda();
        encomenda1.setDataEntrega(new GregorianCalendar(2015, 1, 2).getTime());
        session.getEncomendaDao().insert(encomenda1);

        Encomenda encomenda2 = new Encomenda();
        encomenda2.setDataEntrega(new GregorianCalendar(2015, 1, 3).getTime());
        session.getEncomendaDao().insert(encomenda2);

        EncomendaProduto encomendaProduto = new EncomendaProduto();
        encomendaProduto.setIdEncomenda(encomenda1.getId());
        encomendaProduto.setIdProduto(1L);
        encomendaProduto.setQuantidade(1);
        encomendaProduto.setPrecoUnitario(100);
        session.getEncomendaProdutoDao().insert(encomendaProduto);

        encomendaProduto = new EncomendaProduto();
        encomendaProduto.setIdEncomenda(encomenda1.getId());
        encomendaProduto.setIdProduto(2L);
        encomendaProduto.setQuantidade(2);
        encomendaProduto.setPrecoUnitario(100);
        session.getEncomendaProdutoDao().insert(encomendaProduto);

        encomendaProduto = new EncomendaProduto();
        encomendaProduto.setIdEncomenda(encomenda2.getId());
        encomendaProduto.setIdProduto(1L);
        encomendaProduto.setQuantidade(1);
        encomendaProduto.setPrecoUnitario(100);
        session.getEncomendaProdutoDao().insert(encomendaProduto);

        encomendaProduto = new EncomendaProduto();
        encomendaProduto.setIdEncomenda(encomenda2.getId());
        encomendaProduto.setIdProduto(2L);
        encomendaProduto.setQuantidade(2);
        encomendaProduto.setPrecoUnitario(100);
        session.getEncomendaProdutoDao().insert(encomendaProduto);


    }


    @Test
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void testSynchroniseFrom0() throws IOException, URISyntaxException {
        createFiles1();
        RestJsonCallMock restJsonCallMock = new RestJsonCallMock();
        Map<String, String> map = restJsonCallMock.getMapRequest();
        map.put("/services/rest/encomenda/update", "response1-post-encomendas.json");
        map.put("/services/rest/categoria/desync/0", "response1-categorias.json");
        map.put("/services/rest/encomenda/minhas/0", "response1-encomendas.json");
        map.put("/services/rest/produto/desync/0", "response1-produtos.json");
        map.put("/services/rest/delete/desync/0", "response1-aapagar.json");

        app.setRestJsonCallMock(restJsonCallMock);

        AsyncOfflineTables asyncOfflineTables = new AsyncOfflineTables(Robolectric.application.getApplicationContext(), 0) {

            @Override
            protected void onPostExecute(long syncIndex) {

            }

            @Override
            protected void onPostExecute(String error) {
                Assert.fail(error);
            }
        };

        asyncOfflineTables.execute("francisco", "francisco");

    }

}


