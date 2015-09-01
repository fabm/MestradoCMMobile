package pt.ipg.mcm;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class Generator {


  public static void main(String args[]) throws Exception {
    Generator generator = new Generator();
    generator.generate(args[0], args[1]);
  }

  private void generate(String str0, String str1) throws Exception {
    String path = str0;
    String pac = str1;

    Schema schema = new Schema(3, pac);
    schema.enableKeepSectionsByDefault();
    schema.enableActiveEntitiesByDefault();

    Entity produto = schema.addEntity("Produto");
    produto.addIdProperty();
    produto.addStringProperty("nome");
    produto.addIntProperty("precoActual");
    produto.addByteArrayProperty("foto");
    produto.addBooleanProperty("sync");
    Property fKprodutoIdCategoria = produto.addLongProperty("idCategoria").getProperty();

    Entity categoria = schema.addEntity("Categoria");

    produto.addToMany(categoria,fKprodutoIdCategoria);
    categoria.addIdProperty();
    categoria.addStringProperty("nome");
    categoria.addStringProperty("descricao");

    Entity encomenda = schema.addEntity("Encomenda");
    encomenda.addIdProperty();
    encomenda.addDateProperty("dataEntrega");
    encomenda.addLongProperty("serverId");
    encomenda.addLongProperty("precoTotal");
    encomenda.addBooleanProperty("sync");

    Entity encomendaProduto = schema.addEntity("EncomendaProduto");
    encomendaProduto.addIdProperty();
    encomendaProduto.addIntProperty("quantidade");
    encomendaProduto.addIntProperty("precoUnitario");
    Property fKEncomendaProduto = encomendaProduto.addLongProperty("idEncomenda").getProperty();
    encomenda.addToMany(encomendaProduto,fKEncomendaProduto);


    Property fKEncomendaProdutoProduto = encomendaProduto.addLongProperty("idProduto").getProperty();
    encomendaProduto.addToOne(produto,fKEncomendaProdutoProduto);


    new DaoGenerator().generateAll(schema, path);
  }
}
