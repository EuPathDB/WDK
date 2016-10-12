package org.gusdb.wdk.service.service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.answer.report.ReporterFactory;
import org.gusdb.wdk.model.answer.report.ReporterRef;
import org.gusdb.wdk.model.jspwrap.AnswerValueBean;
import org.gusdb.wdk.model.jspwrap.RecordClassBean;
import org.gusdb.wdk.model.report.Reporter;
import org.gusdb.wdk.model.report.Reporter.ContentDisposition;
import org.gusdb.wdk.service.filter.RequestLoggingFilter;
import org.gusdb.wdk.service.request.DataValidationException;
import org.gusdb.wdk.service.request.RequestMisformatException;
import org.gusdb.wdk.service.request.answer.AnswerSpec;
import org.gusdb.wdk.service.request.answer.AnswerSpecFactory;
import org.gusdb.wdk.service.request.answer.AnswerDetails;
import org.gusdb.wdk.service.request.answer.AnswerDetailsFactory;
import org.gusdb.wdk.service.stream.AnswerStreamer;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>JSON input format:</p>
 * <pre>
 * {
 *   "questionDefinition": {
 *       see AnswerRequestFactory for details
 *   },
 *   formatting: {
 *     format: String,   (reporter internal name. optional.  if not provided, use WDK standard JSON)
 *     formatConfig: Any (sample for JSON, XML, etc. below)
 *   }
 * }
 * </pre>
 * <p>Sample input for our standard reporters:</p>
 * <pre>
 * formatConfig: {
 *   pagination: { offset: Number, numRecords: Number },   [only used by WDK standard JSON]
 *   attributes: [ attributeName: String ],
 *   tables: [ tableName: String ],
 *   sorting: [ { attributeName: String, direction: Enum[ASC,DESC] } ]  [so far, only used by WDK standard JSON]
 *   attachmentType: String    [eg "excel".  optional. if not provided, return in browser (default disposition of inline), using the default content type of the reporter. if provided, disposition is attachment, of this type, and file extension reflects this type.]
 *   includeEmptyTables: true/false
 * }
 * </pre>
 */
@Path("/answer")
public class AnswerService extends WdkService {

  private static final Logger LOG = Logger.getLogger(AnswerService.class);

  private static final String DEFAULT_JSON_FILENAME = "result.json";

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response buildResultFromForm(@FormParam("data") String data) throws WdkModelException, DataValidationException {
    // log this request's JSON here since filter will not log form data
    if (RequestLoggingFilter.isLogEnabled()) {
      RequestLoggingFilter.logRequest("POST", getUriInfo(),
          RequestLoggingFilter.toJsonBodyString(data));
    }
    return buildResult(data);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response buildResult(String body) throws WdkModelException, DataValidationException {
    try {

      JSONObject json = new JSONObject(body);

      // 1. Parse result request (question, params, etc.)

      JSONObject questionDefJson = json.getJSONObject("questionDefinition");
      AnswerSpec request = AnswerSpecFactory.createFromJson(questionDefJson, getWdkModelBean(), getSessionUser());

      // 2. Parse (optional) request specifics (columns, pagination, etc.)

      // use default if no formatting specified
      if (!json.has("formatting")) {
        // request is for standard JSON with default specifics
        return getStandardJsonResponse(request,
            AnswerDetailsFactory.createDefault(request.getQuestion()),
            ContentDisposition.INLINE);
      }

      // user passed formatting object; check to see if asking for default JSON
      JSONObject formatting = json.getJSONObject("formatting");

      // regardless of whether format is specified, formatConfig is now required
      if (!formatting.has("formatConfig")) {
        throw new BadRequestException("formatting object requires the formatConfig property");
      }
      JSONObject formatConfig = formatting.getJSONObject("formatConfig");

      // determine which formatter/reporter to use, or standard JSON if none (or service json reserved word) is specified
      return (formatting.has("format") &&
          !formatting.getString("format").equals(ReporterRef.WDK_SERVICE_JSON_REPORTER_RESERVED_NAME) ?

          // request is for a named format/reporter
          getReporterResponse(request, formatting.getString("format"), formatConfig) :
          
          // request is for standard JSON with configured specifics
          getStandardJsonResponse(request,
              AnswerDetailsFactory.createFromJson(formatConfig, request.getQuestion()),
              formatConfig.has("contentDisposition") ?
                  ContentDisposition.valueOf(formatConfig.getString("contentDisposition").toUpperCase()) :
                  ContentDisposition.INLINE));

    }
    catch (JSONException | RequestMisformatException e) {
      LOG.info("Passed request body deemed unacceptable", e);
      throw new BadRequestException(e);
    }
    catch (WdkUserException e) {
      throw new DataValidationException(e);
    }
  }

  private Response getStandardJsonResponse(AnswerSpec request,
      AnswerDetails requestSpecifics, ContentDisposition disposition) throws WdkModelException {

    // make an answer value
    AnswerValueBean answerValue = getResultFactory().createAnswer(request, requestSpecifics);

    // format to standard WDK JSON and stream response
    return applyDisposition(Response.ok(AnswerStreamer.getAnswerAsStream(answerValue, requestSpecifics))
        .type(MediaType.APPLICATION_JSON), disposition, DEFAULT_JSON_FILENAME).build();
  }

  private ResponseBuilder applyDisposition(ResponseBuilder response,
      ContentDisposition disposition, String filename) throws WdkModelException {
    switch(disposition) {
      case INLINE:
        response.header("Pragma", "Public");
        break;
      case ATTACHMENT:
        response.header("Content-disposition", "attachment; filename=" + filename);
        break;
      default:
        throw new WdkModelException("Unsupported content disposition: " + disposition);
    }
    return response;
  }

  private Response getReporterResponse(AnswerSpec request, String format, JSONObject formatConfig)
      throws WdkModelException, WdkUserException {

    AnswerValueBean answerValue = getResultFactory().createAnswer(request,
        AnswerDetailsFactory.createDefault(request.getQuestion()));

    RecordClassBean recordClass = answerValue.getQuestion().getRecordClass();
    if (!recordClass.getReporterMap().keySet().contains(format)) {
      throw new WdkUserException("Request for an invalid answer format: " + format);
    }

    LOG.info("Creating report '" + format + "' using AnswerValue with spec: " + answerValue.getSpecJson());
    Reporter reporter = ReporterFactory.getReporter(answerValue.getAnswerValue(), format, formatConfig);

    ResponseBuilder builder = Response.ok(AnswerStreamer.getAnswerAsStream(reporter))
        .type(reporter.getHttpContentType());

    return applyDisposition(builder, reporter.getContentDisposition(), reporter.getDownloadFileName()).build();
  }
}
