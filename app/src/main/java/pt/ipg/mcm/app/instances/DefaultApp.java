package pt.ipg.mcm.app.instances;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.calls.RestJsonCall;

public class DefaultApp implements DelegatedApp {


  @Override
  public ObjectMapper getObjectMapper() {
    return MAPPER;
  }

  @Override
  public RestJsonCall.Builder getNewRestJsonCallBuilder() {
    return new RestJsonCallDefault.Builder() {
      @Override
      protected RestJsonCallDefault abstractBuild(Request request) {
        return new RestJsonCallDefault(request);
      }
    };
  }

  @Override
  public SQLiteOpenHelper getOpenHelper(Context context) {
    return new DaoMaster.DevOpenHelper(context, "padaria", null);
  }

  @Override
  public AlertDialog.Builder getAlertDialogBuilder(Context context) {
    return new AlertDialog.Builder(context);
  }

  @Override
  public SharedPreferences getSharedPreferences(Context context, String name) {
    return context.getSharedPreferences(name, Context.MODE_PRIVATE);
  }

  @Override
  public ProgressDialog getProgressDialog(Context context, CharSequence title, CharSequence message) {
    return ProgressDialog.show(context, title, message, true);
  }

  @Override
  public DatePickerDialog getDatePicker(Context context, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
    return new DatePickerDialog(context, listener, year, monthOfYear, dayOfMonth);
  }
}
