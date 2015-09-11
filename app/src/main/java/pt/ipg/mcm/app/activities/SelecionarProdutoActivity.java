package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.adapters.ListAdapterProdutos;
import pt.ipg.mcm.app.bd.Produto;

import java.math.BigDecimal;


public class SelecionarProdutoActivity extends Activity {

    private Produto selecionado;
    private int quantidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecionar_produto);

        final ListAdapterProdutos listAdapterProdutos = new ListAdapterProdutos(this);

        EditText etQuantidade = (EditText) findViewById(R.id.spaEtQuantidadeValue);
        etQuantidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    quantidade = Integer.parseInt(s.toString());
                    findViewById(R.id.spaBtOk).setEnabled(true);
                } catch (NumberFormatException e) {
                    findViewById(R.id.spaBtOk).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ListView listView = (ListView) findViewById(R.id.aspLv);
        listView.setAdapter(listAdapterProdutos);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selecionado = listAdapterProdutos.getProduto(position);
                TextView tvNome = (TextView) findViewById(R.id.sapTvNome);
                tvNome.setText(selecionado.getNome());
                TextView tvPreco = (TextView) findViewById(R.id.sapTvPreco);
                tvPreco.setText(new BigDecimal(
                        selecionado.getPrecoActual())
                        .divide(new BigDecimal(100)).toString() + "€");
                findViewById(R.id.spaTable).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.spaBtOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduto();
            }
        });
    }

    private void addProduto(){
        Intent intent = new Intent();
        intent.putExtra("idProduto",selecionado.getId());
        intent.putExtra("quantidade",quantidade);
        setResult(RESULT_OK,intent);
        finish();
    }

}
