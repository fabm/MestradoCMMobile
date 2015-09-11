package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.adapters.ListAdapterEncomendaProduto;
import pt.ipg.mcm.app.adapters.SynchronizationAdapter;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.bd.EncomendaProdutoDao;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;

import static java.lang.String.format;
import static pt.ipg.mcm.app.grouped.resources.EstadoEncomenda.getEstado;
import static pt.ipg.mcm.app.util.Formatter.centsToEuros;

public class DetalheMinhaEncomendaActivity extends Activity {

    private long idEncomenda;
    private int estado;

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

        Button btCancelar = (Button) findViewById(R.id.dmeaBtCancelar);
        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToCancel();
            }
        });

        estado = getIntent().getIntExtra(Constants.Encomenda.ESTADO, 0);
        idEncomenda = getIntent().getLongExtra(Constants.Encomenda.ID, -1);


        if (estado == 0) {
            btCancelar.setText("Cancelar encomenda");
        } else {
            btCancelar.setText("Eliminar encomenda");
        }
        final ListAdapterEncomendaProduto adapter = new ListAdapterEncomendaProduto(this, idEncomenda);

        String dataCriacao = getIntent().getStringExtra(Constants.Encomenda.DATA_CRIACAO);
        TextView tvDataCriacao = (TextView) findViewById(R.id.dmeaVlDataCriacao);
        tvDataCriacao.setText(dataCriacao);


        TextView tvEstado = (TextView) findViewById(R.id.dmeaVlEstado);
        tvEstado.setText(getEstado(estado));


        listView.setAdapter(adapter);
    }

    private void clickToCancel() {
        AlertDialog.Builder alertBuilder = App.get().getAlertDialogBuilder(DetalheMinhaEncomendaActivity.this);

        alertBuilder.setMessage(format("Tem a certeza que deseja %s a encomenda?", estado != 0 ? "cancelar" : "eliminar"));
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
                cancelar();
            }
        };
        String ok = getString(android.R.string.ok);
        String cancel = getString(android.R.string.cancel);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, ok, okListener);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, cancelListener);
        alertDialog.show();
    }

    private void cancelar() {
        if (estado != 0) {
            cancelarNoServidor();
        } else {
            eliminarNoAndroid();
        }
    }

    private void eliminarNoAndroid() {
        SQLiteDatabase db = App.get().getOpenHelper(this).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        final DaoSession session = daoMaster.newSession();

        session.runInTx(new Runnable() {
            @Override
            public void run() {
                session.getEncomendaProdutoDao().queryBuilder()
                        .where(EncomendaProdutoDao.Properties.IdEncomenda.eq(idEncomenda))
                        .buildDelete()
                        .executeDeleteWithoutDetachingEntities();
                session.getEncomendaDao().deleteByKey(idEncomenda);
            }
        });

        db.close();
        finish();
    }

    private void cancelarNoServidor() {
        SQLiteDatabase db = App.get().getOpenHelper(this).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        final DaoSession session = daoMaster.newSession();

        final Encomenda encomenda = session.getEncomendaDao().load(idEncomenda);
        encomenda.setEstado(4);
        encomenda.setSync(false);

        session.runInTx(new Runnable() {
            @Override
            public void run() {
                session.update(encomenda);
            }
        });

        db.close();

        SynchronizationAdapter synchronizationAdapter = new SynchronizationAdapter(this) {
            @Override
            protected void callOnSuccess() {
                super.callOnSuccess();
                finish();
            }

            @Override
            protected void callOnError(String error) {
                super.callOnError(error);
                finish();
            }
        };
        synchronizationAdapter.sync();
    }
}
