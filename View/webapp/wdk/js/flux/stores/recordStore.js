import Store from '../core/store';
import {
  RecordDetailsReceived
} from '../ActionType';
import indexBy from 'lodash/collection/indexBy';

function createStore({ dispatcher }) {
  let state = { records: {} };
  return new Store(dispatcher, state, update);
}

function update(state, action) {
  switch (action.type) {
    case RecordDetailsReceived:
      return addRecordDetails(state, action);
  }
}

function addRecordDetails(state, action) {
  let { meta, record } = action;
  let key = makeKey(meta.class, record.id);
  // merge `meta` and `record` with state.records[key]
  let recordData = state.records[key] || { meta: {}, record: {} };
  Object.assign(recordData.meta, meta);
  Object.assign(recordData.record, record);
  state.records[key] = recordData;
  return state;
}

function makeKey(recordClass, id) {
  // order keys
  let idStr = Object.keys(id).sort().map(name => `name=${id[name]}`).join('&');
  return recordClass + '?' + idStr;
}

export default { createStore, makeKey };
