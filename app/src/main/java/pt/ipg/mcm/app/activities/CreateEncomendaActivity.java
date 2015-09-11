package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.adapters.EncomendaProdutoListaAdapter;
import pt.ipg.mcm.app.bd.*;
import pt.ipg.mcm.app.instances.App;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CreateEncomendaActivity extends Activity {
    EncomendaProdutoListaAdapter encomendaProdutoListaAdapter;
    List<EncomendaProduto> encomendaProdutoList;
    private SwipeListView swipeListView;
    private Button btOk;
    private Long idEncomenda;
    private Date dataEntrega;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_encomenda);
        encomendaProdutoList = new ArrayList<>();
        encomendaProdutoListaAdapter = new EncomendaProdutoListaAdapter(this, encomendaProdutoList);

        Button btSelecionarProduto = (Button) findViewById(R.id.aceBtSelecionarProduto);
        btSelecionarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEncomendaActivity.this, SelecionarProdutoActivity.class);
                startActivityForResult(intent, 4);
            }
        });

        btOk = (Button) findViewById(R.id.aceBtOkEncomendas);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar ci = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEncomendaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ci.set(year,monthOfYear,dayOfMonth);
                        saveEncomenda(ci.getTime());
                    }
                },ci.get(Calendar.YEAR),ci.get(Calendar.MONTH),ci.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });


        swipeListView = (SwipeListView) findViewById(R.id.ceLvEncomendas);
        swipeListView.setAdapter(encomendaProdutoListaAdapter);


        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    encomendaProdutoList.remove(position);
                }
                changeList();
            }
        });

    }

    private void saveEncomenda(Date dataEntrega) {
        if(idEncomenda == null){
            createEncomenda(dataEntrega);
        }else {
            updateEncomenda(dataEntrega);
        }
        finish();
    }

    private void createEncomenda(final Date dataEntrega) {
        SQLiteDatabase db = App.get().getOpenHelper(this).getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        final DaoSession session = daoMaster.newSession();

        session.runInTx(new Runnable() {
            @Override
            public void run() {
                Encomenda encomenda = new Encomenda();
                encomenda.setDataEntrega(dataEntrega);
                encomenda.setSync(false);
                encomenda.setEstado(0);
                session.getEncomendaDao().insert(encomenda);

                Long total = 0L;

                for(EncomendaProduto encomendaProduto:encomendaProdutoList){
                    encomendaProduto.setIdEncomenda(encomenda.getId());
                    session.getEncomendaProdutoDao().insert(encomendaProduto);
                    total += session.getProdutoDao().load(encomendaProduto.getIdProduto()).getPrecoActual();
                }

                encomenda.setPrecoTotal(total);
                session.getEncomendaDao().update(encomenda);
            }
        });

        db.close();
    }

    private void updateEncomenda(Date dataEntrega) {
        SQLiteDatabase db = App.get().getOpenHelper(this).getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();


        db.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 4:
                long id = data.getLongExtra("idProduto", -1);
                int qtd = data.getIntExtra("quantidade", 0);

                SQLiteDatabase db = App.get().getOpenHelper(this).getReadableDatabase();
                DaoMaster daoMaster = new DaoMaster(db);
                DaoSession session = daoMaster.newSession();
                Produto produto = session.getProdutoDao().load(id);

                EncomendaProduto encomendaProduto = new EncomendaProduto();
                encomendaProduto.setProduto(produto);
                encomendaProduto.setPrecoUnitario(produto.getPrecoActual());
                encomendaProduto.setQuantidade(qtd);

                encomendaProdutoList.add(encomendaProduto);
                changeList();
                db.close();
                break;
            default:
        }
    }

    private void changeList() {
        encomendaProdutoListaAdapter.notifyDataSetChanged();
        btOk.setEnabled(!encomendaProdutoList.isEmpty());
    }
}
