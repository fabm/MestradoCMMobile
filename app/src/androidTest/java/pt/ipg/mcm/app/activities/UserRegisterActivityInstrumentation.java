package pt.ipg.mcm.app.activities;

import android.app.Instrumentation;
import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.google.gson.Gson;
import junit.framework.Assert;
import pt.ipg.mcm.app.DelegatedApp4Tests;
import pt.ipg.mcm.app.JsonTestFileReader;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.app.mock.rest.call.RestJsonCallMock;
import pt.ipg.mcm.calls.WebserviceException;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class UserRegisterActivityInstrumentation extends ActivityInstrumentationTestCase2<UserRegisterActivity> {

  private DelegatedApp4Tests delegatedApp = (DelegatedApp4Tests) App.get();
  private UserRegisterActivity currentActivity;
  private RestJsonCallMock restJsonCallMock;

  public UserRegisterActivityInstrumentation() {
    super(UserRegisterActivity.class);
  }


  @Override
  protected void setUp() throws Exception {
    super.setUp();

    delegatedApp.setGson(new Gson());

    final Map<String, String> mapCalls = new HashMap<>();
    mapCalls.put("/services/rest/user/create", "response1-localidade.json");
    mapCalls.put("/services/rest/localidade/filtro/1/gua", "response1-localidade-filtrada.json");
    mapCalls.put("/services/rest/localidade/filtro/2/gua", "response2-localidade-filtrada.json");
    mapCalls.put("/services/rest/localidade/filtro/3/gua", "response3-localidade-filtrada.json");
    mapCalls.put("/services/rest/localidade/filtro/4/gua", "response4-localidade-filtrada.json");


    restJsonCallMock = new RestJsonCallMock() {

      @Override
      public <T> T getResponse(Class<T> jeClass) throws WebserviceException {
        try {
          String res = mapCalls.get(path);
          if (res == null && path.startsWith("/services/rest/localidade/filtro/")) {
            return (T) new JsonTestFileReader("response1-localidade.json").getJsonObject();
          }
          return (T) new JsonTestFileReader(res).getJsonObject();
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    };

    RestJsonCallMock.Builder builder = new RestJsonCallMock.Builder();
    builder.setRestJsonMock(restJsonCallMock);

    currentActivity = getActivity();
    delegatedApp.setRestJsonCallBuilder(builder);

  }

  public void testOk() {

    Calendar calendar = new GregorianCalendar(2000, 20, 1);

    delegatedApp.setCalendar2DatePicker(calendar);

    onView(withId(R.id.etDataNascimentoRegisto)).perform(ViewActions.click());

    onView(withId(R.id.etNomeRegisto)).perform(ViewActions.typeText("Francisco"));
    onView(withId(R.id.etMoradaRegisto)).perform(ViewActions.typeText("Av. da minha casa"));
    onView(withId(R.id.etNumPortaRegisto)).perform(ViewActions.typeText("123"));

    onView(withId(R.id.etPasswordRegisto)).perform(ViewActions.scrollTo());
    onView(withId(R.id.etMailRegisto)).perform(ViewActions.typeText("x@sapo.pt"));
    onView(withId(R.id.etContribuinteRegisto)).perform(ViewActions.typeText("123456789"));
    onView(withId(R.id.etLoginRegisto)).perform(ViewActions.typeText("xico"));
    onView(withId(R.id.etPasswordRegisto)).perform(ViewActions.typeText("pass"));
    onView(withId(R.id.etContactoRegisto)).perform(ViewActions.typeText("999999999"));

    Instrumentation.ActivityMonitor monitor =
        getInstrumentation().
            addMonitor(LocalidadeActivity.class.getName(), null, false);

    onView(withId(R.id.ivLocalidade)).perform(ViewActions.scrollTo(), ViewActions.click());

    LocalidadeActivity startedActivity = (LocalidadeActivity) monitor
        .waitForActivityWithTimeout(2000);

    assertNotNull(startedActivity);

    onView(withId(R.id.lvLocalidades)).perform(ViewActions.click());

    onView(withId(R.id.btOkRegistar)).perform(ViewActions.click());

    Assert.assertNotNull(restJsonCallMock.getRequest());

    Log.v("log ",restJsonCallMock.getRequest().toString());

  }

  public void testIntent() {


  }
}