package org.gusdb.wdk.model.query.param;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.gusdb.wdk.model.WdkModel;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkModelText;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.user.User;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The DateRangeParam is strictly a web service parameter
 * 
 * 
 *         raw value: a stringified json object containing a min and a max date,
 *         both in iso1806 format (yyyy-mm-dd);
 * 
 *         stable value: same as raw value;
 * 
 *         signature: a checksum of the stable value;
 * 
 *         internal value: same as stable value;
 */
public class DateRangeParam extends Param {

  private List<WdkModelText> _regexes;
  private String _regex;
  private String _minDate;
  private String _maxDate;

  public DateRangeParam() {
    _regexes = new ArrayList<WdkModelText>();

    // register handler
    setHandler(new DateRangeParamHandler());
  }

  public DateRangeParam(DateRangeParam param) {
    super(param);
    if (param._regexes != null)
      _regexes = new ArrayList<WdkModelText>();
    _regex = param._regex;
    _minDate = param._minDate;
    _maxDate = param._maxDate;
  }

  // ///////////////////////////////////////////////////////////////////
  // /////////// Public properties ////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////

  public void addRegex(WdkModelText regex) {
    _regexes.add(regex);
  }

  public void setRegex(String regex) {
    _regex = regex;
  }

  public String getRegex() {
    return _regex;
  }

  @Override
  public String getDefault() throws WdkModelException {
    String defaultValue = super.getDefault();
    try {
      return (defaultValue == null || defaultValue.isEmpty()) ?
          // if default not provided, default is the entire range
          new JSONObject().put("min", getMinDate()).put("max", getMaxDate()).toString() :
          // incoming value may be using single quotes around keys; allow, but translate to proper JSON
          new JSONObject(defaultValue).toString();
    }
    catch (JSONException e) {
      throw new WdkModelException("Supplied default value (" + defaultValue + ") is not valid JSON.", e);
    }
  }

  public String getMinDate() {
    return _minDate;
  }

  /**
   * Setter for minimum allowed date that includes a check to insure that the
   * model's minimum allowed date is in proper format
   * @param minDate
   * @throws WdkModelException
   */
  public void setMinDate(String minDate) throws WdkModelException {
    try {  
      LocalDate.parse(minDate, DateTimeFormatter.ISO_DATE);
    }
    catch(DateTimeParseException dtpe) {
      throw new WdkModelException(dtpe);
    }
    _minDate = minDate;
  }

  public String getMaxDate() {
    return _maxDate;
  }

  /**
   * Setter for maximum allowed date that includes a check to insure that the
   * model's maximum allowed date is in proper format
   * @param maxDate
   * @throws WdkModelException
   */
  public void setMaxDate(String maxDate) throws WdkModelException {
    try {  
      LocalDate.parse(maxDate, DateTimeFormatter.ISO_DATE);
    }
    catch(DateTimeParseException dtpe) {
      throw new WdkModelException(dtpe);
    }
    _maxDate = maxDate;
  }

  @Override
  public String toString() {
    String newline = System.getProperty("line.separator");
    return new StringBuilder(super.toString())
    .append("  regex='").append(_regex).append("'").append(newline).toString();
  }

  // ///////////////////////////////////////////////////////////////
  // protected methods
  // ///////////////////////////////////////////////////////////////

  @Override
  public void resolveReferences(WdkModel model) throws WdkModelException {
    super.resolveReferences(model);
    if (_regex == null)
      _regex = model.getModelConfig().getParamRegex();
    // The default regex is just a date string expressed in iso1806 format
    if (_regex == null) {
      _regex = "\\d{4}-\\d{2}-\\d{2}";
    }
  }

  @Override
  public Param clone() {
    return new DateRangeParam(this);
  }

  @Override
  protected void appendChecksumJSON(JSONObject jsParam, boolean extra) throws JSONException {
    // nothing to be added
  }

