package pt.ipg.mcm.app.tests;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.query.Query;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import pt.ipg.mcm.app.RobolectricGradleTestRunner;
import pt.ipg.mcm.app.bd.Categoria;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.bd.EncomendaProduto;
import pt.ipg.mcm.app.bd.EncomendaProdutoDao;
import pt.ipg.mcm.app.bd.Produto;
import pt.ipg.mcm.app.instances.App;

import java.util.GregorianCalendar;
import java.util.List;

@RunWith(RobolectricGradleTestRunner.class)
public class DBTest {

  @Test
  public void dbTest(){
    SQLiteDatabase db = App.get()
        .getOpenHelper(Robolectric.application.getApplicationContext())
        .getWritableDatabase();

    final DaoSession session = new DaoMaster(db).newSession();

    session.runInTx(new Runnable() {
      @Override
      public void run() {

        Categoria categoria = new Categoria();
        categoria.setNome("Integral");
        session.getCategoriaDao().insert(categoria);

        Produto produto = new Produto();
        produto.setPrecoActual(50);
        produto.setNome("Mealhada integral");
        produto.setIdCategoria(categoria.getId());
        session.getProdutoDao().insert(produto);

        Encomenda encomenda = new Encomenda();
        encomenda.setDataEntrega(new GregorianCalendar(2015, 1, 1).getTime());
        session.getEncomendaDao().insert(encomenda);

        EncomendaProduto produtoEncomenddo = new EncomendaProduto();
        produtoEncomenddo.setIdEncomenda(encomenda.getId());
        produtoEncomenddo.setQuantidade(1);
        produtoEncomenddo.setPrecoUnitario(50);
        produtoEncomenddo.setProduto(produto);
        session.getEncomendaProdutoDao().insert(produtoEncomenddo);

      }
    });

    db.close();

    db = App.get().getOpenHelper(Robolectric.application.getApplicationContext()).getReadableDatabase();

    DaoSession readSession = new DaoMaster(db).newSession();

    List<Encomenda> list = readSession.getEncomendaDao()
        .queryBuilder()
        .build()
        .list();

    for(Encomenda encomenda:list){
      for(EncomendaProduto produto:encomenda.getEncomendaProdutoList()){
        System.out.println(produto.getProduto().getNome());
      }
    }

  }
}
