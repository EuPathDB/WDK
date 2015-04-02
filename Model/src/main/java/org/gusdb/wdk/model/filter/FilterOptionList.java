package org.gusdb.wdk.model.filter;

import java.util.LinkedHashMap;
import java.util.Map;

import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.user.Step;
import org.json.JSONArray;
import org.json.JSONObject;

public class FilterOptionList {

  private final Map<String, FilterOption> _options = new LinkedHashMap<>();

  private final Step _step;

  public FilterOptionList(Step step) {
    this._step = step;
  }

  public FilterOptionList(Step step, JSONArray jsOptions) throws WdkModelException {
    this(step);
    for (int i = 0; i < jsOptions.length(); i++) {
      JSONObject jsFilterOption = jsOptions.getJSONObject(i);
      FilterOption option = new FilterOption(step, jsFilterOption);
      _options.put(option.getKey(), option);
    }
  }

  public boolean isFiltered() {
    return (_options.size() > 0);
  }

  public void addFilterOption(String filterName, JSONObject filterValue) throws WdkModelException {
    Filter filter = _step.getQuestion().getFilter(filterName);
    FilterOption option = new FilterOption(_step, filter, filterValue);
    _options.put(filterName, option);
  }

  public void removeFilterOption(String filterName) {
    _options.remove(filterName);
  }

  public Map<String, FilterOption> getFilterOptions() {
    return new LinkedHashMap<>(_options);
  }

  public JSONArray getJSON() {
    JSONArray jsOptions = new JSONArray();
    for (FilterOption option : _options.values()) {
      jsOptions.put(option.getJSON());
    }
    return jsOptions;
  }

  public FilterOption getFilterOption(String filterName) {
    return _options.get(filterName);
  }
}
