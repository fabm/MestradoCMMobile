package pt.ipg.mcm.app.mock;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SharedPreferenciesMock implements SharedPreferences {
  private Map<String,Object> map;

  public SharedPreferenciesMock() {
    map = new HashMap<>();
  }

  @Override
  public Map<String, ?> getAll() {
    return map;
  }

  @Override
  public String getString(String key, String defValue) {
    return get(key,defValue);
  }

  @Override
  public Set<String> getStringSet(String key, Set<String> defValues) {
    return get(key,defValues);
  }

  @Override
  public int getInt(String key, int defValue) {
    return get(key,defValue);
  }

  @Override
  public long getLong(String key, long defValue) {
    return get(key,defValue);
  }

  @Override
  public float getFloat(String key, float defValue) {
    return get(key,defValue);
  }

  @Override
  public boolean getBoolean(String key, boolean defValue) {
    return get(key,defValue);
  }

  private <T>T get(String key,T defValue){
    Object value =  map.get(key);
    if(value == null){
      return (T) defValue;
    }
    return (T) value;
  }

  @Override
  public boolean contains(String key) {
    return map.containsKey(key);
  }

  @Override
  public Editor edit() {
    return new Editor(map);
  }

  @Override
  public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

  }

  @Override
  public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

  }


  public class Editor implements SharedPreferences.Editor {
    private Map<String,Object> localMap;
    private Map<String, Object> map;
    private List<String> removes;

    public Editor(Map<String,Object> map) {
      this.map = map;
      localMap = new HashMap<>();
      removes = new ArrayList<>();
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
      localMap.put(key,value);
      return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
      localMap.put(key,values);
      return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
      localMap.put(key,value);
      return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
      localMap.put(key,value);
      return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
      localMap.put(key,value);
      return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
      localMap.put(key,value);
      return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
      localMap.remove(key);
      return this;
    }

    @Override
    public SharedPreferences.Editor clear() {
      localMap.clear();
      return this;
    }

    @Override
    public boolean commit() {
      map.putAll(localMap);
      for(String key:removes){
          map.remove(key);
      }
      return true;
    }

    @Override
    public void apply() {

    }
  }
}
