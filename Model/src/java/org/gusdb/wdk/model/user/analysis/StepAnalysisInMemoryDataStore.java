package org.gusdb.wdk.model.user.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.gusdb.fgputil.FormatUtil;
import org.gusdb.wdk.model.WdkModel;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.user.analysis.StepAnalysisFactory.AnalysisResult;

public class StepAnalysisInMemoryDataStore implements StepAnalysisDataStore {

  private static final Logger LOG = Logger.getLogger(StepAnalysisInMemoryDataStore.class);
  
  /**
   * Eventual table will have:
   *   analysisId(PK), stepId, displayName, isNew, context CLOB
   */
  // will map analysisId -> context
  private static Map<Integer, String> ANALYSIS_CONTEXT_MAP = new HashMap<>();
  // will map analysisId -> displayName
  private static Map<Integer, String> ANALYSIS_NAME_MAP = new HashMap<>();
  // will map analysisId -> isNew
  private static Map<Integer, Boolean> ANALYSIS_NEW_MAP = new HashMap<>();
  // will map stepId -> List<analysisId>
  private static Map<Integer, List<Integer>> STEP_ANALYSIS_MAP = new HashMap<>();

  /**
   * Eventual table will have:
   *   contextHash(PK), status, log CLOB, result CLOB
   */
  // will map contextHash -> String[]{ status, log, result }
  private static Map<String, String[]> ANALYSIS_STATUS_TABLE = new LinkedHashMap<>();  

  private static AtomicInteger ID_SEQUENCE = new AtomicInteger(0);

  private WdkModel _wdkModel;
  
  public StepAnalysisInMemoryDataStore(WdkModel wdkModel) {
    _wdkModel = wdkModel;
  }

  @Override
  public int getNextId() throws WdkModelException {
    return ID_SEQUENCE.incrementAndGet();
  }

  @Override
  public void insertAnalysis(int saId, int stepId, String displayName,
      String serializedContext) throws WdkModelException {
    synchronized(STEP_ANALYSIS_MAP) {
      if (!STEP_ANALYSIS_MAP.containsKey(stepId)) {
        STEP_ANALYSIS_MAP.put(stepId, new ArrayList<Integer>());
      }
      STEP_ANALYSIS_MAP.get(stepId).add(saId);
      ANALYSIS_CONTEXT_MAP.put(saId, serializedContext);
      ANALYSIS_NAME_MAP.put(saId, displayName);
      ANALYSIS_NEW_MAP.put(saId, true);
      LOG.info("Inserted analysis with ID " + saId + ", map now: " + FormatUtil.prettyPrint(ANALYSIS_CONTEXT_MAP));
    }
  }

  @Override
  public List<StepAnalysisContext> getAllAnalyses() throws WdkModelException {
    List<StepAnalysisContext> contextList = new ArrayList<>();
    synchronized(STEP_ANALYSIS_MAP) {
      for (Integer id : ANALYSIS_CONTEXT_MAP.keySet()) {
        contextList.add(getAnalysisById(id));
      }
    }
    return contextList;
  }

  @Override
  public Map<Integer,StepAnalysisContext> getAnalysesByStepId(int stepId) throws WdkModelException {
    Map<Integer,StepAnalysisContext> contextMap = new LinkedHashMap<>();
    synchronized(STEP_ANALYSIS_MAP) {
      if (STEP_ANALYSIS_MAP.containsKey(stepId)) {
        for (Integer id : STEP_ANALYSIS_MAP.get(stepId)) {
          contextMap.put(id, getAnalysisById(id));
        }
      }
    }
    return contextMap;
  }
  
  @Override
  public boolean insertExecution(String contextHash, ExecutionStatus initialStatus)
      throws WdkModelException {
    synchronized(ANALYSIS_STATUS_TABLE) {
      if (ANALYSIS_STATUS_TABLE.containsKey(contextHash)) {
        return false;
      }
      ANALYSIS_STATUS_TABLE.put(contextHash, new String[]{ initialStatus.name(), "", "" });
      return true;
    }
  }

  @Override
  public void updateExecution(String contextHash, ExecutionStatus status, String result) throws WdkModelException {
    synchronized(ANALYSIS_STATUS_TABLE) {
      if (ANALYSIS_STATUS_TABLE.containsKey(contextHash)) {
        String[] old = ANALYSIS_STATUS_TABLE.get(contextHash);
        old[0] = status.name();
        old[2] = (result == null ? "" : result);
        LOG.info("Updated result record for hash[" + contextHash + "], status=" + status + ", result =\n" + result);
        return;
      }
      throw new WdkModelException("Step Analysis Execution for hash [" + contextHash + "] does not exist.");
    }
  }

  @Override
  public void renameAnalysis(int analysisId, String displayName) throws WdkModelException {
    synchronized(ANALYSIS_CONTEXT_MAP) {
      if (ANALYSIS_NAME_MAP.containsKey(analysisId)) {
        ANALYSIS_NAME_MAP.put(analysisId, displayName);
        return;
      }
      throw new WdkModelException("No analysis exists with id: " + analysisId);
    }
  }

