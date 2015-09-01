package pt.ipg.mcm.app.assync;

import android.os.AsyncTask;
import android.util.Log;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.WebserviceException;
import pt.ipg.mcm.calls.client.model.localidades.GetLocalidadeRest;
import pt.ipg.mcm.calls.client.model.localidades.LocalidadeRest;

import java.util.Arrays;
import java.util.List;

public class AsyncLocalidades extends AsyncTask<Object, String, List<LocalidadeRest>> {

  public static final String TAG = "asyncLocalidades";
  protected Exception cancelException;
  private List<LocalidadeRest> localidades;
  private String filter;
  private boolean paginaCompleta;
  private static final int MAX = 50;
  private int page;


  public AsyncLocalidades() {
  }


  public int getPage() {
    return page;
  }


  public List<LocalidadeRest> getLocalidades() {
    return localidades;
  }

  public String getFilter() {
    return filter;
  }

  @Override
  protected List<LocalidadeRest> doInBackground(Object... params) {
    page = (int) params[0];
    filter = (String) params[1];


    try {
      String lFilter = "";
      if (page == 1) {
        Thread.sleep(2000);
      }
      if (filter != null && !filter.isEmpty()) {
        Thread.sleep(2000);
        lFilter = "/" + filter;
      }
      String path = "/services/rest/localidade/filtro/" + page + lFilter;
      Log.v(TAG, "path:" + Constants.SERVER_URL + path);
      GetLocalidadeRest getLocalidadeRest = App.get().getNewRestJsonCallBuilder()
          .serverUrl(Constants.SERVER_URL)
          .path(path)
          .get()
          .build()
          .getResponse(GetLocalidadeRest.class);

      localidades = getLocalidadeRest.getLocalidadeRestList();

      Log.v(TAG, "size:" + localidades.size() + "filter:\"" + filter + "\"página:" + page);
      Log.v(TAG, Arrays.toString(localidades.toArray()));

      paginaCompleta = localidades.size() == MAX;

      return localidades;
    } catch (WebserviceException e) {
      cancelException = e;
      cancel(false);
      return null;
    } catch (InterruptedException e) {
      cancelException = e;
      cancel(false);
      return null;
    }
  }

  public boolean isPaginaCompleta() {
    return paginaCompleta;
  }
}
