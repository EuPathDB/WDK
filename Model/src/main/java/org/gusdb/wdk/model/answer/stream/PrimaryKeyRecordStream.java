package org.gusdb.wdk.model.answer.stream;

import java.util.Collection;
import java.util.Iterator;

import org.gusdb.fgputil.functional.Functions;
import org.gusdb.fgputil.iterator.IteratorUtil;
import org.gusdb.wdk.model.record.PrimaryKeyValue;
import org.gusdb.wdk.model.record.RecordClass;
import org.gusdb.wdk.model.record.RecordInstance;
import org.gusdb.wdk.model.record.StaticRecordInstance;

public class PrimaryKeyRecordStream implements RecordStream {

  private final RecordClass _recordClass;
  private final Collection<PrimaryKeyValue> _records;

  public PrimaryKeyRecordStream(RecordClass recordClass, Collection<PrimaryKeyValue> records) {
    _recordClass = recordClass;
    _records = records;
  }

  @Override
  public Iterator<RecordInstance> iterator() {
    return IteratorUtil.transform(_records.iterator(), Functions.fSwallow(pk ->
        new StaticRecordInstance(_recordClass, _recordClass, pk.getRawValues(), false)));
  }

  @Override
  public void close() {
    // nothing to do here
  }

}
