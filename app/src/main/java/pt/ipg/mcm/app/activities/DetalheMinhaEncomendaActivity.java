package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.adapters.ListAdapterEncomendaProduto;
import pt.ipg.mcm.app.adapters.ListAdapterEncomendas;
import pt.ipg.mcm.app.adapters.ListAdapterProdutos;
import pt.ipg.mcm.app.adapters.SynchronizationAdapter;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.grouped.resources.EstadoEncomenda;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.app.util.Formatter;

import java.util.Date;

import static pt.ipg.mcm.app.grouped.resources.EstadoEncomenda.getEstado;
import static pt.ipg.mcm.app.util.Formatter.centsToEuros;

public class DetalheMinhaEncomendaActivity extends Activity {

    private long idEncomenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.detalhe_minha_encomenda_activity);

        ListView listView = (ListView) findViewById(R.id.dmeaLv);

        long total = getIntent().getLongExtra(Constants.Encomenda.TOTAL, 0);
        TextView tvTotal = (TextView) findViewById(R.id.dmeaVlTotal);
        tvTotal.setText(centsToEuros(total));

        String dataEntrega = getIntent().getStringExtra(Constants.Encomenda.DATA_ENTREGA);
        TextView tvDataEntrega = (TextView) findViewById(R.id.dmeaVlDataEntrega);
        tvDataEntrega.setText(dataEntrega);

        idEncomenda = getIntent().getLongExtra(Constants.Encomenda.ID, -1);
        final ListAdapterEncomendaProduto adapter = new ListAdapterEncomendaProduto(this, idEncomenda);

        String dataCriacao = getIntent().getStringExtra(Constants.Encomenda.DATA_CRIACAO);
        TextView tvDataCriacao = (TextView) findViewById(R.id.dmeaVlEstado);
        tvDataCriacao.setText(dataCriacao);

        int estado = getIntent().getIntExtra(Constants.Encomenda.ESTADO, 0);
        TextView tvEstado = (TextView) findViewById(R.id.dmeaVlEstado);
        tvEstado.setText(getEstado(estado));

        Button buttonCancelar = (Button) findViewById(R.id.dmeaBtCancelar);
        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToCancel();
            }
        });

        listView.setAdapter(adapter);
    }

    private void clickToCancel() {
        AlertDialog.Builder alertBuilder = App.get().getAlertDialogBuilder(DetalheMinhaEncomendaActivity.this);
        alertBuilder.setMessage("Tem a certeza que deseja cancelar a encomenda?");
        AlertDialog alertDialog = alertBuilder.create();

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sync();
            }
        };
        String ok = getString(android.R.string.ok);
        String cancel = getString(android.R.string.cancel);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, ok, okListener);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, cancelListener);
        alertDialog.show();
    }

    private void sync(){
        SQLiteDatabase db = App.get().getOpenHelper(this).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        final DaoSession session = daoMaster.newSession();

        final Encomenda encomenda = session.getEncomendaDao().load(idEncomenda);
        encomenda.setEstado(5);
        encomenda.setSync(false);

        session.runInTx(new Runnable() {
            @Override
            public void run() {
                session.update(encomenda);
            }
        });

        db.close();
        SynchronizationAdapter synchronizationAdapter = new SynchronizationAdapter(this);
        synchronizationAdapter.sync();
    }
}
