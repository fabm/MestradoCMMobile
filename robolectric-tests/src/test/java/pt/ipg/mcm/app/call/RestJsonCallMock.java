package pt.ipg.mcm.app.call;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.okhttp.Request;
import pt.ipg.mcm.app.JsonTestFile;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.AuthBasicUtf8;
import pt.ipg.mcm.calls.RestJsonCall;
import pt.ipg.mcm.calls.WebserviceException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RestJsonCallMock extends RestJsonCall {

  protected String path;
  protected RestActionType restActionType;
  private Object request;
  private Map<String, String> mapRequest;

  public RestJsonCallMock() {
    super(null);
    mapRequest = new HashMap<>();
  }

  public RestJsonCallMock(String path) {
    this();
    mapRequest.put(null, path);
  }

  public Map<String, String> getMapRequest() {
    return mapRequest;
  }

  @Override
  public <T> T getResponse(Class<T> jeClass) throws WebserviceException {
    String filePath = mapRequest.get(path);
    if (filePath == null) {
      filePath = mapRequest.get(null);
    }
    try {
      return (T) new JsonTestFile(filePath).getRequestObject(jeClass);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public ObjectMapper getObjectMapper() {
    return App.get().getObjectMapper();
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

  public static class Builder extends RestJsonCall.Builder<RestJsonCallMock> {
    private String path;
    private RestJsonCallMock restJsonMock;

    public Builder() {
    }

    @Override
    public RestJsonCallMock.Builder serverUrl(String serverUrl) {
      return this;
    }

    @Override
    public RestJsonCallMock.Builder path(String path) {
      this.path = path;
      return this;
    }

    @Override
    public RestJsonCallMock.Builder auth(AuthBasicUtf8 authBasicUtf8) {
      return this;
    }

    @Override
    public RestJsonCallMock.Builder post(Object object) {
      restJsonMock.restActionType = RestActionType.POST;
      restJsonMock.request = object;
      return this;
    }

    public RestJsonCallMock.Builder put(JsonElement jsonElementRequest) {
      restJsonMock.restActionType = RestActionType.PUT;
      restJsonMock.request = jsonElementRequest;
      return this;
    }

    @Override
    public RestJsonCallMock.Builder get() {
      restJsonMock.restActionType = RestActionType.GET;
      return this;
    }

    @Override
    protected RestJsonCallMock abstractBuild(Request request) {
      return new RestJsonCallMock() {
        @Override
        public ObjectMapper getObjectMapper() {
          return App.get().getObjectMapper();
        }
      };
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
