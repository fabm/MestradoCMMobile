package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.assync.AsyncOfflineTables;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SincronizarActivity extends Activity implements ActivityListener<Boolean> {
  private AsyncOfflineTables asyncOfflineTables;
  private CheckBox guardarCheckBox;
  private SharedPreferences sharedPreferences;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    sharedPreferences = getSharedPreferences(Constants.SHARED_SYNC, MODE_PRIVATE);
    setContentView(R.layout.activity_sync);


    final EditText loginEditText = (EditText) findViewById(R.id.syncEtLogin);

    final EditText passwordEditText = (EditText) findViewById(R.id.syncEtPassword);
    guardarCheckBox = (CheckBox) findViewById(R.id.syncCbGuardar);
    guardarCheckBox.setChecked(sharedPreferences.getBoolean(Constants.User.GUARDAR_LOGIN_PASSWORD, false));

    String loginStr = sharedPreferences.getString("login", "");
    if (!loginStr.isEmpty()) {
      loginEditText.setText(loginStr);
      passwordEditText.setText(sharedPreferences.getString("password", ""));
    }


    Button btRegistarSincronizar = (Button) findViewById(R.id.btRegistarSincronizar);

    btRegistarSincronizar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intentRegisto = new Intent(SincronizarActivity.this, UserRegisterActivity.class);
        startActivityForResult(intentRegisto, 1);
      }
    });

    findViewById(R.id.syncBtCancel).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SincronizarActivity.this.finish();
      }
    });

    findViewById(R.id.syncBtSync).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        String login = loginEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        long sync = sharedPreferences.getLong(Constants.SYNC_INDEX, 0);
        if (guardarCheckBox.isChecked()) {
          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putString("login", login);
          editor.putString("password", password);
          editor.putBoolean(Constants.User.GUARDAR_LOGIN_PASSWORD, true);
          editor.commit();
        }

        asyncOfflineTables = new AsyncOfflineTables(SincronizarActivity.this, sync) {

          @Override
          protected void onPreExecute() {
            progressDialog = new ProgressDialog(SincronizarActivity.this);
            progressDialog.setMessage("A sincronizar");
            progressDialog.setProgress(0);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
          }

          @Override
          protected void onPostExecute(long sync) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(Constants.SYNC_INDEX, sync);
            editor.commit();
            callOnSuccess();
          }

          @Override
          protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
          }

          @Override
          protected void onPostExecute(String error) {
            progressDialog.dismiss();
            callOnError(error);
          }
        };
          asyncOfflineTables.execute(login, password);

      }
    });
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      String login = data.getStringExtra(Constants.User.LOGIN);
      EditText etLogin = (EditText) findViewById(R.id.syncEtLogin);
      etLogin.setText(login);

      String password = data.getStringExtra(Constants.User.PASSWORD);
      EditText etPassword = (EditText) findViewById(R.id.syncEtPassword);
      etPassword.setText(password);
    }
  }

  @Override
  public void call(Boolean finalizar) {

    if (finalizar) {
      if (!guardarCheckBox.isChecked()) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login", "");
        editor.putString("password", "");
        editor.commit();
      }
      finish();
    }
  }

  public void callOnError(String error) {
    alerteMessage(error, false);
  }

  public void callOnSuccess() {
    alerteMessage("Sincronização feita com sucesso", true);
  }

  private void alerteMessage(String message, final boolean finish) {
    AlertDialog.Builder alertBuilder = App.get().getAlertDialogBuilder(SincronizarActivity.this);
    alertBuilder.setMessage(message);
    AlertDialog alertDialog = alertBuilder.create();
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (finish) {
          dialog.dismiss();
          finish();
        }
      }
    };
    String ok = getString(android.R.string.ok);
    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, ok, listener);
    alertDialog.show();
  }
}
