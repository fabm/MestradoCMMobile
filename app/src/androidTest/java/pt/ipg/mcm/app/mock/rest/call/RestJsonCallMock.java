package pt.ipg.mcm.app.mock.rest.call;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.okhttp.Request;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.AuthBasicUtf8;
import pt.ipg.mcm.calls.RestJsonCall;
import pt.ipg.mcm.calls.WebserviceException;

public class RestJsonCallMock extends RestJsonCall {

  protected String path;
  protected RestActionType restActionType;
  private Object request;

  public RestJsonCallMock() {
    super(null);

  }

  @Override
  public <T> T getResponse(Class<T> jeClass) throws WebserviceException {
    throw new UnsupportedOperationException("No caso de usar o mock fazer override deste método");
  }

  public String getPath() {
    return path;
  }

  public RestActionType getRestActionType() {
    return restActionType;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Object getRequest() {
    return request;
  }

  @Override
  protected Gson getGson() {
    return App.get().getGson();
  }

  public static class Builder extends RestJsonCall.Builder {
    private String path;
    private RestJsonCallMock restJsonMock;

    public Builder() {
    }

    @Override
    public RestJsonCall.Builder serverUrl(String serverUrl) {
      return this;
    }

    @Override
    public RestJsonCall.Builder path(String path) {
      this.path = path;
      return this;
    }

    @Override
    public RestJsonCall.Builder auth(AuthBasicUtf8 authBasicUtf8) {
      return this;
    }

    @Override
    public RestJsonCall.Builder post(Object request) {
      restJsonMock.restActionType = RestActionType.POST;
      restJsonMock.request = request;
      return this;
    }

    @Override
    public RestJsonCall.Builder put(JsonElement jsonElementRequest) {
      restJsonMock.restActionType = RestActionType.PUT;
      restJsonMock.request = jsonElementRequest;
      return this;
    }

    @Override
    public RestJsonCall.Builder get() {
      restJsonMock.restActionType = RestActionType.GET;
      return this;
    }

    @Override
    protected RestJsonCall abstractBuild(Request request) {
      return null;
    }

    public void setRestJsonMock(RestJsonCallMock restJsonMock) {
      this.restJsonMock = restJsonMock;
    }


    @Override
    public RestJsonCallMock build() {
      restJsonMock.setPath(path);
      return restJsonMock;
    }
  }
}
