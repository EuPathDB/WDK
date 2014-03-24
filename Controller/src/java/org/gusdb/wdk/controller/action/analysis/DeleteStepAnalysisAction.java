package org.gusdb.wdk.controller.action.analysis;

import org.gusdb.wdk.controller.actionutil.ActionResult;
import org.gusdb.wdk.controller.actionutil.ParamGroup;
import org.gusdb.wdk.model.user.analysis.StepAnalysisContext;

public class DeleteStepAnalysisAction extends AbstractStepAnalysisIdAction {

  @Override
  protected ActionResult handleRequest(ParamGroup params) throws Exception {
    StepAnalysisContext context = getContextFromPassedId();
    getAnalysisMgr().deleteAnalysis(context);
    return getStepAnalysisJsonResult(context);
  }
}
