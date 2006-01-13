package org.gusdb.wdk.model.jspwrap;

import org.gusdb.wdk.model.Answer;
import org.gusdb.wdk.model.FieldI;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.BooleanQuery;

import java.util.Map;
import java.util.Iterator;
import java.util.Vector;

/**
 * A wrapper on a {@link Answer} that provides simplified access for 
 * consumption by a view
 */ 
public class AnswerBean {

    Answer answer;
    Map<Object, Object> downloadConfigMap = null;

    boolean isCombinedAnswer = false;
    String userAnswerName = null;

    public AnswerBean(Answer answer) {
	this.answer = answer;
    }

    /**
     * @return A Map of param displayName --> param value.
     */
    public Map<String, Object> getParams() {
	return answer.getDisplayParams();
    }

    public Map<Object, Object> getInternalParams() {
	return answer.getParams();
    }

    public Integer getDatasetId() {
	return answer.getDatasetId();
    }

    /**
     * @return opertation for boolean answer
     */
    public String getBooleanOperation() {
	System.err.println("the param map is: " + answer.getParams());
	if (!getIsBoolean()) {
	    throw new RuntimeException("getBooleanOperation can not be called on simple AnswerBean");
	}
	return (String)answer.getParams().get(BooleanQuery.OPERATION_PARAM_NAME);
    }

    /**
     * @return first child answer for boolean answer, null if it is an answer for a simple question.
     */
    public AnswerBean getFirstChildAnswer() {
	if (!getIsBoolean()) {
	    throw new RuntimeException("getFirstChildAnswer can not be called on simple AnswerBean");
	}
	Object childAnswer = answer.getParams().get(BooleanQuery.FIRST_ANSWER_PARAM_NAME);
	return new AnswerBean((Answer)childAnswer);
    }

    /**
     * @return second child answer for boolean answer, null if it is an answer for a simple question.
     */
    public AnswerBean getSecondChildAnswer() {
	if (!getIsBoolean()) {
	    throw new RuntimeException("getSecondChildAnswer can not be called on simple AnswerBean");
	}
	Object childAnswer = answer.getParams().get(BooleanQuery.SECOND_ANSWER_PARAM_NAME);
	return new AnswerBean((Answer)childAnswer);
    }

    public int getPageSize() {
	return answer.getPageSize();
    }

    public int getResultSize() {
 	try {
	    return answer.getResultSize();
	} catch (WdkModelException e) {
	    throw new RuntimeException(e);
	}
    }

    public boolean getIsBoolean(){
	return answer.getIsBoolean();
    }

    public RecordClassBean getRecordClass() {
	return new RecordClassBean(answer.getQuestion().getRecordClass());
    }

    public QuestionBean getQuestion() {
	return new QuestionBean(answer.getQuestion());
    }

    /**
     * @return A list of {@link RecordBean}s.
     */
    public Iterator getRecords() {
	return new RecordBeanList();
    }

    /**
     * for controller: reset counter for download purpose
     */
    public void resetAnswerRowCursor() {
	answer.resetRecordInstanceCounter();
    }

    /**
     * for controller: reset counter for download purpose
     */
    public boolean getResetAnswerRowCursor() {
	answer.resetRecordInstanceCounter();
	return true;
    }

    public void setDownloadConfigMap (Map downloadConfigMap) {
	this.downloadConfigMap = downloadConfigMap;
    }

    public AttributeFieldBean[] getSummaryAttributes() {
	Map<String, FieldI> attribs = answer.getQuestion().getSummaryAttributes();
	Iterator<String> ai = attribs.keySet().iterator();
	Vector<AttributeFieldBean> v = new Vector<AttributeFieldBean>();
	while (ai.hasNext()) {
	    String attribName = ai.next();
	    v.add(new AttributeFieldBean(attribs.get(attribName)));
	}
	int size = v.size();
	AttributeFieldBean[] sumAttribs = new AttributeFieldBean[size];
	v.copyInto(sumAttribs);
	return sumAttribs;
    }

    public String[] getSummaryAttributeNames() {
	AttributeFieldBean[] sumAttribs = getSummaryAttributes();
	Vector<String> v = new Vector<String>();
	for (int i=0; i<sumAttribs.length; i++) {
	    String attribName = sumAttribs[i].getName();
	    v.add(attribName);
	}
	int size = v.size();
	String[] sumAttribNames = new String[size];
	v.copyInto(sumAttribNames);
	return sumAttribNames;
    }

    public AttributeFieldBean[] getDownloadAttributes() {
	AttributeFieldBean[] sumAttribs = getSummaryAttributes();
	Vector<AttributeFieldBean> v = new Vector<AttributeFieldBean>();
	for (int i=0; i<sumAttribs.length; i++) {
	    String attribName = sumAttribs[i].getName();

	    if (downloadConfigMap == null || downloadConfigMap.size() == 0) {
		v.add(sumAttribs[i]);
	    } else {
		Object configStatus = downloadConfigMap.get(attribName);
		//System.err.println("DEBUG AnswerBean: configStatus for " + attrName + " is " + configStatus);
		if (configStatus != null) { v.add(sumAttribs[i]); }
            }
	}
	int size = v.size();
	AttributeFieldBean[] downloadAttribs = new AttributeFieldBean[size];
	v.copyInto(downloadAttribs);
	return downloadAttribs;
    }

    public String[] getDownloadAttributeNames() {
	AttributeFieldBean[] downloadAttribs = getDownloadAttributes();
	Vector<String> v = new Vector<String>();
	for (int i=0; i<downloadAttribs.length; i++) {
	    v.add(downloadAttribs[i].getName());
	}
	int size = v.size();
	String[] downloadAttribNames = new String[size];
	v.copyInto(downloadAttribNames);
	return downloadAttribNames;
    }

    public void setIsCombinedAnswer(boolean isComAns) {
	isCombinedAnswer = isComAns;
    }
    public boolean getIsCombinedAnswer() {
	return isCombinedAnswer;
    }

    public void setUserAnswerName(String ua) {
	userAnswerName = ua;
    }
    public String getUserAnswerName() {
	return userAnswerName;
    }

    ////////////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////////////

    class RecordBeanList implements Iterator {

	public int getSize() {
	    return answer.getPageSize();
	}
    
	public boolean hasNext() {
	    try {
		return answer.hasMoreRecordInstances();
	    } catch (WdkModelException exp) {
                throw new RuntimeException(exp);
            }
	}
	
	public Object next() {
	    try {
		return new RecordBean(answer.getNextRecordInstance());
	    }
	    catch (WdkModelException exp) {
		throw new RuntimeException(exp);
	    }
	}
    
	public void remove() {
	    throw new UnsupportedOperationException("remove isn't allowed on this iterator");
	} 
	
    }
    

}
