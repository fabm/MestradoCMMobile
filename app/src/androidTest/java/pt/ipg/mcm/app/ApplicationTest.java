package pt.ipg.mcm.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.app.mock.rest.call.RestJsonCallMock;
import pt.ipg.mcm.calls.WebserviceException;
import pt.ipg.mcm.calls.client.model.user.CreateUserClientRestIn;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ApplicationTest extends ApplicationTestCase<Application> {

  private DelegatedApp4Tests delegatedApp4Tests;

  public ApplicationTest() {
    super(Application.class);
    delegatedApp4Tests = (DelegatedApp4Tests) App.get();
    createApp();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  private void createApp() {
    DelegatedApp4Tests delegatedApp4Tests = (DelegatedApp4Tests) App.get();
    delegatedApp4Tests.setRestJsonCallBuilder(new RestJsonCallMock.Builder());
  }


  public void testLogin() {

    SharedPreferences sharedPreferences = delegatedApp4Tests.getSharedPreferences(getContext(), "user");

    String login = sharedPreferences.getString("login", null);

    String authenticated = login;

    Assert.assertNull(authenticated);

    sharedPreferences.edit()
        .putString("login", "francisco")
        .putString("password", "francisco")
        .commit();

    RestJsonCallMock restJsonCallMock = new RestJsonCallMock() {
      @Override
      public <T> T getResponse(Class<T> jeClass) throws WebserviceException {
        return (T) getJsonObject("response1-login.json", jeClass);
      }

      @Override
      public ObjectMapper getObjectMapper() {
        return getObjectMapper();
      }
    };

    delegatedApp4Tests.getNewRestJsonCallBuilder().setRestJsonMock(restJsonCallMock);


  }


  public void testCriaRegisto() throws InterruptedException {

    final Thread current = Thread.currentThread();

    final RestJsonCallMock restJsonCallMock = new RestJsonCallMock() {
      @Override
      public <T> T getResponse(Class<T> jeClass) throws WebserviceException {
        return (T) getJsonObject("response1-registar.json", jeClass);
      }

      @Override
      public ObjectMapper getObjectMapper() {
        return getObjectMapper();
      }
    };

    delegatedApp4Tests.getNewRestJsonCallBuilder().setRestJsonMock(restJsonCallMock);

/*
    AsyncUserSign asyncUserSign = new AsyncUserSign(aa) {

      protected void onPostExecute(Void response) {
        synchronized (current) {
          assertEquals(RestActionType.POST,restJsonCallMock.getRestActionType());
          current.notify();
        }
      }
    };
*/

    CreateUserClientRestIn request = getJsonObject("request1-registar.json", CreateUserClientRestIn.class);

/*
    asyncUserSign.execute(request);
*/


    synchronized (current) {
      current.wait();
    }
  }

  private <T> T getJsonObject(String file, Class<T> clazz) {
    Reader reader = new InputStreamReader(ApplicationTest.class.getResourceAsStream(file));
    try {
      return delegatedApp4Tests.getObjectMapper().readValue(reader,clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void assertBdAfterPostConstruct() {
    SQLiteDatabase db = App.get().getOpenHelper(getContext()).getReadableDatabase();

    db.close();
  }


}