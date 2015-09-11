package pt.ipg.mcm.app.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.AuthBasicUtf8;
import pt.ipg.mcm.calls.RestJsonCall;
import pt.ipg.mcm.calls.WebserviceException;
import pt.ipg.mcm.calls.client.model.RetornoRest;

public class LoginController {
    private final String host;
    private Context context;

  public LoginController(Context context) {
    this.context = context;
      host = App.get().getSharedPreferences(context, "host").getString("host", "");
  }

  public void remoteLogin(AuthBasicUtf8 authBasicUtf8) {
    RestJsonCall restJsonCall = App.get().getNewRestJsonCallBuilder()
        .serverUrl(host)
        .path("/services/rest/authentication/login")
        .get()
        .auth(authBasicUtf8)
        .build();

    try {
      restJsonCall.getResponse(RetornoRest.class);
    } catch (WebserviceException e) {
    }
  }

  private SharedPreferences getSharedPreferences() {
    return App.get().getSharedPreferences(context, "user");
  }

  public boolean isAuthenticated() {
    return getSharedPreferences().contains("login");
  }

  public boolean tryConnect(boolean saveInPreferences, AuthBasicUtf8 authBasicUtf8) {
    RestJsonCall restJsonCall = App.get().getNewRestJsonCallBuilder()
        .serverUrl(host)
        .path("/services/rest/authentication/login")
        .get()
        .auth(authBasicUtf8)
        .build();
    try {
      restJsonCall.getResponse(RetornoRest.class);
      if (saveInPreferences) {
        getSharedPreferences()
            .edit()
            .putString("login", authBasicUtf8.getLogin())
            .putString("password", authBasicUtf8.getPassword())
            .commit();
      }
      return true;
    } catch (WebserviceException e) {
      return false;
    }
  }
}
