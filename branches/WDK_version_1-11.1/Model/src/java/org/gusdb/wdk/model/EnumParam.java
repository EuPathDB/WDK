package org.gusdb.wdk.model;

import java.util.LinkedHashMap;


public class EnumParam extends AbstractEnumParam {
    
    public EnumParam () {
	super();
    }

    /////////////////////////////////////////////////////////////////////
    /////////////  Public properties ////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    public void addItem(EnumItem item) {
	
	if (vocabMap == null) vocabMap = new LinkedHashMap();
	orderedKeySet.add(item.getTerm());
	vocabMap.put(item.getTerm(), item.getInternal());
    }

    /////////////////////////////////////////////////////////////////////
    /////////////  Protected properties ////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    protected void resolveReferences(WdkModel model) throws WdkModelException {
    }

    protected void initVocabMap() throws WdkModelException {
	
    }

    /* (non-Javadoc)
     * @see org.gusdb.wdk.model.Param#clone()
     */
    @Override
    public Param clone() {
        EnumParam param = new EnumParam();
        super.clone(param);
        return param;
    }
}
