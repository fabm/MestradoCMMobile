package pt.ipg.mcm.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocalidadesService extends Service {
  public LocalidadesService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }
}
