package pt.ipg.mcm.app.instances;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowPreferenceManager;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.call.RestJsonCallMock;
import pt.ipg.mcm.calls.RestJsonCall;

public class DelegatedApp4RETests implements DelegatedApp {


  private RestJsonCallMock restJsonCallMock;


  @Override
  public ObjectMapper getObjectMapper() {
    return DelegatedApp.MAPPER;
  }

  @Override
  public RestJsonCall.Builder getNewRestJsonCallBuilder() {
    if (restJsonCallMock == null) {
      return new RestJsonCallDefault.Builder();
    } else {
      RestJsonCallMock.Builder builder = new RestJsonCallMock.Builder();
      builder.setRestJsonMock(restJsonCallMock);
      return builder;
    }
  }

  @Override
  public SQLiteOpenHelper getOpenHelper(Context context) {
    return new DaoMaster.DevOpenHelper(context, "test", null);
  }

  @Override
  public AlertDialog.Builder getAlertDialogBuilder(Context context) {
    return null;
  }

  @Override
  public SharedPreferences getSharedPreferences(Context context, String name) {
    return ShadowPreferenceManager.getDefaultSharedPreferences(Robolectric.application.getApplicationContext());
  }

  @Override
  public ProgressDialog getProgressDialog(Context context, CharSequence title, CharSequence message) {
    return null;
  }


  @Override
  public DatePickerDialog getDatePicker(Context context, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
    return null;
  }

  public void setRestJsonCallMock(RestJsonCallMock restJsonCallMock) {
    this.restJsonCallMock = restJsonCallMock;
  }
}
