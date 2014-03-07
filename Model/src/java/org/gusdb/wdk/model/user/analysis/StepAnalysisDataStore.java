package org.gusdb.wdk.model.user.analysis;

import java.util.Map;

import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.user.analysis.StepAnalysisFactory.AnalysisResult;

public interface StepAnalysisDataStore {

  public int getNextId() throws WdkModelException;

  public void insertAnalysis(int saId, int stepId, String displayName, String serializedContext) throws WdkModelException;

  public Map<Integer,StepAnalysisContext> getAnalysesByStepId(int stepId) throws WdkModelException;
  
  public boolean insertExecution(String contextHash) throws WdkModelException;

  public void updateExecution(String contextHash, ExecutionStatus status, String result) throws WdkModelException;

  public StepAnalysisContext getAnalysisById(int analysisId) throws WdkModelException;

  public AnalysisResult getAnalysisResult(String contextHash) throws WdkModelException;

  public void setAnalysisLog(String contextHash, String str) throws WdkModelException;

  public String getAnalysisLog(String contextHash) throws WdkModelException;

  public void deleteAnalysis(int analysisId) throws WdkModelException;

  
}
