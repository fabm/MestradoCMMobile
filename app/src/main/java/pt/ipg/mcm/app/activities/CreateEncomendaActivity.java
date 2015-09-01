package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import pt.ipg.mcm.app.R;


public class CreateEncomendaActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_encomenda);

    Button btSelecionarProduto = (Button) findViewById(R.id.aceBtSelecionarProduto);
    btSelecionarProduto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(CreateEncomendaActivity.this,SelecionarProdutoActivity.class);
        startActivity(intent);
      }
    });
  }
}
