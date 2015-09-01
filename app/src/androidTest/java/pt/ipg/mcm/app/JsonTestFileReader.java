package pt.ipg.mcm.app;

import com.google.gson.JsonObject;
import org.junit.Assert;
import pt.ipg.mcm.app.instances.App;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class JsonTestFileReader {
  private String path;

  public JsonTestFileReader(String path) {
    this.path = path;
  }

  public JsonObject getJsonObject() throws IOException {
    URL url = JsonTestFileReader.class.getResource(path);
    Assert.assertNotNull("Object url is null to path:"+path,url);
    return App.get().getGson().fromJson(new InputStreamReader(url.openStream()), JsonObject.class);
  }

}
