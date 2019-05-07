package org.gusdb.wdk.model.query.spec;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gusdb.fgputil.Tuples.TwoTuple;
import org.gusdb.fgputil.collection.ReadOnlyHashMap;
import org.gusdb.fgputil.validation.ValidationBundle;
import org.gusdb.fgputil.validation.ValidationBundle.ValidationBundleBuilder;
import org.gusdb.fgputil.validation.ValidationLevel;
import org.gusdb.wdk.model.Utilities;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.query.param.Param;
import org.gusdb.wdk.model.query.param.ParameterContainer;
import org.gusdb.wdk.model.query.spec.PartiallyValidatedStableValues.ParamValidity;
import org.gusdb.wdk.model.user.StepContainer;
import org.gusdb.wdk.model.user.User;

public class ParameterContainerInstanceSpecBuilder<T extends ParameterContainerInstanceSpecBuilder<T>>
    extends ReadOnlyHashMap.Builder<String,String> {

  public static enum FillStrategy {
    NO_FILL(false, false),
    FILL_PARAM_IF_MISSING(true, false),
    FILL_PARAM_IF_MISSING_OR_INVALID(true, true);

    private final boolean _fillWhenMissing;
    private final boolean _fillWhenInvalid;

    private FillStrategy(boolean fillWhenMissing, boolean fillWhenInvalid) {
      _fillWhenMissing = fillWhenMissing;
      _fillWhenInvalid = fillWhenInvalid;
    }

    public boolean shouldFillWhenMissing() {
      return _fillWhenMissing;
    }

    public boolean shouldFillWhenInvalid() {
      return _fillWhenInvalid;
    }
  }

  ParameterContainerInstanceSpecBuilder() {
    super(new LinkedHashMap<>());
  }

  ParameterContainerInstanceSpecBuilder(ParameterContainerInstanceSpec<?> spec) {
    super(new LinkedHashMap<>(spec.toMap()));
  }

  @Override
  @SuppressWarnings("unchecked")
  public T put(String key, String value) {
    return (T)super.put(key, value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public T putAll(Map<String,String> values) {
    return (T)super.putAll(values);
  }

  protected TwoTuple<PartiallyValidatedStableValues, ValidationBundleBuilder>
  validateParams(User user, ParameterContainer paramContainer, StepContainer stepContainer,
      ValidationLevel validationLevel, FillStrategy fillStrategy) throws WdkModelException {

    // create a copy of the values in this builder which will be modified before passing to constructor
    Map<String,String> tmpValues = new HashMap<>(toMap());

    // trim off any values supplied that don't apply to this container
    for (String name : tmpValues.keySet().toArray(new String[0])) {
      if (!paramContainer.getParamMap().keySet().contains(name)) {
        tmpValues.remove(name);
      }
    }

    // add user_id to the param values if needed
    String userKey = Utilities.PARAM_USER_ID;
    if (paramContainer.getParamMap().containsKey(userKey) && !tmpValues.containsKey(userKey)) {
      tmpValues.put(userKey, Long.toString(user.getUserId()));
    }

    PartiallyValidatedStableValues stableValues = new PartiallyValidatedStableValues(user, tmpValues, stepContainer);
    ValidationBundleBuilder validation = ValidationBundle.builder(validationLevel);
    for (Param param : paramContainer.getParams()) {
      ParamValidity result = param.validate(stableValues, validationLevel, fillStrategy);
      if (!result.isValid()) {
        validation.addError(param.getName(), result.getMessage());
      }
    }

    return new TwoTuple<>(stableValues, validation);
  }
}