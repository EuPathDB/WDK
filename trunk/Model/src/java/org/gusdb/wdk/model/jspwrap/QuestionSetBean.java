package org.gusdb.wdk.model.jspwrap;

import org.gusdb.wdk.model.QuestionSet;
import org.gusdb.wdk.model.Question;

/**
 * A wrapper on a {@link QuestionSet} that provides simplified access for 
 * consumption by a view
 */ 
public class QuestionSetBean {

    QuestionSet questionSet;

    public QuestionSetBean(QuestionSet questionSet) {
	this.questionSet = questionSet;
    }

    public QuestionBean[] getQuestions() {
	Question[] questions = questionSet.getQuestions();
	QuestionBean[] questionBeans = new QuestionBean[questions.length];
	for (int i=0; i<questions.length; i++) {
	    questionBeans[i] = new QuestionBean(questions[i]);
	}
	return questionBeans;
    }

    public String getName() {
	return questionSet.getName();
    }

    public String getDisplayName() {
	return questionSet.getDisplayName();
    }

    public String getDescription(){
	return questionSet.getDescription();
    }
}
