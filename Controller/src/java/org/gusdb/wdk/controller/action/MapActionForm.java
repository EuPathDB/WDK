package org.gusdb.wdk.controller.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action.ActionForm;

public abstract class MapActionForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Map<String, Object> values = new HashMap<String, Object>();
    private Map<String, String[]> arrays = new HashMap<String, String[]>();

    public Map<String, Object> getValues() {
        return new HashMap<String, Object>(values);
    }

    public Object getValue(String key) {
        return values.get(key);
    }

    public void setValue(String key, Object value) {
        values.put(key, value);
    }

    public Map<String, String[]> getArrays() {
        return new HashMap<String, String[]>(arrays);
    }

    public String[] getArray(String key) {
        return arrays.get(key);
    }

    public void setArray(String key, String[] array) {
        arrays.put(key, array);
    }

    public void copyFrom(MapActionForm form) {
        values.clear();
        for (String key : form.values.keySet()) {
            values.put(key, form.values.get(key));
        }

        arrays.clear();
        for (String key : form.arrays.keySet()) {
            arrays.put(key, form.arrays.get(key));
        }
    }
}
