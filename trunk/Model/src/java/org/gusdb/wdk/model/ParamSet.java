package org.gusdb.wdk.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParamSet extends WdkModelBase implements ModelSetI {

    private List<Param> paramList = new ArrayList<Param>();
    private Map<String, Param> paramMap = new LinkedHashMap<String, Param>();
    private String name;

    private List<ParamConfiguration> useTermOnlies =
            new ArrayList<ParamConfiguration>();
    private boolean useTermOnly = false;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Param getParam(String name) throws WdkModelException {
        Param q = paramMap.get(name);
        if (q == null)
            throw new WdkModelException("Param Set " + getName()
                    + " does not include param " + name);
        return q;
    }

    public Object getElement(String name) {
        return paramMap.get(name);
    }

    public Param[] getParams() {
        Param[] array = new Param[paramMap.size()];
        paramMap.values().toArray(array);
        return array;
    }

    public void addParam(Param param) throws WdkModelException {
        paramList.add(param);
    }

    public void addUseTermOnly(ParamConfiguration paramConfig) {
        useTermOnlies.add(paramConfig);
    }

    public boolean isUseTermOnly() {
        return useTermOnly;
    }

    public void resolveReferences(WdkModel model) throws WdkModelException {
        for (Param param : paramMap.values()) {
            param.resolveReferences(model);
        }
    }

    public void setResources(WdkModel model) throws WdkModelException {
        for (Param param : paramMap.values()) {
            param.setResources(model);
            param.setFullName(this.getName());
        }
    }

    public String toString() {
        String newline = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer("ParamSet: name='" + name + "'");
        buf.append(newline);

        for (Param param : paramMap.values()) {
            buf.append(newline);
            buf.append(":::::::::::::::::::::::::::::::::::::::::::::");
            buf.append(newline);
            buf.append(param).append(newline);
        }
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gusdb.wdk.model.WdkModelBase#excludeResources(java.lang.String)
     */
    @Override
    public void excludeResources(String projectId) throws WdkModelException {
        // exclude use term only. this must happen before processing params,
        // since enum/vocab params will use the value as default
        boolean hasUseTermOnly = false;
        for (ParamConfiguration paramConfig : useTermOnlies) {
            if (paramConfig.include(projectId)) {
                if (hasUseTermOnly) {
                    throw new WdkModelException("paramSet " + getName()
                            + " has more than one <useTermOnly> tag "
                            + "for project " + projectId);
                } else {
                    useTermOnly = paramConfig.isValue();
                    hasUseTermOnly = true;
                }
            }
        }
        useTermOnlies = null;

        // exclude resources in each question
        for (Param param : paramList) {
            // set the paramSet to each child param. The paramSet contains the
            // default value for the param, therefore it should happen before
            // excluding the resource from param
            if (param.include(projectId)) {
                param.setParamSet(this);
                param.excludeResources(projectId);
                String paramName = param.getName();

                if (paramMap.containsKey(paramName))
                    throw new WdkModelException("Param named " + paramName
                            + " already exists in param set " + this.name);
                paramMap.put(param.getName(), param);
            }
        }
        paramList = null;
    }

    // ///////////////////////////////////////////////////////////////
    // ///// protected
    // ///////////////////////////////////////////////////////////////

}
