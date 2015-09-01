package pt.ipg.mcm.app.assync;

import java.util.ArrayList;
import java.util.List;

public class DeletedEntities {
  public static final int PRODUTO = 0, CATEGORIA = 1, ENCOMENDA = 2;

  private List<Long> idsProdutos;
  private List<Long> idsCategorias;
  private List<Long> idsEncomendas;


  public DeletedEntities() {
    idsCategorias = new ArrayList<Long>();
    idsEncomendas = new ArrayList<Long>();
    idsProdutos = new ArrayList<Long>();
  }

  public List<Long> getIdsProdutos() {
    return idsProdutos;
  }

  public List<Long> getIdsCategorias() {
    return idsCategorias;
  }

  public List<Long> getIdsEncomendas() {
    return idsEncomendas;
  }

  public void registerDelete(String tabela, long id) {
    if (tabela.equals("CATEGORIA")) {
      getIdsCategorias().add(id);
    } else if (tabela.equals("PRODUTO")) {
      getIdsProdutos().add(id);
    } else if (tabela.equals("ENCOMENDA")) {
      getIdsEncomendas().add(id);
    }
  }
}
