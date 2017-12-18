package org.gusdb.wdk.service.formatter.param;

import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.query.param.AbstractEnumParam;
import org.gusdb.wdk.model.query.param.EnumParamVocabInstance;
import org.gusdb.wdk.model.query.param.values.ValidStableValuesFactory.CompleteValidStableValues;
import org.gusdb.wdk.model.user.User;
import org.gusdb.wdk.service.formatter.Keys;
import org.json.JSONException;
import org.json.JSONObject;

public class EnumParamFormatter extends AbstractEnumParamFormatter {

  EnumParamFormatter(AbstractEnumParam param) {
    super(param);
  }

  @Override
  public JSONObject getJson(User user, CompleteValidStableValues stableValues)
      throws JSONException, WdkModelException, WdkUserException {
    EnumParamVocabInstance vocabInstance = getVocabInstance(user, stableValues);
    return super.getJson(user, stableValues)
        .put(Keys.DEFAULT_VALUE, vocabInstance.getDefaultValue())
        .put(Keys.VOCABULARY, getVocabJson(vocabInstance));
  }
}
