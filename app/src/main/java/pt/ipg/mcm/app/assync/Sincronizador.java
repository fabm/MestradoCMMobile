package pt.ipg.mcm.app.assync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import de.greenrobot.dao.query.CloseableListIterator;
import pt.ipg.mcm.app.bd.Categoria;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.bd.EncomendaProduto;
import pt.ipg.mcm.app.bd.Produto;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.client.DateHelper;
import pt.ipg.mcm.calls.client.model.encomendas.EncomendaDetalheRest;
import pt.ipg.mcm.calls.client.model.encomendas.GetMinhasEncomendasRest;
import pt.ipg.mcm.calls.client.model.encomendas.ProdutoEncomendadoComPrecoRest;
import pt.ipg.mcm.calls.client.model.produtos.GetProdutoDesyncRest;
import pt.ipg.mcm.calls.client.model.produtos.ProdutoRest;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import static pt.ipg.mcm.app.bd.EncomendaDao.Properties.ServerId;

public class Sincronizador {
  private DeletedEntities deletedEntities;
  private List<Categoria> categorias;
  private GetProdutoDesyncRest produtos;
  private List<Encomenda> encomendasToUpdateServerId;


  private DaoSession session;
  private Context context;
  private GetMinhasEncomendasRest getMinhasEncomendasRest;

  public Sincronizador(Context context) {
    this.context = context;
  }


  public DeletedEntities getDeletedEntities() {
    return deletedEntities;
  }

  public void setDeletedEntities(DeletedEntities deletedEntities) {
    this.deletedEntities = deletedEntities;
  }

  public List<Categoria> getCategorias() {
    return categorias;
  }

  public void setCategorias(List<Categoria> categorias) {
    this.categorias = categorias;
  }

  public GetProdutoDesyncRest getProdutos() {
    return produtos;
  }

  public void setProdutos(GetProdutoDesyncRest produtos) {
    this.produtos = produtos;
  }


  private void sincronizaBdFromServer() {
    session.runInTx(new Runnable() {
      @Override
      public void run() {
        for (Long encomendaId : getDeletedEntities().getIdsEncomendas()) {
          session.getEncomendaDao().deleteByKey(encomendaId);
        }
        for (Long produtoId : getDeletedEntities().getIdsProdutos()) {
          session.getProdutoDao().deleteByKey(produtoId);
        }
        for (Long categoriaId : getDeletedEntities().getIdsCategorias()) {
          session.getProdutoDao().deleteByKey(categoriaId);
        }
        for (Categoria categoria : getCategorias()) {
          session.getCategoriaDao().insertOrReplace(categoria);
        }
        for (ProdutoRest produtoRest : getProdutos().getProdutoRestList()) {
          Produto produto = new Produto();
          produto.setIdCategoria(produtoRest.getCategoria());
          produto.setId(produtoRest.getId());
          produto.setNome(produtoRest.getNome());
          produto.setPrecoActual(produtoRest.getPrecoUnitario());
          if (produtoRest.getFoto() != null) {
            produto.setFoto(Base64.decode(produtoRest.getFoto().getBytes(),Base64.DEFAULT));
          }
          produto.setSync(true);
          session.getProdutoDao().insertOrReplace(produto);
        }

        for (EncomendaDetalheRest encomendaDetalheRest : getMinhasEncomendasRest.getEncomendaDetalheRestList()) {
          Encomenda encomenda = new Encomenda();
          encomenda.setServerId(encomendaDetalheRest.getId());
          encomenda.setEstado(encomendaDetalheRest.getEstado());
          encomenda.setObservacoes(encomendaDetalheRest.getObservacoes());
          long precoTotal = 0L;

          try {
            encomenda.setDataEntrega(new DateHelper(DateHelper.Format.COMPACT).toDate(encomendaDetalheRest.getDataEntrega()));
            encomenda.setDataCriacao(new DateHelper(DateHelper.Format.COMPACT).toDate(encomendaDetalheRest.getDataCriacao()));
          } catch (ParseException e) {
            throw new IllegalStateException(e);
          }

          CloseableListIterator<Encomenda> li = session.getEncomendaDao().queryBuilder()
              .where(ServerId.eq(encomenda.getServerId()))
              .build().listIterator();
          if (li.hasNext()) {
            continue;
          } else {
            session.getEncomendaDao().insert(encomenda);
          }
          if (li != null) {
            try {
              li.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          for (ProdutoEncomendadoComPrecoRest produtoEncomendadoComPrecoRest : encomendaDetalheRest.getProdutosEncomendados()) {
            EncomendaProduto encomendaProduto = new EncomendaProduto();
            encomendaProduto.setIdEncomenda(encomenda.getId());
            int precoUnitarioCentimos = produtoEncomendadoComPrecoRest.getPreco();
            encomendaProduto.setPrecoUnitario(precoUnitarioCentimos);
            encomendaProduto.setQuantidade(produtoEncomendadoComPrecoRest.getQuandidade());
            encomendaProduto.setIdProduto(produtoEncomendadoComPrecoRest.getIdProduto());
            precoTotal+= precoUnitarioCentimos*encomendaProduto.getQuantidade();
            session.getEncomendaProdutoDao().insertOrReplace(encomendaProduto);
          }
          encomenda.setPrecoTotal(precoTotal);
          session.getEncomendaDao().update(encomenda);
        }
        for (Encomenda encomenda : encomendasToUpdateServerId) {
          session.getEncomendaDao().update(encomenda);
        }
      }
    });
  }

  public void sincronizar() {
    SQLiteDatabase db = App.get().getOpenHelper(context).getWritableDatabase();
    DaoMaster daoMaster = new DaoMaster(db);

    session = daoMaster.newSession();
    sincronizaBdFromServer();

    db.close();
  }


  public void setEncomendasToUpdateServerId(List<Encomenda> encomendasToUpdateServerId) {
    this.encomendasToUpdateServerId = encomendasToUpdateServerId;
  }

  public void setGetMinhasEncomendasRest(GetMinhasEncomendasRest getMinhasEncomendasRest) {
    this.getMinhasEncomendasRest = getMinhasEncomendasRest;
  }
}
