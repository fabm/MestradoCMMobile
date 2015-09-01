package pt.ipg.mcm.app.mock;

import android.app.DatePickerDialog;
import android.content.Context;

import java.util.Calendar;

public class DatePickerDialogMock extends DatePickerDialog {

  private OnDateSetListener callBack;

  public DatePickerDialogMock(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
    super(context, callBack, year, monthOfYear, dayOfMonth);
    this.callBack = callBack;
  }

  protected Calendar getCalendar(){
    return null;
  }

  @Override
  public void show() {
    callBack.onDateSet(null, getCalendar().get(Calendar.YEAR), getCalendar().get(Calendar.MONTH), getCalendar().get(Calendar.DAY_OF_MONTH));
  }

}
