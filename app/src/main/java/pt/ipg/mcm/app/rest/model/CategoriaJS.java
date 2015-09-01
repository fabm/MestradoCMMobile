package pt.ipg.mcm.app.rest.model;

import pt.ipg.mcm.app.bd.Categoria;

public class CategoriaJS extends Categoria {
  private Long id;
  private String nome;
  private String descricao;

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
