package pt.ipg.mcm.app.assync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import pt.ipg.mcm.app.controllers.Erro;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.RestJsonCall;
import pt.ipg.mcm.calls.WebserviceException;
import pt.ipg.mcm.calls.client.Retorno;
import pt.ipg.mcm.calls.client.model.RetornoRest;
import pt.ipg.mcm.calls.client.model.user.CreateUserClientRestIn;

public abstract class AsyncUserSign extends AsyncTask<CreateUserClientRestIn, String, Retorno> {

  private Context context;

  public AsyncUserSign(Context context) {
    this.context = context;
  }

  @Override
  protected Retorno doInBackground(CreateUserClientRestIn... createUserClientRestInArray) {
    RestJsonCall call = App.get().getNewRestJsonCallBuilder()
        .serverUrl(Constants.SERVER_URL)
        .path("/services/rest/user/create")
        .post(createUserClientRestInArray)
        .auth(Constants.AUTH_BASIC_CONVIDADO)
        .build();
    try {
      RetornoRest retorno = call.getResponse(RetornoRest.class);

      if (retorno.getCodigo() != 1) {
        return retorno;
      }

      CreateUserClientRestIn createUserClientRestIn = createUserClientRestInArray[0];
      SharedPreferences.Editor edit = App.get().getSharedPreferences(context, "user").edit();
      edit.putString("login", createUserClientRestIn.getLogin());
      edit.putString("password", createUserClientRestIn.getPassword());
      edit.commit();
      return retorno;
    } catch (WebserviceException e) {
      return Erro.TECNICO;
    }
  }

  protected abstract void onPostExecute();
  protected abstract void onPostExecute(String error);

  @Override
  protected void onPostExecute(Retorno retorno) {
    switch (retorno.getCodigo()) {
      case 1:
        onPostExecute();
        break;
      default:
        onPostExecute(retorno.getMensagem());
    }
  }
}
