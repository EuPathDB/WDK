package org.gusdb.wdk.controller.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.gusdb.wdk.controller.CConstants;
import org.gusdb.wdk.controller.actionutil.ActionUtility;
import org.gusdb.wdk.controller.form.QuestionForm;
import org.gusdb.wdk.model.Utilities;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.jspwrap.AnswerValueBean;
import org.gusdb.wdk.model.jspwrap.DatasetBean;
import org.gusdb.wdk.model.jspwrap.DatasetParamBean;
import org.gusdb.wdk.model.jspwrap.ParamBean;
import org.gusdb.wdk.model.jspwrap.QuestionBean;
import org.gusdb.wdk.model.jspwrap.RecordClassBean;
import org.gusdb.wdk.model.jspwrap.StepBean;
import org.gusdb.wdk.model.jspwrap.StrategyBean;
import org.gusdb.wdk.model.jspwrap.UserBean;

/**
 * This Action is called by the ActionServlet when a WDK question is asked. It
 * 1) reads param values from input form bean, 2) runs the query and saves the
 * answer 3) forwards control to a jsp page that displays a summary
 */

public class ProcessQuestionAction extends Action {

    private static final Logger logger = Logger.getLogger(ProcessQuestionAction.class);

    public static Map<String, String> prepareParams(UserBean user,
            HttpServletRequest request, QuestionForm qform)
            throws WdkModelException, WdkUserException, FileNotFoundException,
            IOException, SQLException {
        Map<String, String> paramValues = new HashMap<String, String>();
        QuestionBean question = qform.getQuestion();
        if (question == null)
            throw new WdkUserException("The question '"
                    + request.getParameter(CConstants.QUESTION_FULLNAME_PARAM)
                    + "' doesn't exist.");

        Map<String, ParamBean<?>> params = question.getParamsMap();
        // convert from raw data to user dependent data
        for (String paramName : params.keySet()) {
        	ParamBean<?> param = params.get(paramName);

            String rawValue = (String) qform.getValue(paramName);
            logger.debug("Param raw: " + paramName + " = " + rawValue);
            String dependentValue = null;
            if (param instanceof DatasetParamBean) {
                // get the input type
                String type = request.getParameter(paramName + "_type");
                if (type == null)
                    throw new WdkUserException("Missing input parameter: "
                            + paramName + "_type.");

                RecordClassBean recordClass = ((DatasetParamBean) param).getRecordClass();
                String data = null;
                String uploadFile = "";
                if (type.equalsIgnoreCase("data")) {
                    data = request.getParameter(paramName + "_data");
                } else if (type.equalsIgnoreCase("file")) {
                    FormFile file = (FormFile) qform.getValue(paramName
                            + "_file");
                    uploadFile = file.getFileName();
                    logger.debug("upload file: " + uploadFile);
                    data = new String(file.getFileData());
                } else if (type.equalsIgnoreCase("basket")) {
                    data = user.getBasket(recordClass);
                } else if (type.equals("strategy")) {
                    String strId = request.getParameter(paramName + "_strategy");
                    int displayId = Integer.parseInt(strId);
                    StrategyBean strategy = user.getStrategy(displayId);
                    StepBean step = strategy.getLatestStep();
                    data = step.getAnswerValue().getAllIdList();
                }

                logger.debug("dataset data: '" + data + "'");
                if (data != null && data.trim().length() > 0) {
                    DatasetBean dataset = user.createDataset(recordClass,
                            uploadFile, data);
                    dependentValue = Integer.toString(dataset.getUserDatasetId());
                }
            } else if (rawValue != null && rawValue.length() > 0) {
            	// check to see if this param is dependent and assign depended value
                dependentValue = param.rawOrDependentValueToDependentValue(user, rawValue);
            }
            logger.debug("param " + paramName + " - " + param.getClass().getSimpleName() + " = " + dependentValue);
            paramValues.put(paramName, dependentValue);
        }
        return paramValues;
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        logger.debug("Entering ProcessQuestionAction..");
        QuestionForm qForm = (QuestionForm) form;
        try {
            UserBean wdkUser = ActionUtility.getUser(servlet, request);

            // get question
            String qFullName = request.getParameter(CConstants.QUESTION_FULLNAME_PARAM);

            // the params has been validated, and now is parsed, and if the size
            // of the value is too long, ti will be replaced is checksum
            Map<String, String> params = prepareParams(wdkUser, request, qForm);
            
            // get the assigned weight
            String strWeight = request.getParameter(CConstants.WDK_ASSIGNED_WEIGHT_KEY);
            boolean hasWeight = (strWeight != null && strWeight.length() > 0);
            int weight = Utilities.DEFAULT_WEIGHT;
            if (hasWeight) {
                if (!strWeight.matches("[\\-\\+]?\\d+"))
                    throw new WdkUserException("Invalid weight value: '"
                            + strWeight
                            + "'. Only integer numbers are allowed.");
                if (strWeight.length() > 9)
                    throw new WdkUserException("Weight number is too big: "
                            + strWeight);
                weight = Integer.parseInt(strWeight);
            }

            QuestionBean wdkQuestion = qForm.getQuestion();
            // the question is already validated in the question form, don't need to do it again.
            AnswerValueBean answerValue = wdkQuestion.makeAnswerValue(wdkUser,
                    params, false, weight);
            logger.debug("Test run search [" + qFullName
                    + "] and get # of results: " + answerValue.getResultSize());

            /*
             * Charles Treatman 4/23/09 Add code here to set the
             * current_application_tab cookie so that user will go to the Run
             * Strategies tab after running a question from a question page.
             */
            ShowApplicationAction.setWdkTabStateCookie(request, response);

            ActionForward forward = getSuccessForward(request, mapping,
                    qFullName, params, weight);
            logger.debug("Leaving ProcessQuestionAction successfully, forward to "
                    + forward.getPath());
            return forward;
        }
        catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();

            ActionMessages messages = new ActionErrors();
            ActionMessage message = new ActionMessage("mapped.properties",
                    qForm.getQuestion().getDisplayName(), ex.getMessage());
            messages.add(ActionErrors.GLOBAL_MESSAGE, message);
            saveErrors(request, messages);
            ActionForward forward = mapping.getInputForward();
            return forward;
        }
    }

    private ActionForward getSuccessForward(HttpServletRequest request,
            ActionMapping mapping, String qFullName,
            Map<String, String> params, int weight)
            throws UnsupportedEncodingException {

        // construct the url to summary page
        ActionForward showSummary = mapping.findForward(CConstants.PQ_SHOW_SUMMARY_MAPKEY);
        StringBuffer url = new StringBuffer(showSummary.getPath());
        url.append("?" + CConstants.QUESTION_FULLNAME_PARAM + "=" + qFullName);
        for (String paramName : params.keySet()) {
            String paramValue = params.get(paramName);
            url.append("&"
                    + URLEncoder.encode("value(" + paramName + ")", "utf-8"));
            url.append("=");
            if (paramValue != null)
                url.append(URLEncoder.encode(paramValue, "utf-8"));
        }

        // check if user want to define the output size for the answer
        String altPageSizeKey = request.getParameter(CConstants.WDK_ALT_PAGE_SIZE_KEY);
        if (altPageSizeKey != null && altPageSizeKey.length() > 0) {
            url.append("&" + CConstants.WDK_ALT_PAGE_SIZE_KEY);
            url.append("=" + altPageSizeKey);
        }

        // pass along the skip param
        String skipToDownloadKey = request.getParameter(CConstants.WDK_SKIPTO_DOWNLOAD_PARAM);
        logger.debug("skipto download: " + skipToDownloadKey);
        if (skipToDownloadKey != null && skipToDownloadKey.length() > 0) {
            url.append("&" + CConstants.WDK_SKIPTO_DOWNLOAD_PARAM);
            url.append("=" + skipToDownloadKey);

            // pass the reporter format if present
            String format = request.getParameter("wdkReportFormat");
            if (format != null && format.length() > 0)
              url.append("&wdkReportFormat=" + format);
        }

        url.append("&" + CConstants.WDK_ASSIGNED_WEIGHT_KEY + "=" + weight);

        // pass the noStrategy flag to showSummary
        String noStrategy = request.getParameter(CConstants.WDK_NO_STRATEGY_PARAM);
        if (noStrategy != null && noStrategy.length() > 0) {
            url.append("&" + CConstants.WDK_NO_STRATEGY_PARAM + "="
                    + noStrategy);
        }

        // pass no_skip to showSummary
        String noSkip = request.getParameter("noskip");
        if (noSkip != null && noSkip.length() > 0) {
            url.append("&noskip=" + noSkip);
        }

        // pass custom name to showSummary
        String customName = request.getParameter("customName");
        if (customName != null && customName.length() > 0) {
            url.append("&customName=" + customName);
        }

        // construct the forward to show_summary action
        ActionForward forward = new ActionForward(url.toString());
        forward.setRedirect(true);
        return forward;
    }
}
