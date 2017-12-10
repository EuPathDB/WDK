package org.gusdb.wdk.service.service.user;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.gusdb.fgputil.IoUtil;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.user.Step;
import org.gusdb.wdk.model.user.analysis.StepAnalysisContext;
import org.gusdb.wdk.service.UserBundle;

public class StepAnalysisService extends UserService {

  private static final Logger LOG = Logger.getLogger(StepAnalysisService.class);

  protected StepAnalysisService(@PathParam(USER_ID_PATH_PARAM) String uid) {
    super(uid);
  }

  @GET
  @Path("/steps/{stepId}/analyses/{analysisId}/properties")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getStepAnalysisProperties(
      @PathParam("stepId") String stepIdStr,
      @PathParam("analysisId") String analysisIdStr,
      @QueryParam("accessToken") String accessToken) throws WdkModelException {
    StepAnalysisContext context = getAnalysis(analysisIdStr, stepIdStr, accessToken);
    InputStream propertiesStream = getWdkModel().getStepAnalysisFactory().getProperties(context);
    return Response.ok(getStreamingOutput(propertiesStream)).build();
  }

  @PUT
  @Path("/steps/{stepId}/analyses/{analysisId}/properties")
  @Consumes(MediaType.TEXT_PLAIN)
  public Response setStepAnalysisProperties(
      @PathParam("stepId") String stepIdStr,
      @PathParam("analysisId") String analysisIdStr,
      @QueryParam("accessToken") String accessToken,
      InputStream body) throws WdkModelException, IOException {
    String bodyStr = IoUtil.readAllChars(new InputStreamReader(body));
    StepAnalysisContext context = getAnalysis(analysisIdStr, stepIdStr, accessToken);
    LOG.info("Properties body: " + bodyStr);
    getWdkModel().getStepAnalysisFactory().setProperties(context,
        new ByteArrayInputStream(bodyStr.getBytes(StandardCharsets.UTF_8.name())));
    return Response.noContent().build();
  }

  private StepAnalysisContext getAnalysis(String analysisIdStr, String stepIdStr, String accessToken) throws WdkModelException {
    try {
      long stepId = parseIdOrNotFound("step", stepIdStr);
      long analysisId = parseIdOrNotFound("step analysis", analysisIdStr);
      UserBundle userBundle = getUserBundle(Access.PUBLIC);
      StepAnalysisContext context = getWdkModel().getStepAnalysisFactory().getSavedContext(analysisId);
      Step step = context.getStep();
      if (stepId != step.getStepId()) {
        // step of this analysis does not match step in URL
        throw new NotFoundException("Step " + stepId + " does not contain analysis " + analysisId);
      }
      if (userBundle.getTargetUser().getUserId() != step.getUser().getUserId()) {
        // owner of this step does not match user in URL
        throw new NotFoundException("User " + userBundle.getTargetUser().getUserId() + " does not own step " + stepIdStr);
      }
      if (userBundle.isSessionUser() || context.getAccessToken().equals(accessToken)) {
        return context;
      }
      throw new ForbiddenException();
    }
    catch (WdkUserException e) {
      throw new NotFoundException(formatNotFound("step analysis: " + analysisIdStr));
    }
  }
}
