package pt.ipg.mcm.app.mock;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogMock extends ProgressDialog{
  public ProgressDialogMock(Context context) {
    super(context);
  }

  @Override
  public void show() {
    //do nothing
  }
}
