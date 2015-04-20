package org.gusdb.wdk.model.question;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gusdb.wdk.model.ModelSetI;
import org.gusdb.wdk.model.WdkModel;
import org.gusdb.wdk.model.WdkModelBase;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkModelText;
import org.gusdb.wdk.model.test.sanity.OptionallyTestable;

/**
 * Question sets are used to organize questions into different groups.
 * 
 * Created: Fri June 4 15:05:30 2004 EDT
 * 
 * @author David Barkan
 * @version $Revision$ $Date: 2006-03-09 23:02:31 -0500 (Thu, 09 Mar
 *          2006) $ $Author$
 */

public class QuestionSet extends WdkModelBase implements ModelSetI<Question>, OptionallyTestable {

    private List<Question> questionList = new ArrayList<Question>();
    private Map<String, Question> questionMap = new LinkedHashMap<String, Question>();
    private String name;
    private String displayName;

    private List<WdkModelText> descriptions = new ArrayList<WdkModelText>();
    private String description;
    private boolean doNotTest = false;

    private boolean internal = false;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return (displayName != null) ? displayName : name;
    }

    public void addDescription(WdkModelText description) {
        this.descriptions.add(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDoNotTest(boolean doNotTest) {
	this.doNotTest = doNotTest;
    }

    @Override
    public boolean getDoNotTest() {
	return doNotTest;
    }

    public boolean isInternal() {
        return this.internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public Question getQuestion(String name) throws WdkModelException {
        Question question = questionMap.get(name);
        if (question == null)
            throw new WdkModelException("Question Set " + getName()
                    + " does not include question " + name);
        return question;
    }

    public boolean contains(String questionName) {
        return questionMap.containsKey(questionName);
    }

    @Override
    public Question getElement(String name) {
        return questionMap.get(name);
    }

    public Question[] getQuestions() {
        Question[] array = new Question[questionMap.size()];
        questionMap.values().toArray(array);
        return array;
    }
    
    public Map<String, Question> getQuestionMap() {
      return new LinkedHashMap<>(questionMap);
    }

    public void addQuestion(Question question) {
        question.setQuestionSet(this);
        if (questionList != null) questionList.add(question);
        else questionMap.put(question.getName(), question);
    }

    @Override
    public void resolveReferences(WdkModel model) throws WdkModelException {
        for (Question question : questionMap.values()) {
            question.resolveReferences(model);
        }
    }

    @Override
    public void setResources(WdkModel model) throws WdkModelException {
    // do nothing
    }

    @Override
    public String toString() {
        String newline = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer("QuestionSet: name='" + getName()
                + "'" + newline + "  displayName='" + getDisplayName() + "'"
                + newline + "  description='" + getDescription() + "'"
                + newline + "  internal='" + isInternal() + "'" + newline);
        buf.append(newline);

        for (Question question : questionMap.values()) {
            buf.append(newline);
            buf.append(":::::::::::::::::::::::::::::::::::::::::::::");
            buf.append(newline);
            buf.append(question);
            buf.append(newline);
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
        // exclude descriptions
        boolean hasDescription = false;
        for (WdkModelText description : descriptions) {
            if (description.include(projectId)) {
                if (hasDescription) {
                    throw new WdkModelException("The questionSet " + getName()
                            + " has more than one description for project "
                            + projectId);
                } else {
                    this.description = description.getText();
                    hasDescription = true;
                }
            }
        }
        descriptions = null;

        // exclude resources in each question
        for (Question question : questionList) {
            if (question.include(projectId)) {
                question.setQuestionSet(this);
                question.excludeResources(projectId);
                String questionName = question.getName();
                if (questionMap.containsKey(questionName))
                    throw new WdkModelException("Question named "
                            + questionName + " already exists in question set "
                            + getName());

                questionMap.put(questionName, question);
            }
        }
        questionList = null;
    }
}
