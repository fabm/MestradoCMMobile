package pt.ipg.mcm.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.instances.DelegatedApp;
import pt.ipg.mcm.app.mock.AlertDialogMock;
import pt.ipg.mcm.app.mock.DatePickerDialogMock;
import pt.ipg.mcm.app.mock.ProgressDialogMock;
import pt.ipg.mcm.app.mock.SharedPreferenciesMock;
import pt.ipg.mcm.app.mock.rest.call.RestJsonCallMock;

import java.util.Calendar;

public class DelegatedApp4Tests implements DelegatedApp {

    private RestJsonCallMock.Builder restJsonCallBuilder;
    private SharedPreferenciesMock sharedPreferences;
    private Calendar calendar;

    public DelegatedApp4Tests() {
        sharedPreferences = new SharedPreferenciesMock();
    }


    @Override
    public ObjectMapper getObjectMapper() {
        return MAPPER;
    }

    @Override
    public RestJsonCallMock.Builder getNewRestJsonCallBuilder() {
        return restJsonCallBuilder;
    }

    @Override
    public SQLiteOpenHelper getOpenHelper(Context context) {
        return new DaoMaster.DevOpenHelper(context, "test", null);
    }

    @Override
    public AlertDialog.Builder getAlertDialogBuilder(Context context) {
        return new AlertDialogMock.Builder(context);
    }

    @Override
    public SharedPreferences getSharedPreferences(Context context, String name) {
        return sharedPreferences;
    }

    @Override
    public ProgressDialog getProgressDialog(Context context, CharSequence title, CharSequence message) {
        return new ProgressDialogMock(context);
    }


    @Override
    public DatePickerDialogMock getDatePicker(Context context, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        return new DatePickerDialogMock(context, listener, year, monthOfYear, dayOfMonth) {
            @Override
            protected Calendar getCalendar() {
                return calendar;
            }
        };
    }

    public void setCalendar2DatePicker(Calendar calendar) {
        this.calendar = calendar;
    }


    public void setRestJsonCallBuilder(RestJsonCallMock.Builder restJsonCallBuilder) {
        this.restJsonCallBuilder = restJsonCallBuilder;
    }
}
