package pt.ipg.mcm.app;

import pt.ipg.mcm.app.bd.Categoria;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.bd.EncomendaProduto;
import pt.ipg.mcm.app.bd.Produto;

import java.util.GregorianCalendar;

public class DbCreation {
  private DaoSession session;

  public DbCreation(DaoSession daoSession) {
    this.session = daoSession;
  }

  public void createScenario(){
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

}
