package pt.ipg.mcm.app.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import pt.ipg.mcm.app.assync.AsyncOfflineTables;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.instances.App;

import static android.content.Context.MODE_PRIVATE;

public class SynchronizationAdapter {
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;


    public SynchronizationAdapter(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences(Constants.SHARED_SYNC, MODE_PRIVATE);
    }

    public String getSharedPassword() {
        return sharedPreferences.getString("password", "");
    }

    public String getSharedLogin() {
        return sharedPreferences.getString("login", "");
    }

    public boolean guardarPassword() {
        return sharedPreferences.getBoolean(Constants.User.GUARDAR_LOGIN_PASSWORD, false);
    }

    public long syncIndex() {
        return sharedPreferences.getLong(Constants.SYNC_INDEX, 0);
    }

    public void authenticationValuesSave(String login, String password, boolean save) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login", login);
        editor.putString("password", password);
        editor.putBoolean(Constants.User.GUARDAR_LOGIN_PASSWORD, true);
        editor.commit();
    }

    public void sync() {
        new AsyncOfflineTables(activity, syncIndex()) {

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
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
                progressDialog.dismiss();
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
        }.execute(getSharedLogin(), getSharedPassword());
    }

    protected void callOnError(String error) {
        alerteMessage(error, false);
    }

    protected void callOnSuccess() {
        alerteMessage("Sincronização feita com sucesso", true);
    }

    private void alerteMessage(String message, final boolean finish) {
        AlertDialog.Builder alertBuilder = App.get().getAlertDialogBuilder(activity);
        alertBuilder.setMessage(message);
        AlertDialog alertDialog = alertBuilder.create();
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (finish) {
                    dialog.dismiss();
                    activity.finish();
                }
            }
        };
        String ok = activity.getString(android.R.string.ok);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, ok, listener);
        alertDialog.show();
    }
}
