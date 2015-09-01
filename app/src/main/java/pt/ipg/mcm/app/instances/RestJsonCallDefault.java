package pt.ipg.mcm.app.instances;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;
import pt.ipg.mcm.calls.RestJsonCall;

public class RestJsonCallDefault extends RestJsonCall {
  public RestJsonCallDefault(Request request) {
    super(request);
  }

  @Override
  public ObjectMapper getObjectMapper() {
    return App.get().getObjectMapper();
  }

  public static class Builder extends RestJsonCall.Builder {

    @Override
    protected RestJsonCall abstractBuild(Request request) {
      return new RestJsonCallDefault(request);
    }

    @Override
    public RestJsonCall build() {
      return super.build();
    }
  }
}
