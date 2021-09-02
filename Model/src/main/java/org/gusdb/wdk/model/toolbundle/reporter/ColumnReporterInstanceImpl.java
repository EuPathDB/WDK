package org.gusdb.wdk.model.toolbundle.reporter;

import static org.gusdb.fgputil.functional.Functions.mapException;

import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gusdb.fgputil.Timer;
import org.gusdb.fgputil.db.runner.SQLRunner;
import org.gusdb.fgputil.db.runner.SQLRunnerException;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.answer.AnswerValue;
import org.gusdb.wdk.model.answer.stream.RecordStreamFactory;
import org.gusdb.wdk.model.answer.stream.SingleAttributeRecordStream;
import org.gusdb.wdk.model.record.attribute.AttributeField;
import org.gusdb.wdk.model.report.Reporter;
import org.gusdb.wdk.model.report.ReporterConfigException;
import org.gusdb.wdk.model.report.ReporterInfo;
import org.gusdb.wdk.model.toolbundle.ColumnReporter;
import org.gusdb.wdk.model.toolbundle.ColumnReporterInstance;
import org.gusdb.wdk.model.toolbundle.ColumnToolConfig;
import org.gusdb.wdk.model.toolbundle.impl.AbstractColumnToolInstance;
import org.json.JSONObject;

public class ColumnReporterInstanceImpl extends AbstractColumnToolInstance implements ColumnReporterInstance {

  private static final Logger LOG = Logger.getLogger(ColumnReporterInstanceImpl.class);

  private final ColumnReporter _reporter;
  private final ColumnProcessor _processor;

  protected ColumnReporterInstanceImpl(AnswerValue answerValue,
      AttributeField column, ColumnToolConfig config,
      ColumnReporter reporter, ColumnProcessor processor) {
    super(answerValue, column, config);
    _reporter = reporter;
    _processor = processor;
  }

  @Override
  public void report(OutputStream out) throws WdkModelException {
    Timer t = new Timer();
    AnswerValue fullAnswer = getAnswerValue().cloneWithNewPaging(1, -1);
    try (var records = RecordStreamFactory.getRecordStream(fullAnswer,
        Collections.singletonList(getColumn()), Collections.emptyList())) {
      _processor.initialize(out);
      LOG.info("Time to build record stream and initialize column processor: " + t.getElapsedStringAndRestart());
      boolean firstRecord = true;
      for (var record : records) {
        if (firstRecord) {
          LOG.info("Time to run SQL: " + t.getElapsedStringAndRestart());
          firstRecord = false;
        }
        // convert record into the attribute field specified for this reporter
        _processor.processValue(mapException(
            () -> record.getAttributeValue(getColumn().getName()).getValue(),
            e -> WdkModelException.translateFrom(e)), out);
      }
      LOG.info("Time to process values in column reporter: " + t.getElapsedString());
      _processor.complete(out);
    }
  }

  // TODO: delete; this was just to try to trim down runtime (but failed)
  public void reportWithRawSql(OutputStream out) throws WdkModelException {
    Timer t = new Timer();
    AnswerValue fullAnswer = getAnswerValue().cloneWithNewPaging(1, -1);
    try (SingleAttributeRecordStream records = (SingleAttributeRecordStream)RecordStreamFactory.getRecordStream(fullAnswer,
        Collections.singletonList(getColumn()), Collections.emptyList())) {
      _processor.initialize(out);
      LOG.info("Time to build record stream and initialize column processor: " + t.getElapsedStringAndRestart());
      new SQLRunner(_answer.getWdkModel().getAppDb().getDataSource(), records.getSql()).executeQuery(rs -> {
        try {
          LOG.info("Time to run SQL: " + t.getElapsedStringAndRestart());
          while (rs.next()) {
            _processor.processValue(rs.getString(getColumn().getName()), out);
          }
          return null;
        }
        catch (WdkModelException e) {
          throw new SQLRunnerException(e);
        }
      });
      LOG.info("Time to process values in column reporter: " + t.getElapsedString());
      _processor.complete(out);
    }
    catch (Exception e) {
      throw WdkModelException.translateFrom(e);
    }
  }

  @Override
  public String getHttpContentType() {
    return "application/json";
  }

  @Override
  public ContentDisposition getContentDisposition() {
    return ContentDisposition.INLINE;
  }

  @Override
  public String getDownloadFileName() {
    return getColumn().getName() + "_" + _reporter.getKey() + ".json";
  }

  @Override
  public String getHelp() {
    return _reporter.getInputSpec(getColumn().getDataType()).build().toString();
  }

  @Override
  public void setProperties(ReporterInfo reporterInfo) throws WdkModelException {
    // no-op; properties are accessible via column reporter
  }

  @Override
  public Reporter configure(Map<String, String> config) throws ReporterConfigException, WdkModelException {
    throw new UnsupportedOperationException("Configuration handled via constructor.");
  }

  @Override
  public Reporter configure(JSONObject config) throws ReporterConfigException, WdkModelException {
    throw new UnsupportedOperationException("Configuration handled via constructor.");
  }
}