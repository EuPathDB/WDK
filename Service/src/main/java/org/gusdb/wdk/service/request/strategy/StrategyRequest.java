package org.gusdb.wdk.service.request.strategy;

import static org.gusdb.fgputil.json.JsonUtil.getStringOrDefault;
import static org.gusdb.fgputil.json.JsonUtil.getBooleanOrDefault;

import javax.ws.rs.ForbiddenException;

import org.gusdb.fgputil.functional.TreeNode;
import org.gusdb.wdk.core.api.JsonKeys;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.query.Query;
import org.gusdb.wdk.model.user.Step;
import org.gusdb.wdk.model.user.StepFactory;
import org.gusdb.wdk.model.user.User;
import org.gusdb.wdk.service.request.exception.DataValidationException;
import org.gusdb.wdk.service.request.exception.RequestMisformatException;
import org.gusdb.wdk.service.service.AbstractWdkService;
import org.json.JSONException;
import org.json.JSONObject;

public class StrategyRequest {
  private String _name;
  private String _savedName;
  private String _description;
  private boolean _isSaved;
  private boolean _isHidden;
  private boolean _isPublic;
  private TreeNode<Step> _stepTree;
  
  /**
   * Strategy Request JSON as follows:
   * {
   *   name: strategy name
   *   savedName: saved strategy name (optional)
   *   description: strategy description (optional - default empty string)
   *   isSaved: whether strategy should be saved (optional - default false)
   *   isHidden: whether strategy should be hidden (optional - default false)
   *   isPublic: whether strategy should be public (optional - default false)
   *   root: {
   *     id: 1234,
   *     left: {
   *       id: 2345,
   *       left: { id: 3456 },
   *       right: { id: 4567 }
   *     },
   *     right: {
   *       id: 5678,
   *       left: { id: 6789 }
   *     }
   *   }
   * }
   * 
   * @param name
   * @param savedName
   * @param description
   * @param isSaved
   * @param isHidden
   * @param isPublic
   * @param stepTree
   */
  public StrategyRequest(String name,
		                 String savedName,
		                 String description,
		                 boolean isSaved,
		                 boolean isHidden,
		                 boolean isPublic,
		                 TreeNode<Step> stepTree) {
    _name = name;
    _savedName = savedName;
    _description = description;
    _isSaved = isSaved;
    _isHidden = isHidden;
    _isPublic = isPublic;
    _stepTree = stepTree;
  }

  public static StrategyRequest createFromJson(JSONObject json, StepFactory stepFactory, User user, String projectId)
      throws WdkModelException, RequestMisformatException, DataValidationException {
    try {
    	  String name = json.getString(JsonKeys.NAME);
    	  String savedName = getStringOrDefault(json, JsonKeys.SAVED_NAME, "");
    	  String description = getStringOrDefault(json, JsonKeys.DESCRIPTION, "");
    	  boolean isSaved = getBooleanOrDefault(json, JsonKeys.IS_SAVED, false);
    	  boolean isHidden = getBooleanOrDefault(json, JsonKeys.IS_HIDDEN, false);
    	  boolean isPublic = getBooleanOrDefault(json, JsonKeys.IS_PUBLIC, false);
    	  JSONObject rootStepJson = json.getJSONObject(JsonKeys.ROOT_STEP);
    	  Step rootStep = stepFactory.getStepById(rootStepJson.getLong(JsonKeys.ID));
    	  TreeNode<Step> stepTree = buildStepTree(new TreeNode<Step>(rootStep), rootStepJson, stepFactory, user, projectId, new StringBuilder());
      return new StrategyRequest(name, savedName, description, isSaved, isHidden, isPublic, stepTree);
    }
    catch (JSONException e) {
      throw new RequestMisformatException(e.getMessage());
    }
    catch (WdkUserException e) {
    	  throw new DataValidationException(e);
    }
  }
  
