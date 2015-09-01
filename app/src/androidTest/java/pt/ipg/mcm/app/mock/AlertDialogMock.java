package pt.ipg.mcm.app.mock;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;

public class AlertDialogMock extends AlertDialog{
  protected AlertDialogMock(Context context) {
    super(context);
  }

  @Override
  public void show() {
    //mock
  }
  public static class Builder extends AlertDialog.Builder{
    private Context context;

    public Builder(Context context) {
      super(context);
      this.context = context;
    }

    @NonNull
    @Override
    public AlertDialog create() {
      return new AlertDialogMock(context);
    }
  }
}
