package pt.ipg.mcm.app.rest.model;

import java.util.List;

public class ResCategoria {
  private List<CategoriaJS> categorias;

  public List<CategoriaJS> getCategorias() {
    return categorias;
  }

  public void setCategorias(List<CategoriaJS> categorias) {
    this.categorias = categorias;
  }
}
