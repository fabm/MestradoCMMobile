package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.adapters.ListAdapterEncomendaProduto;
import pt.ipg.mcm.app.adapters.ListAdapterEncomendas;
import pt.ipg.mcm.app.adapters.ListAdapterProdutos;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.grouped.resources.Constants;

public class DetalheMinhaEncomendaActivity extends Activity{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.detalhe_minha_encomenda_activity);

    ListView listView = (ListView) findViewById(R.id.dmeaLv);
    long idEncomenda = getIntent().getLongExtra(Constants.Encomenda.ID, -1);
    final ListAdapterEncomendaProduto adapter = new ListAdapterEncomendaProduto(this,idEncomenda);
    listView.setAdapter(adapter);
  }
}
