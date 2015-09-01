package pt.ipg.mcm.app.instances;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import pt.ipg.mcm.calls.RestJsonCall;

public interface DelegatedApp {

    ObjectMapper MAPPER = new ObjectMapper() {{
        configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);

        //disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
    }};


    ObjectMapper getObjectMapper();

    RestJsonCall.Builder getNewRestJsonCallBuilder();

    SQLiteOpenHelper getOpenHelper(Context context);

    AlertDialog.Builder getAlertDialogBuilder(Context context);

    SharedPreferences getSharedPreferences(Context context, String name);

    ProgressDialog getProgressDialog(Context context, CharSequence title, CharSequence message);

    DatePickerDialog getDatePicker(Context context, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth);
}
