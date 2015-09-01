package pt.ipg.mcm.app.controllers;

import pt.ipg.mcm.calls.client.Retorno;

public enum Erro implements Retorno {
  TECNICO(-1, "Problema técnico"),
  CAMPO_VAZIO(-2, "O campo ''{0}'' não pode ser vazio"),
  AUTENTICACAO(-3, "Não foi possível ligar ao servidor, problema de autenticação");

  private final int codigo;
  private final String mensagem;

  Erro(int codigo, String mensagem) {

    this.codigo = codigo;
    this.mensagem = mensagem;
  }

  @Override
  public int getCodigo() {
    return codigo;
  }

  @Override
  public String getMensagem() {
    return mensagem;
  }


}