  public static TreeNode<Step> buildStepTree(TreeNode<Step> stepTree,
		  JSONObject stepJson,
		  StepFactory stepFactory,
		  User user,
		  String projectId,
		  StringBuilder errors)
      throws WdkUserException, WdkModelException {
    if(stepJson.length() == 0) return stepTree;
    Step parentStep = stepTree.getContents();
    errors.append(validateStep(parentStep, user, projectId));
    if(stepJson.has(JsonKeys.LEFT_STEP)) {
    	  JSONObject leftStepJson = stepJson.getJSONObject(JsonKeys.LEFT_STEP);
    	  if(leftStepJson != null && leftStepJson.has(JsonKeys.ID)) {
    		Step leftStep = stepFactory.getStepById(leftStepJson.getLong(JsonKeys.ID));
    		parentStep.setPreviousStep(leftStep);
    	    TreeNode<Step> leftTreeNode = new TreeNode<>(leftStep);
    	    if(leftStepJson.has(JsonKeys.LEFT_STEP)) {
    	      stepTree.addChildNode(buildStepTree(leftTreeNode, leftStepJson.getJSONObject(JsonKeys.LEFT_STEP), stepFactory, user, projectId, errors));
    	    }
    	    else {
    	    	  stepTree.addChildNode(leftTreeNode);
    	    }
    	  }
    }
    if(stepJson.has(JsonKeys.RIGHT_STEP)) {
  	  JSONObject rightStepJson = stepJson.getJSONObject(JsonKeys.RIGHT_STEP);
  	  if(rightStepJson != null && rightStepJson.has(JsonKeys.ID)) {
  		Step rightStep = stepFactory.getStepById(rightStepJson.getLong(JsonKeys.ID));
  		parentStep.setChildStep(rightStep);
  	    TreeNode<Step> rightTreeNode = new TreeNode<>(rightStep);
  	    if(rightStepJson.has(JsonKeys.RIGHT_STEP)) {
  	      stepTree.addChildNode(buildStepTree(rightTreeNode, rightStepJson.getJSONObject(JsonKeys.RIGHT_STEP), stepFactory, user, projectId, errors));
  	    }
	    else {
	    	  stepTree.addChildNode(rightTreeNode);
	    }
  	  }
    }
    validateWiring(stepTree, errors);
    if(errors.length() > 0) throw new WdkUserException(errors.toString());
    return stepTree;
  }
  
  protected static String validateStep(Step step, User user, String projectId) throws WdkModelException {
	StringBuilder errors = new StringBuilder();
    if(step.getStrategyId() != null) {
    	  errors.append("Step " + step.getStepId() + " is already embedded in strategy with id " + step.getStrategyId() + "." + System.lineSeparator());
    }
    if(!projectId.equals(step.getProjectId())) {
    	  errors.append("Step " + step.getStepId() + " belongs to a project other than " + projectId + "." + System.lineSeparator());
    }
    if(step.getUser().getUserId() != user.getUserId()) {
      throw new ForbiddenException(AbstractWdkService.PERMISSION_DENIED);
    }
    if(step.isDeleted()) {
    	  errors.append("Step " + step.getStepId() + " is marked as deleted." + System.lineSeparator());
    }
    if(step.getPrimaryInputStep() != null  || step.getPrimaryInputStepId() != 0 ||
    	    step.getSecondaryInputStep() != null || step.getSecondaryInputStepId() != 0) {
    	  errors.append("Step " + step.getStepId() + " cannot already be wired." + System.lineSeparator());
    }
    return errors.toString();
  }
  
  protected static void validateWiring(TreeNode<Step> stepTree, StringBuilder errors) throws WdkUserException, WdkModelException {
    Step step = stepTree.getContents();
    int numExpectedChildren = step.getAnswerSpec().getQuestion().getQuery().getAnswerParamCount();
    if (numExpectedChildren == 2 && (step.getPrimaryInputStep() == null || step.getSecondaryInputStep() == null)) {
      errors.append("The boolean step " + step.getStepId() + " requires two input steps." + System.lineSeparator());
    }
    if (numExpectedChildren == 1 && (step.getPrimaryInputStep() == null || step.getSecondaryInputStep() != null)) {
      errors.append("The transform step " + step.getStepId() + " requires exactly one input step." + System.lineSeparator());
    }
    for(TreeNode<Step> subTree : stepTree.getChildNodes()) {
      validateWiring(subTree, errors);
    }
  }

  public String getName() {
    return _name;
  }

  public String getSavedName() {
    return _savedName;
  }

  public String getDescription() {
    return _description;
  }
  
  public boolean isSaved() {
    return _isSaved;
  }
  
  public boolean isHidden() {
    return _isHidden;
  }
  
  public boolean isPublic() {
    return _isPublic;
  }

  public TreeNode<Step> getStepTree() {
    return _stepTree;
  }
}
