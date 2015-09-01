package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.adapters.ListAdapterEncomendas;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.grouped.resources.Constants;

public class MinhasEncomendasActivity extends Activity{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.minhas_encomendas_activity);

    ListView listView = (ListView) findViewById(R.id.meaLv);
    final ListAdapterEncomendas adapter = new ListAdapterEncomendas(this);
    listView.setAdapter(adapter);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Encomenda item = ((Encomenda) adapter.getItem(position));
        Intent intent = new Intent(MinhasEncomendasActivity.this,DetalheMinhaEncomendaActivity.class);
        intent.putExtra(Constants.Encomenda.ID,item.getId());
        startActivity(intent);
      }
    });
  }
}
