package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.adapters.ListAdapterEncomendas;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.grouped.resources.Constants;

import static pt.ipg.mcm.app.util.Formatter.dateSimpleToString;

public class MinhasEncomendasActivity extends Activity {

    private ListView listView;
    private ListAdapterEncomendas adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.minhas_encomendas_activity);

        listView = (ListView) findViewById(R.id.meaLv);
        adapter = new ListAdapterEncomendas(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Encomenda item = ((Encomenda) adapter.getItem(position));
                Intent intent = new Intent(MinhasEncomendasActivity.this, DetalheMinhaEncomendaActivity.class);
                intent.putExtra(Constants.Encomenda.ID, item.getId());
                intent.putExtra(Constants.Encomenda.TOTAL, item.getPrecoTotal());
                intent.putExtra(Constants.Encomenda.ESTADO, item.getEstado());
                intent.putExtra(Constants.Encomenda.OBSERVACOES, item.getObservacoes());
                if (item.getDataCriacao() == null) {
                    intent.putExtra(Constants.Encomenda.DATA_CRIACAO, "--");
                } else {
                    intent.putExtra(Constants.Encomenda.DATA_CRIACAO, dateSimpleToString(item.getDataCriacao()));
                }
                intent.putExtra(Constants.Encomenda.DATA_ENTREGA, dateSimpleToString(item.getDataEntrega()));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        adapter.load();
        adapter.notifyDataSetChanged();
        super.onResume();
    }
}
