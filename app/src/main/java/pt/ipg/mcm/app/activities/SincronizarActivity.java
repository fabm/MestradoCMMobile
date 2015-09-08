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
import pt.ipg.mcm.app.adapters.SynchronizationAdapter;
import pt.ipg.mcm.app.assync.AsyncOfflineTables;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SincronizarActivity extends Activity {
  private CheckBox guardarCheckBox;
  private SynchronizationAdapter synchronizationAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sync);
    synchronizationAdapter = new SynchronizationAdapter(this);

    final EditText loginEditText = (EditText) findViewById(R.id.syncEtLogin);

    final EditText passwordEditText = (EditText) findViewById(R.id.syncEtPassword);
    guardarCheckBox = (CheckBox) findViewById(R.id.syncCbGuardar);
    guardarCheckBox.setChecked(synchronizationAdapter.guardarPassword());

    String loginStr = synchronizationAdapter.getSharedLogin();
    if (!loginStr.isEmpty()) {
      loginEditText.setText(loginStr);
      passwordEditText.setText(synchronizationAdapter.getSharedPassword());
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
        if (guardarCheckBox.isChecked()) {
          synchronizationAdapter.authenticationValuesSave(login,password,true);
        }
        synchronizationAdapter.sync();
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

}
