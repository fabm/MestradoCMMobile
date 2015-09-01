package pt.ipg.mcm.app.instances;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class App {
  static {
    InputStream is = App.class.getResourceAsStream("conf.properties");
    Properties properties = new Properties();
    try {
      properties.load(is);
      String envProp = properties.getProperty("env");
      if ("prod".equals(envProp)) {
        delegatedApp = new DefaultApp();
      } else if ("test".equals(envProp)) {
        String delegatedAppPath = properties.getProperty("test-class");
        delegatedApp = (DelegatedApp) App.class.getClassLoader()
            .loadClass(delegatedAppPath)
            .newInstance();
      }
    } catch (IOException e) {
      throw new IllegalStateException(impossibleLoadClassMessage(), e);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(impossibleLoadClassMessage(), e);
    } catch (InstantiationException e) {
      throw new IllegalStateException(impossibleLoadClassMessage(), e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(impossibleLoadClassMessage(), e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
        }
      }
    }

  }


  public static final String APP = "App";

  private static DelegatedApp delegatedApp;

  private static String impossibleLoadClassMessage() {
    return "Impossible to load class that implements DelegatedApp";
  }

  public static DelegatedApp get() {
    Log.v(APP,delegatedApp.getClass().toString());
    return delegatedApp;
  }


}
