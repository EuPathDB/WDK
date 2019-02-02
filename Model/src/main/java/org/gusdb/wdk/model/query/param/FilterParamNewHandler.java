package org.gusdb.wdk.model.query.param;

import org.gusdb.fgputil.EncryptionUtil;
import org.gusdb.fgputil.MapBuilder;
import org.gusdb.fgputil.validation.ValidObjectFactory.RunnableObj;
import org.gusdb.wdk.model.WdkModel;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.query.*;
import org.gusdb.wdk.model.query.spec.QueryInstanceSpec;
import org.gusdb.wdk.model.user.User;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jerric
 */
public class FilterParamNewHandler extends AbstractParamHandler {

  public static final String FILTERS_KEY = "filters";

  public FilterParamNewHandler() {}

  public FilterParamNewHandler(FilterParamNewHandler handler, Param param) {
    super(handler, param);
  }

  /*
   * @see org.gusdb.wdk.model.query.param.ParamHandlerPlugin#toStoredValue(org.gusdb .wdk.model.user.User,
   *      java.lang.String, java.util.Map)
   *      This method is not relevant to service layer, since it only uses stable values, never raw values.
   */
  @Override
  public String toStableValue(User user, Object rawValue) {
    return (String) rawValue;
  }

  /**
   * the raw value is a JSON string, and same as the stable value.
   */
  @Override
  public String toRawValue(User user, String stableValue) {
    return stableValue;
  }

  /**
   * return SQL that runs the metadataQuery, including its depended params, and applies
   * the filters to it.
   *

    SELECT mf.internal
      FROM (${metadata_qc}) mf
      WHERE mf.ontology_term_id = 'age'
      AND mf.numeric_value      >= 66
      AND mf.numeric_value      <= 80
    INTERSECT
    SELECT mf.internal
      FROM mf.${metadata_qc} mf
      WHERE mf.ontology_term_id = 'mood'
      AND mf.string_value       IN ('confused', 'happy')
    INTERSECT
    SELECT mf.internal
      FROM (${metadata_qc}) mf
      WHERE mf.ontology_term_id = 'size'
      AND mf.string_value       IN ('large')
   */
  @Override
  public String toInternalValue(RunnableObj<QueryInstanceSpec> ctxParamVals)
      throws WdkModelException {

    final String name = _param.getName();
    final String value = ctxParamVals.getObject().get(name);
    final User user = ctxParamVals.getObject().getUser();

    try {
      FilterParamNew fpn = (FilterParamNew) _param;
      FilterParamNewStableValue stableValue = new FilterParamNewStableValue(value, fpn);
      String fvSql = fpn.getFilteredMetadataSql(ctxParamVals, stableValue, fpn.getMetadataQuery(), null);
      String cachedSql = getCachedFilteredSql(user, fvSql, _param.getWdkModel());
      return "SELECT " + FilterParamNew.COLUMN_INTERNAL + " FROM (" + cachedSql + ")";

    }
    catch (JSONException ex) {
      throw new WdkModelException(ex);
    }
  }

  private String getCachedFilteredSql(User user, String filteredSql, WdkModel wdkModel) throws WdkModelException {
     try {
       // get an sqlquery so we can cache this internal value. it is parameterized by the sql itself
       SqlQuery sqlQuery = getSqlQueryForInternalValue(wdkModel);
       Map<String, String> paramValues = new MapBuilder<>("sql", filteredSql).toMap();
       QueryInstance<?> instance = Query.makeQueryInstance(QueryInstanceSpec.builder()
           .putAll(paramValues).buildRunnable(user, sqlQuery, null));
       return instance.getSqlUnsorted(); // because isCacheable=true, we get the cached sql
     }
     catch (WdkUserException e) {
       throw new WdkModelException(e);
     }
  }

  private SqlQuery getSqlQueryForInternalValue(WdkModel wdkModel) throws WdkModelException {
    SqlQuery sqlQuery = new SqlQuery();
    sqlQuery.setName("InternalValue");
    sqlQuery.setSql("select distinct " + FilterParamNew.COLUMN_INTERNAL + " from ( $$sql$$)");  // the sql will be provided by the sql param
    Column column = new Column();
    column.setName(FilterParamNew.COLUMN_INTERNAL);
    sqlQuery.addColumn(column);
    sqlQuery.setDoNotTest(true);
    sqlQuery.setIsCacheable(true);
    StringParam sqlParam = new StringParam();
    sqlParam.setName("sql");
    sqlParam.setNoTranslation(true);  // avoid quotes
    sqlQuery.addParam(sqlParam);
    sqlQuery.resolveReferences(wdkModel);
    QuerySet querySet= new QuerySet();
    querySet.setName("FilterParamNew");
    sqlQuery.setQuerySet(querySet);
    return sqlQuery;
  }

  /**
   * the signature is a checksum of sorted stable value.
   */
  @Override
  public String toSignature(RunnableObj<QueryInstanceSpec> ctxParamVals) throws WdkModelException {
    FilterParamNew param = (FilterParamNew)_param;
    QueryInstanceSpec spec = ctxParamVals.getObject();

    return EncryptionUtil.encrypt(
      new FilterParamNewStableValue(spec.get(_param.getName()), param).toSignatureString()
        + dependedParamsSignature(ctxParamVals));
  }

  private String dependedParamsSignature(RunnableObj<QueryInstanceSpec> contextParamValues) throws WdkModelException {
    FilterParamNew filterParam  = (FilterParamNew)_param;
    if (filterParam.getDependedParams() == null) return "";
    List<Param> dependedParamsList = new ArrayList<>(filterParam.getDependedParams());
    java.util.Collections.sort(dependedParamsList);
    StringBuilder sb = new StringBuilder();
    for (Param dependedParam : dependedParamsList)  {
      String stableValue = contextParamValues.getObject().get(dependedParam.getName());
      if (stableValue == null) throw new WdkModelException("can't find value for param " + dependedParam.getName());
      sb.append(dependedParam.getParamHandler().toSignature(contextParamValues));
    }

    return sb.toString();
  }

  @Override
  public ParamHandler clone(Param param) {
    return new FilterParamNewHandler(this, param);
  }

  @Override
  public String getDisplayValue(QueryInstanceSpec ctxParamVals)
      throws WdkModelException {

    FilterParamNew param = (FilterParamNew)_param;
    FilterParamNewStableValue stableValue = new FilterParamNewStableValue(ctxParamVals.get(param.getName()), param);
    return stableValue.getDisplayValue(ctxParamVals.getUser(), ctxParamVals.toMap());
  }
}
