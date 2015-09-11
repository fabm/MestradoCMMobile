package pt.ipg.mcm.app.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.Scheduler;
import pt.ipg.mcm.app.JsonTestFile;
import pt.ipg.mcm.app.RobolectricGradleTestRunner;
import pt.ipg.mcm.app.assync.AsyncLocalidades;
import pt.ipg.mcm.app.call.RestJsonCallMock;
import pt.ipg.mcm.app.common.AsyncExecutionType;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.app.instances.DelegatedApp4RETests;
import pt.ipg.mcm.calls.WebserviceException;
import pt.ipg.mcm.calls.client.model.localidades.LocalidadeRest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricGradleTestRunner.class)
public class LocalidadesTest {
  private DelegatedApp4RETests app;

  @Before
  public void setup() {
    if (app == null) {
      app = (DelegatedApp4RETests) App.get();
    }
  }

  @Test
  public void testLocalidades() throws InterruptedException {
    final Map<String, String> mapCalls = new HashMap<>();
    mapCalls.put("/services/rest/localidade/filtro/1/gua", "response1-localidade-filtrada.json");
    mapCalls.put("/services/rest/localidade/filtro/2/gua", "response2-localidade-filtrada.json");
    mapCalls.put("/services/rest/localidade/filtro/3/gua", "response3-localidade-filtrada.json");
    mapCalls.put("/services/rest/localidade/filtro/4/gua", "response4-localidade-filtrada.json");

    RestJsonCallMock restJsonCallMock = new RestJsonCallMock() {

      @Override
      public <T> T getResponse(Class<T> jeClass) throws WebserviceException {
        try {
          String res = mapCalls.get(path);
          if (res == null) {
            return (T) new JsonTestFile("response1-localidade.json").getRequestObject(jeClass);
          }
          return (T) new JsonTestFile(res).getRequestObject(jeClass);
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    };

    app.setRestJsonCallMock(restJsonCallMock);


    Scheduler backgroundScheduler = Robolectric.getBackgroundScheduler();

    backgroundScheduler.pause();

    AsyncLocalidades asyncLocalidades = getAsyncLocalidades(AsyncExecutionType.CANCEL);
    asyncLocalidades.execute(1, "g");
    asyncLocalidades.cancel(true);

    asyncLocalidades = getAsyncLocalidades(AsyncExecutionType.CANCEL);
    asyncLocalidades.execute(1, "gu");
    asyncLocalidades.cancel(true);

    asyncLocalidades = getAsyncLocalidades(AsyncExecutionType.SUCCESS);
    asyncLocalidades.execute(1, "gua");

    backgroundScheduler.advanceToLastPostedRunnable();
    Assert.assertEquals(1, asyncLocalidades.getPage());
    Assert.assertEquals(50, asyncLocalidades.getLocalidades().size());

    asyncLocalidades = getAsyncLocalidades(AsyncExecutionType.SUCCESS);
    asyncLocalidades.execute(2, "gua");

    backgroundScheduler.advanceToLastPostedRunnable();
    Assert.assertEquals(2, asyncLocalidades.getPage());
    Assert.assertEquals(50, asyncLocalidades.getLocalidades().size());


  }

  private AsyncLocalidades getAsyncLocalidades(final AsyncExecutionType type) {
    return new AsyncLocalidades(App.get().getSharedPreferences(this, "host").getString("host", "")) {
      @Override
      protected void onPostExecute(List<LocalidadeRest> localidadeList) {
        Assert.assertEquals(AsyncExecutionType.SUCCESS, type);
      }

      @Override
      protected void onCancelled() {
        Assert.assertEquals(AsyncExecutionType.CANCEL, type);
      }

      @Override
      protected void onCancelled(List<LocalidadeRest> localidadeList) {
        Assert.fail("it's not supposed to be called");
      }
    };
  }


}
