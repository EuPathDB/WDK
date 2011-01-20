package org.gusdb.wdk.model.jspwrap;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;

import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.query.param.AbstractEnumParam;
import org.gusdb.wdk.model.query.param.EnumParam;
import org.gusdb.wdk.model.query.param.EnumParamTermNode;
import org.gusdb.wdk.model.query.param.Param;
import org.json.JSONException;

/**
 * A wrapper on a {@link EnumParam} that provides simplified access for
 * consumption by a view
 */
public class EnumParamBean extends ParamBean {

    public EnumParamBean(UserBean user, AbstractEnumParam param) {
        super(user, param);
    }

    public EnumParamBean(EnumParamBean parambean) {
        this(parambean.user, (AbstractEnumParam) parambean.param);
    }

    public Boolean getMultiPick() {
        return ((AbstractEnumParam) param).getMultiPick();
    }

    public String[] getVocab() throws WdkModelException,
            NoSuchAlgorithmException, SQLException, JSONException,
            WdkUserException {
        return ((AbstractEnumParam) param).getVocab();
    }

    public String[] getDisplays() throws WdkModelException,
            NoSuchAlgorithmException, SQLException, JSONException,
            WdkUserException {
        return ((AbstractEnumParam) param).getDisplays();
    }

    public Map<String, String> getDisplayMap() throws WdkModelException,
            NoSuchAlgorithmException, SQLException, JSONException,
            WdkUserException {
        return ((AbstractEnumParam) param).getDisplayMap();
    }

    public Map<String, String> getParentMap() throws WdkModelException,
            NoSuchAlgorithmException, SQLException, JSONException,
            WdkUserException {
        return ((AbstractEnumParam) param).getParentMap();
    }

    public String getDisplayType() {
        return ((AbstractEnumParam) param).getDisplayType();
    }

    public ParamBean getDependedParam() {
        Param dependedParam = ((AbstractEnumParam) param).getDependedParam();
        if (dependedParam != null) {
            return new ParamBean(user, dependedParam);
        }
        return null;
    }

    public String getDependedValue() {
        return ((AbstractEnumParam) param).getDependedValue();
    }

    public void setDependedValue(String dependedValue) {
        ((AbstractEnumParam) param).setDependedValue(dependedValue);
    }

    public EnumParamTermNode[] getVocabTreeRoots() throws Exception {
        try {
            return ((AbstractEnumParam) param).getVocabTreeRoots();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public String[] getTerms(String termList) throws NoSuchAlgorithmException,
            WdkModelException, SQLException, JSONException, WdkUserException {
        return ((AbstractEnumParam) param).getTerms(termList);
    }

    public String getRawDisplayValue() throws Exception {
        String rawValue = getRawValue();
        if (rawValue == null) rawValue = "";
        if (!((AbstractEnumParam) param).isSkipValidation()) {
            String[] terms = rawValue.split(",");
            Map<String, String> displays = getDisplayMap();
            StringBuffer buffer = new StringBuffer();
            for (String term : terms) {
                if (buffer.length() > 0) buffer.append(", ");
                String display = displays.get(term.trim());
                if (display == null) display = term;
                buffer.append(display);
            }
            return buffer.toString();
        } else {
            return rawValue;
        }
    }
}
