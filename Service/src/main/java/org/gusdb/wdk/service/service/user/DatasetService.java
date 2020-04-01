package org.gusdb.wdk.service.service.user;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.gusdb.wdk.core.api.JsonKeys;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkRuntimeException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.service.annotation.InSchema;
import org.gusdb.wdk.service.annotation.OutSchema;
import org.gusdb.wdk.service.request.exception.DataValidationException;
import org.gusdb.wdk.service.request.exception.RequestMisformatException;
import org.gusdb.wdk.service.request.user.DatasetRequestProcessor;
import org.gusdb.wdk.service.request.user.DatasetRequestProcessor.DatasetRequest;
import org.json.JSONException;
import org.json.JSONObject;


public class DatasetService extends UserService {

  public DatasetService(@PathParam(USER_ID_PATH_PARAM) String uid) {
    super(uid);
  }

  /**
   * Input JSON is:
   * <pre>
   * {
   *   "displayName": String (optional),
   *   "sourceType": Enum<IdList,Basket,Strategy,File> // more types to come...
   *   "sourceContent": {
   *     "ids": Array<String>,        // only for IdList
   *     "basketName": String,        // record class full name, only for basket
   *     "strategyId": Number,        // strategy id, only for strategy
   *     "temporaryFileId": String,   // temporary file id, only for file
   *     "parser": String,            // file content parser, only for file
   *     "parameterName": String,     // name of parameter that contains the parser configuration, only for file
   *     "questionName": String,      // name of question that contains the parameter associated w/ parameterName, only for file
   *   }
   * }
   * </pre>
   *
   * @param input request body (JSON)
   * @return HTTP response for this request
   */
  @POST
  @Path("datasets")
  @InSchema("wdk.users.datasets.post-request")
  @OutSchema("wdk.users.datasets.post-response")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addDatasetFromJson(JSONObject input)
      throws WdkModelException, RequestMisformatException, DataValidationException {
    try {
      var user    = getUserBundle(Access.PRIVATE).getSessionUser();
      var factory = getWdkModel().getDatasetFactory();
      var request = new DatasetRequest(input);
      var dataset = DatasetRequestProcessor.createFromRequest(request, user, factory, getSession());

      if (request.getDisplayName().isPresent()) {
        dataset.setName(request.getDisplayName().get());
        factory.saveDatasetMetadata(dataset);
      }

      return Response.ok(new JSONObject().put(JsonKeys.ID, dataset.getDatasetId())).build();
    }
    catch (JSONException e) {
      throw new RequestMisformatException(e.toString());
    }
    catch (WdkUserException e) {
      throw new DataValidationException(e);
    }
  }

  @GET
  @Path("datasets/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public StreamingOutput getDataset(@PathParam("id") long datasetId) throws WdkModelException {
    var factory = getWdkModel().getDatasetFactory();
    try {
      factory.getDatasetWithOwner(datasetId, getUserBundle(Access.PRIVATE).getTargetUser().getUserId());
    }
    catch (WdkUserException e) {
      throw new NotFoundException(formatNotFound("Dataset with ID " + datasetId));
    }

    return output -> {
      var buf  = new byte[8192];
      var read = 0;
      try {
        var stream = factory.getDatasetWithOwner(
          datasetId,
          getUserBundle(Access.PRIVATE).getTargetUser().getUserId()
        )
          .getContent()
          .getContentStream();

        do {
          read = stream.read(buf, 0, buf.length);
          output.write(buf, 0, read);
        } while (read > 0);

      } catch (WdkModelException | WdkUserException e) {
        throw new WdkRuntimeException(e);
      }
    };
  }
}
