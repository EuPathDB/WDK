package org.gusdb.wdk.controller.action;

import java.io.File;
import java.util.Random;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.gusdb.wdk.controller.ApplicationInitListener;
import org.gusdb.wdk.controller.CConstants;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.jspwrap.FlatVocabParamBean;
import org.gusdb.wdk.model.jspwrap.ParamBean;
import org.gusdb.wdk.model.jspwrap.QuestionBean;
import org.gusdb.wdk.model.jspwrap.QuestionSetBean;
import org.gusdb.wdk.model.jspwrap.UserBean;
import org.gusdb.wdk.model.jspwrap.WdkModelBean;

/**
 * This Action is called by the ActionServlet when a WDK question is requested.
 * It 1) finds the full name from the form, 2) gets the question from the WDK
 * model 3) forwards control to a jsp page that displays a question form
 */

public class ShowQuestionAction extends ShowQuestionSetsFlatAction {

    private static Logger logger = Logger.getLogger(ShowQuestionAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String qFullName = ((QuestionSetForm) form).getQuestionFullName();
        QuestionBean wdkQuestion = getQuestionByFullName(qFullName);

        QuestionForm qForm = prepareQuestionForm(wdkQuestion, request);

        QuestionSetForm qSetForm = (QuestionSetForm) request.getAttribute(CConstants.QUESTIONSETFORM_KEY);
        if (null == qSetForm) {
            qSetForm = new QuestionSetForm();
            request.setAttribute(CConstants.QUESTIONSETFORM_KEY, qSetForm);
        }
        qSetForm.setQuestionFullName(qFullName);
        prepareQuestionSetForm(getServlet(), qSetForm);

        ServletContext svltCtx = getServlet().getServletContext();
        String customViewDir = (String) svltCtx.getAttribute(CConstants.WDK_CUSTOMVIEWDIR_KEY);
        String customViewFile1 = customViewDir + File.separator
                + wdkQuestion.getFullName() + ".jsp";
        String customViewFile2 = customViewDir + File.separator
                + wdkQuestion.getRecordClass().getFullName() + ".question.jsp";
        String customViewFile3 = customViewDir + File.separator
                + CConstants.WDK_CUSTOM_QUESTION_PAGE;
        ActionForward forward = null;
        if (ApplicationInitListener.resourceExists(customViewFile1, svltCtx)) {
            forward = new ActionForward(customViewFile1);
        } else if (ApplicationInitListener.resourceExists(customViewFile2,
                svltCtx)) {
            forward = new ActionForward(customViewFile2);
        } else if (ApplicationInitListener.resourceExists(customViewFile3,
                svltCtx)) {
            forward = new ActionForward(customViewFile3);
        } else {
            forward = mapping.findForward(CConstants.SHOW_QUESTION_MAPKEY);
        }

        String gotoSum = request.getParameter(CConstants.GOTO_SUMMARY_PARAM);
        if (qForm.getParamsFilled() && "1".equals(gotoSum)) {
            forward = mapping.findForward(CConstants.SKIPTO_SUMMARY_MAPKEY);
            // System.out.println("SQA: form has all param vals, go to summary
            // page " + forward.getPath() + " directly");
        }

        return forward;

    }

    protected QuestionBean getQuestionByFullName(String qFullName) {
        int dotI = qFullName.indexOf('.');
        String qSetName = qFullName.substring(0, dotI);
        String qName = qFullName.substring(dotI + 1, qFullName.length());

        WdkModelBean wdkModel = (WdkModelBean) getServlet().getServletContext().getAttribute(
                CConstants.WDK_MODEL_KEY);

        QuestionSetBean wdkQuestionSet = (QuestionSetBean) wdkModel.getQuestionSetsMap().get(
                qSetName);
        QuestionBean wdkQuestion = (QuestionBean) wdkQuestionSet.getQuestionsMap().get(
                qName);
        return wdkQuestion;
    }

    protected QuestionForm prepareQuestionForm(QuestionBean wdkQuestion,
            HttpServletRequest request) throws WdkUserException,
            WdkModelException {
        // get the current user
        UserBean user = (UserBean) request.getSession().getAttribute(
                CConstants.WDK_USER_KEY);

        Random rand = new Random(System.currentTimeMillis());
        QuestionForm qForm = new QuestionForm();

        ActionServlet servlet = getServlet();
        qForm.setServlet(servlet);

        ServletContext context = servlet.getServletContext();
        ParamBean[] params = wdkQuestion.getParams();

        boolean hasAllParams = true;
        for (int i = 0; i < params.length; i++) {
            ParamBean p = params[i];
            Object pVal = null;
            if (p instanceof FlatVocabParamBean) {
                // not assuming fixed order, so call once, use twice.
                String[] flatVocab = ((FlatVocabParamBean) p).getVocab();
                qForm.getMyValues().put(p.getName(), flatVocab);
                qForm.getMyLabels().put(p.getName(),
                        getLengthBoundedLabels(flatVocab));
                String[] cgiParamValSet = request.getParameterValues(p.getName());
                if (cgiParamValSet != null && cgiParamValSet.length > 0) {
                    // use the user's selection from revise url
                    pVal = cgiParamValSet;
                } else { // no selection made, then use the default ones;
                    String defaultSelection = p.getDefault();
                    if (defaultSelection == null) {
                        // just select the first one as the default
                        pVal = new String[] { flatVocab[0] };
                    } else { // use the value by the author
                        pVal = new String[] { defaultSelection };
                    }
                }
            } else {
                String cgiParamVal = request.getParameter(p.getName());
                pVal = cgiParamVal;
            }

            // System.out.println("DEBUG: param " + p.getName() + " = '" + pVal
            // + "'");
            if (pVal == null) {
                hasAllParams = false;
                pVal = p.getDefault();
            }
            qForm.getMyProps().put(p.getName(), pVal);
        }
        qForm.setQuestion(wdkQuestion);
        qForm.setParamsFilled(hasAllParams);

        if (request.getParameter(CConstants.VALIDATE_PARAM) == "0") {
            qForm.setNonValidating();
        }

        request.setAttribute(CConstants.QUESTIONFORM_KEY, qForm);
        request.setAttribute(CConstants.WDK_QUESTION_KEY, wdkQuestion);

        return qForm;
    }

    static String[] getLengthBoundedLabels(String[] labels) {
        return getLengthBoundedLabels(labels, CConstants.MAX_PARAM_LABEL_LEN);
    }

    static String[] getLengthBoundedLabels(String[] labels, int maxLength) {
        Vector v = new Vector();
        int halfLen = maxLength / 2;
        for (String l : labels) {
            int len = l.length();
            if (len > CConstants.MAX_PARAM_LABEL_LEN) {
                l = l.substring(0, halfLen) + "..."
                        + l.substring(len - halfLen, len);
            }
            v.add(l);
        }
        String[] newLabels = new String[v.size()];
        v.copyInto(newLabels);
        return newLabels;
    }
}