  /**
   * Ensure that the value provided by the user conforms to the parameter's requirements
   * 
   * @see org.gusdb.wdk.model.query.param.Param#validateValue(java.lang.String)
   */
  @Override
  protected void validateValue(User user, String stableValue, Map<String, String> contextParamValues)
      throws WdkUserException, WdkModelException {

    JSONObject stableValueJson = null;
    LocalDate minValue;
    LocalDate maxValue;

    // Insure that the JSON Object format is valid.
    try {
      stableValueJson = new JSONObject(stableValue);
      minValue = LocalDate.parse(stableValueJson.getString("min"), DateTimeFormatter.ISO_DATE);
      maxValue = LocalDate.parse(stableValueJson.getString("max"), DateTimeFormatter.ISO_DATE);
    }
    catch(JSONException je) {
      throw new WdkUserException("Could not parse '" + stableValue + "'. "
          + "The range should be is the format {'min':'min value','max':'max value'}");
    }

    // Validate each value in the range against regex.  The regex could potentially be
    // more restrictive than LocalDate.
    if (_regex != null) {
     if (!stableValueJson.getString("min").matches(_regex)) {
       throw new WdkUserException("value '" + stableValueJson.getString("min") + "' is invalid. " +
         "It must match the regular expression '" + _regex + "'");
     }
     if (!stableValueJson.getString("max").matches(_regex)) {
       throw new WdkUserException("value '" + stableValueJson.getString("max") + "' is invalid. " +
         "It must match the regular expression '" + _regex + "'");
      }
    }

    // Ensure that the minimum date does not come after the maximum date
    if (minValue.isAfter(maxValue)) {
      throw new WdkUserException("The minimum date '" + minValue + "' should " +
          "come before, or equal, the maximum date '" + maxValue + "'");
    }

    // Ensure that the minimum date comes no earlier than the minimum allowed date
    if (_minDate != null &&
        minValue.isBefore(LocalDate.parse(_minDate, DateTimeFormatter.ISO_DATE))) {
      throw new WdkUserException("The date '" + minValue + "' should not be earlier than '" + _minDate + "'");
    }

    // Ensure that the maximum data comes no later than the maximum allowed date
    if (_maxDate != null && 
        maxValue.isAfter(LocalDate.parse(_maxDate, DateTimeFormatter.ISO_DATE))) {
      throw new WdkUserException("The date '" + maxValue + "' should not be after '" + _maxDate + "'");
    }
    
  }

  /**
   * Need to alter sql replacement to accommodate fact that internal value
   * is really a JSON string containing min and max ends of range.  The convention is
   * that the minimum value replace $$name.min$$ and the maximum value replace $$name.max$$
   * in the query.
   */
  @Override
  public String replaceSql(String sql, String internalValue) {
    JSONObject valueJson = new JSONObject(internalValue);
    LocalDate values[] = new LocalDate[2];
    values[0] = LocalDate.parse(valueJson.getString("min"), DateTimeFormatter.ISO_DATE);
    values[1] = LocalDate.parse(valueJson.getString("max"), DateTimeFormatter.ISO_DATE);
    String regex = "\\$\\$" + _name + ".min\\$\\$";
    String replacedSql = sql.replaceAll(regex, Matcher.quoteReplacement("date '"  + values[0].toString() + "'"));
    regex = "\\$\\$" + _name + ".max\\$\\$";
    replacedSql = replacedSql.replaceAll(regex, Matcher.quoteReplacement("date '"  + values[1].toString() + "'"));
    return replacedSql;
  }

  @Override
  public void excludeResources(String projectId) throws WdkModelException {
    super.excludeResources(projectId);

    boolean hasRegex = false;
    for (WdkModelText regex : _regexes) {
      if (regex.include(projectId)) {
        if (hasRegex) {
          throw new WdkModelException("The param " + getFullName() + " has more than one regex for project " +
              projectId);
        }
        else {
          _regex = regex.getText();
          hasRegex = true;
        }
      }
    }
    _regexes = null;
  }

  @Override
  protected void applySuggestion(ParamSuggestion suggest) {
    // do nothing
  }

  @Override
  public String getBriefRawValue(Object rawValue, int truncateLength) throws WdkModelException {
    String value = (String) rawValue;
    if (value == null) return value;
    if (value.length() > truncateLength)
      value = value.substring(0, truncateLength) + "...";
    return value;
  }

}
