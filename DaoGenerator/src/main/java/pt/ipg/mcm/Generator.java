package pt.ipg.mcm;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
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
    produto.addIntProperty("sync");

    new DaoGenerator().generateAll(schema, path);
  }
}
