package pt.ipg.mcm.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.assync.AsyncUserSign;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.client.DateHelper;
import pt.ipg.mcm.calls.client.model.CheckException;
import pt.ipg.mcm.calls.client.model.user.CreateUserClientRestIn;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class UserRegisterActivity extends Activity {

  public static final String TAG = UserRegisterActivity.class.getName();
  private CreateUserClientRestIn createUserClientRestIn;
  private ProgressDialog progressDialog;
  private AsyncUserSign asyncUserSign;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_register_activity);
    final EditText localidadeEt = (EditText) findViewById(R.id.etLocalidadeRegisto);
    View.OnClickListener onClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectLocalidade();
      }
    };

    localidadeEt.setOnClickListener(onClickListener);
    ImageView ivLocalidade = (ImageView) findViewById(R.id.ivLocalidade);
    ivLocalidade.setOnClickListener(onClickListener);


    final EditText etDataNascimento = (EditText) findViewById(R.id.etDataNascimentoRegisto);
    etDataNascimento.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectDataNascimeto();
      }
    });

    final Button btOk = (Button) findViewById(R.id.btOkRegistar);

    btOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        registar();
      }
    });
    createUserClientRestIn = new CreateUserClientRestIn();
  }

  private void registar() {
    try {
      createUserClientRestIn.setAndCheckNome(getTextFromEditText(R.id.etNomeRegisto));
      createUserClientRestIn.checkDataDeNascimento();
      createUserClientRestIn.setAndCheckMorada(getTextFromEditText(R.id.etMoradaRegisto));
      createUserClientRestIn.setAndCheckPorta(getTextFromEditText(R.id.etNumPortaRegisto));
      createUserClientRestIn.checkLocalidade();
      createUserClientRestIn.setAndCheckContacto(getTextFromEditText(R.id.etContactoRegisto));
      createUserClientRestIn.setAndCheckEmail(getTextFromEditText(R.id.etMailRegisto));
      createUserClientRestIn.setAndCheckContribuinte(getTextFromEditText(R.id.etContribuinteRegisto));
      createUserClientRestIn.setAndCheckLogin(getTextFromEditText(R.id.etLoginRegisto));
      createUserClientRestIn.setAndCheckPassword(getTextFromEditText(R.id.etPasswordRegisto));
      asyncUserSign = new AsyncUserSign(this){

        @Override
        protected void onPostExecute() {
          callOnSuccess();
        }

        @Override
        protected void onPostExecute(String error) {
          callOnError(error);
        }
      };
      asyncUserSign.execute(createUserClientRestIn);
      progressDialog = App.get().getProgressDialog(this, "Aguarde...", "Registando utilizador");
    } catch (CheckException e) {
      errorDialog(e.getMessage());
    }
  }

  private void errorDialog(String errorMessage) {
    Log.v(UserRegisterActivity.class.getName(),errorMessage==null?"nulo":errorMessage);
    final AlertDialog alertDialog = App.get()
        .getAlertDialogBuilder(this)
        .setMessage(errorMessage)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();

    alertDialog.show();
  }

  private String getTextFromEditText(@IdRes int id) {
    return ((EditText) findViewById(id)).getText().toString();
  }

  private void selectDataNascimeto() {
    Calendar calendar = GregorianCalendar.getInstance();
    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        EditText etDataNascimento = (EditText) findViewById(R.id.etDataNascimentoRegisto);
        etDataNascimento.setText(String.format("%02d-%02d-%4d", dayOfMonth, monthOfYear, year));
        createUserClientRestIn.setDataNascimento(new DateHelper(DateHelper.Format.COMPACT).toString(year,monthOfYear,dayOfMonth));
      }
    };

    final int year = calendar.get(Calendar.YEAR);
    final int monthOfYear = calendar.get(Calendar.MONTH);
    final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    DatePickerDialog datePickerDialog = App.get().getDatePicker(this, listener, year, monthOfYear, dayOfMonth);

    datePickerDialog.show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      final EditText localidadeEt = (EditText) findViewById(R.id.etLocalidadeRegisto);
      Bundle extras = data.getExtras();
      int idLocalidade = extras.getInt(Constants.Localidade.ID);
      Log.v(TAG, "idLocalidade:" + extras.get(Constants.Localidade.ID).toString());
      int codigoPostal = extras.getInt(Constants.Localidade.CODIGO_POSTAL);
      String localidade = extras.getString(Constants.Localidade.LOCALIDADE);
      createUserClientRestIn.setLocalidade(idLocalidade);

      String codigoPostalString = String.valueOf(codigoPostal);

      localidadeEt.setText(localidade + ":" + codigoPostalString.substring(0, 4) + '-' + codigoPostalString.substring(4));
    }
  }

  private void selectLocalidade() {
    Intent intent = new Intent(this, LocalidadeActivity.class);
    startActivityForResult(intent, 1);
  }

  public void callOnError(String error) {
    errorDialog(error);
    asyncUserSign = null;
    progressDialog.dismiss();
    finish();
  }

  public void callOnSuccess() {
    asyncUserSign = null;
    progressDialog.dismiss();
    Intent intent = new Intent();
    intent.putExtra(Constants.User.LOGIN,createUserClientRestIn.getLogin());
    intent.putExtra(Constants.User.PASSWORD,createUserClientRestIn.getPassword());
    setResult(RESULT_OK,intent);
    finish();
  }
}