  @Override
  public void setNewFlag(int analysisId, boolean isNew) throws WdkModelException {
    synchronized(ANALYSIS_CONTEXT_MAP) {
      if (ANALYSIS_NEW_MAP.containsKey(analysisId)) {
        ANALYSIS_NEW_MAP.put(analysisId, isNew);
        return;
      }
      throw new WdkModelException("No analysis exists with id: " + analysisId);
    }
  }

  @Override
  public void updateContext(int analysisId, String serializedContext) throws WdkModelException {
    synchronized(ANALYSIS_CONTEXT_MAP) {
      if (ANALYSIS_CONTEXT_MAP.containsKey(analysisId)) {
        ANALYSIS_CONTEXT_MAP.put(analysisId, serializedContext);
        return;
      }
      throw new WdkModelException("No analysis exists with id: " + analysisId);
    }
  }
  
  @Override
  public StepAnalysisContext getAnalysisById(int analysisId) throws WdkModelException {
    synchronized(ANALYSIS_CONTEXT_MAP) {
      if (ANALYSIS_CONTEXT_MAP.containsKey(analysisId)) {
        StepAnalysisContext context = StepAnalysisContext.createFromStoredData(_wdkModel, analysisId,
            ANALYSIS_NAME_MAP.get(analysisId), ANALYSIS_CONTEXT_MAP.get(analysisId));
        ExecutionStatus storedStatus = getExecutionStatus(context.createHash());
        // need to resolve and assign status for this analysis
        context.setStatus(ExecutionStatus.resolveStatus(ANALYSIS_NEW_MAP.get(analysisId), storedStatus));
        return context;
      }
      return null;
    }
  }
  
  @Override
  public ExecutionStatus getExecutionStatus(String contextHash) throws WdkModelException {
    synchronized(ANALYSIS_STATUS_TABLE) {
      if (ANALYSIS_STATUS_TABLE.containsKey(contextHash)) {
        String value = ANALYSIS_STATUS_TABLE.get(contextHash)[0];
        try {
          return ExecutionStatus.valueOf(value);
        }
        catch (IllegalArgumentException | NullPointerException e) {
          throw new WdkModelException("Status value in database '" + value +
              "' is not a valid execution status.");
        }
      }
      return null;
    }
  }
  
  @Override
  public void deleteAnalysis(int analysisId) throws WdkModelException {
    synchronized(STEP_ANALYSIS_MAP) {
      if (!ANALYSIS_CONTEXT_MAP.containsKey(analysisId)) {
        LOG.info("Unable to find value for analysis ID " + analysisId + " in map: " + FormatUtil.prettyPrint(ANALYSIS_CONTEXT_MAP));
        throw new WdkModelException("Analysis ID to be deleted [ " + analysisId + " ] does not exist.");
      }
      ANALYSIS_CONTEXT_MAP.remove(analysisId);
      ANALYSIS_NAME_MAP.remove(analysisId);
      ANALYSIS_NEW_MAP.remove(analysisId);
      List<Integer> stepIdsToRemove = new ArrayList<>();
      for (Entry<Integer,List<Integer>> entry : STEP_ANALYSIS_MAP.entrySet()) {
        entry.getValue().remove((Integer)analysisId);
        if (entry.getValue().isEmpty()) {
          stepIdsToRemove.add(entry.getKey());
        }
      }
      // must do this afterward to avoid concurrent modification exception (on iterator above)
      for (Integer stepId : stepIdsToRemove) {
        STEP_ANALYSIS_MAP.remove(stepId);
      }
    }
  }

  @Override
  public AnalysisResult getAnalysisResult(String contextHash) throws WdkModelException {
    if (ANALYSIS_STATUS_TABLE.containsKey(contextHash)) {
      String[] result = ANALYSIS_STATUS_TABLE.get(contextHash);
      return new AnalysisResult(ExecutionStatus.valueOf(result[0]), result[2], result[1]);
    }
    LOG.warn("Could not find analysis result for hash: " + contextHash + ". Available hashes: " +
        FormatUtil.arrayToString(new ArrayList<String>(ANALYSIS_STATUS_TABLE.keySet()).toArray()));
    return null;
  }

  @Override
  public void setAnalysisLog(String contextHash, String str) throws WdkModelException {
    synchronized(ANALYSIS_STATUS_TABLE) {
      if (ANALYSIS_STATUS_TABLE.containsKey(contextHash)) {
        String[] result = ANALYSIS_STATUS_TABLE.get(contextHash);
        result[1] = str;
        return;
      }
      throw new WdkModelException("No analysis execution with context hash value: " + contextHash);
    }
  }

  @Override
  public String getAnalysisLog(String contextHash) throws WdkModelException {
    if (ANALYSIS_STATUS_TABLE.containsKey(contextHash)) {
      String[] result = ANALYSIS_STATUS_TABLE.get(contextHash);
      return result[1];
    }
    throw new WdkModelException("No analysis execution with context hash value: " + contextHash);
  }
}
