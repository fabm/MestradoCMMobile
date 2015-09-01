package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.adapters.ListAdapterLocalidades;
import pt.ipg.mcm.app.assync.AsyncLocalidades;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.WebserviceException;
import pt.ipg.mcm.calls.client.model.localidades.LocalidadeRest;

import java.util.ArrayList;
import java.util.List;

public class LocalidadeActivity extends Activity {

  public static final String TAG = "localidadeActivity";
  private AsyncLocalidades asyncLocalidades;
  private ListAdapterLocalidades localidadesAdapter;
  private ListView localidadesView;


  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.activity_localidades);

    createAsyncLocalidades(1, "");

    localidadesView = (ListView) findViewById(R.id.lvLocalidades);
    final EditText etFilter = (EditText) findViewById(R.id.etLocalidadesFilter);
    etFilter.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        asyncLocalidades.cancel(true);
        createAsyncLocalidades(1, s.toString());
        localidadesView.invalidateViews();
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbLocalidade);
        progressBar.setVisibility(View.VISIBLE);
      }
    });

    final List<LocalidadeRest> localidadeList = new ArrayList<>();

    localidadesAdapter = new ListAdapterLocalidades(localidadeList, this);

    localidadesView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (view.getLastVisiblePosition() == localidadesAdapter.getCount() - 1) {
          proximaPaginaAsyncLocalidades();
          Log.v(TAG, "end scroll");
          Log.v(TAG, "y" + view.getHeight());
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
      }
    });
    localidadesView.setAdapter(localidadesAdapter);

    localidadesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LocalidadeRest item = (LocalidadeRest) localidadesAdapter.getItem(position);
        Intent intentForResult = new Intent();
        intentForResult.putExtra(Constants.Localidade.CODIGO_POSTAL, item.getCodPostal());
        intentForResult.putExtra(Constants.Localidade.LOCALIDADE, item.getLocalidade());
        intentForResult.putExtra(Constants.Localidade.ID, item.getId());
        setResult(RESULT_OK, intentForResult);
        finish();
      }
    });

  }

  private void proximaPaginaAsyncLocalidades() {
    Log.v(TAG, "atualiza pagina");
    if (asyncLocalidades.isPaginaCompleta()) {
      Log.v(TAG, "pagina atual:" + asyncLocalidades.getPage());
      createAsyncLocalidades(asyncLocalidades.getPage() + 1, asyncLocalidades.getFilter());
    }
  }

  private void createAsyncLocalidades(final int page, String filter) {
    asyncLocalidades = new AsyncLocalidades() {

      @Override
      protected void onPostExecute(List<LocalidadeRest> localidades) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbLocalidade);
        progressBar.setVisibility(View.GONE);
        if (page > 1) {
          addToList(localidades);
        } else {
          updateList(localidades);
        }
      }

      @Override
      protected void onCancelled() {
        if (cancelException instanceof WebserviceException) {
          App.get().getAlertDialogBuilder(LocalidadeActivity.this)
              .setMessage("Não foi possivel ligar ao servidor")
              .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  LocalidadeActivity.this.finish();
                }
              })
              .create()
              .show();
        }
      }
    };

    asyncLocalidades.execute(page, filter);
  }

  private void addToList(List<LocalidadeRest> localidades) {
    localidadesAdapter.addData(localidades);
  }

  private void updateList(final List<LocalidadeRest> localidades) {
    localidadesAdapter.updateData(new ArrayList<>(localidades));
  }

}
