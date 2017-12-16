package org.gusdb.wdk.service.service.user;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gusdb.fgputil.functional.Functions;
import org.gusdb.fgputil.functional.TreeNode;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.user.Step;
import org.gusdb.wdk.model.user.StepFactory;
import org.gusdb.wdk.model.user.Strategy;
import org.gusdb.wdk.model.user.User;
import org.gusdb.wdk.service.formatter.StrategyFormatter;
import org.gusdb.wdk.service.request.exception.DataValidationException;
import org.gusdb.wdk.service.request.exception.RequestMisformatException;
import org.gusdb.wdk.service.request.strategy.StrategyRequest;
import org.gusdb.wdk.service.service.WdkService;
import org.json.JSONObject;


public class StrategyService extends UserService {
	
  public static final String STRATEGY_RESOURCE = "Strategy ID ";

  public StrategyService(@PathParam(USER_ID_PATH_PARAM) String uid) {
    super(uid);
  }
  
  @GET
  @Path("strategies")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStrategies() throws WdkModelException {
	User user = getPrivateRegisteredUser();
	List<Strategy> strategies = getWdkModel().getStepFactory().loadStrategies(user,false,false);
    return Response.ok(StrategyFormatter.getStrategiesJson(strategies, false).toString()).build();
  }
  
  @POST
  @Path("strategies")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createStrategy(String body) throws WdkModelException, DataValidationException {
    try {
      User user = getUserBundle(Access.PRIVATE).getSessionUser();
      JSONObject json = new JSONObject(body);
      StepFactory stepFactory = getWdkModel().getStepFactory();
      StrategyRequest strategyRequest = StrategyRequest.createFromJson(json, stepFactory, user, getWdkModel().getProjectId());
      TreeNode<Step> stepTree = strategyRequest.getStepTree();
      Step rootStep = stepTree.getContents();
     
      // Pull all the steps out of the tree
      List<Step> steps = stepTree.
    		  findAll(step -> true)
    		  .stream()
    		  .map(node -> node.getContents())
    		  .collect(Collectors.toList());
      
      // Update steps with filled in answer params and save.
      for(Step step : steps) {
    	    stepFactory.patchAnswerParams(step);
      }
      
      // Create the strategy
      Strategy strategy = stepFactory.createStrategy(user,
    		                           rootStep,
    		                           strategyRequest.getName(),
    		                           strategyRequest.getSavedName(),
    		                           strategyRequest.isSaved(),
    		                           strategyRequest.getDescription(),
    		                           strategyRequest.isHidden(),
    		                           strategyRequest.isPublic());
      
      // Add new strategy to all the embedded steps
      steps.stream().forEach(step -> step.setStrategyId(strategy.getStrategyId()));
      
      // Update left/right child ids in db first
      //rootStep.update(true);
      
      // Update those steps in the database with the strategyId
      stepFactory.setStrategyIdForThisAndUpstreamSteps(rootStep, strategy.getStrategyId());
      return Response.ok(StrategyFormatter.getStrategyJson(getStrategyForCurrentUser(Long.toString(strategy.getStrategyId())), true).toString()).build();
    }
    catch(WdkModelException wme) {
    	  throw new WdkModelException("Unable to create the strategy.", wme);
    }
    catch(RequestMisformatException rmfe) {
    	  throw new BadRequestException(rmfe);
    }
    catch(WdkUserException wue) {
    	  throw new DataValidationException(wue);
    }
  }
  
  
  
  @GET
  @Path("strategies/{strategyId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStrategy(@PathParam("strategyId") String strategyId) throws WdkModelException {
    return Response.ok(StrategyFormatter.getStrategyJson(getStrategyForCurrentUser(strategyId), true).toString()).build();
  }
  
  protected Strategy getStrategyForCurrentUser(String strategyId) {
    try {
      User user = getUserBundle(Access.PRIVATE).getSessionUser();
      // Whether the user owns this strategy or not is resolved in the getStepFactory method
      Strategy strategy = getWdkModel().getStepFactory().getStrategyById(user, Long.parseLong(strategyId));
      if (strategy.getUser().getUserId() != user.getUserId()) {
        throw new ForbiddenException(WdkService.PERMISSION_DENIED);
      }
      return strategy;
    }
    catch (NumberFormatException | WdkUserException | WdkModelException e) {
      throw new NotFoundException(WdkService.formatNotFound(STRATEGY_RESOURCE + strategyId));
    }
  }

}
