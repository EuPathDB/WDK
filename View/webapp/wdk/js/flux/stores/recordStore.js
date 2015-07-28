import Store from '../core/store';
import {
  RECORD_DETAILS_ADDED,
  RECORD_TOGGLE_CATEGORY
} from '../constants/actionTypes';

function createStore({ dispatcher }) {
  let state = { records: {}, hiddenCategories: [] };
  return new Store(dispatcher, state, update);
}

function update(state, action) {
  switch (action.type) {
    case RECORD_DETAILS_ADDED:
      return addRecordDetails(state, action);
    case RECORD_TOGGLE_CATEGORY:
      return toggleCategory(state, action);
  }
}

function addRecordDetails(state, action) {
  let { meta, record } = action;
  let key = makeKey(meta.class, record.id);
  // merge `meta` and `record` with state.records[key]
  let recordData = state.records[key] || { meta: {}, record: {} };
  // link attribute value and meta
  let { attributes } = record;
  meta.attributes.forEach(function(attributeMeta) {
    let { name } = attributeMeta;
    if (name in attributes) {
      attributes[name] = createAttribute(attributeMeta, attributes[name]);
    }
  });
  Object.assign(recordData.meta, meta);
  Object.assign(recordData.record, record);
  state.records[key] = recordData;
  return state;
}

// FIXME Key by record class
function toggleCategory(state, action) {
  let { name, isVisible } = action;
  state.hiddenCategories = isVisible === false
    ? state.hiddenCategories.concat(name)
    : state.hiddenCategories.filter(function(n) {
      return n !== name;
    });
  return state;
}

function makeKey(recordClass, id) {
  // order keys
  let idStr = Object.keys(id).sort().map(name => `name=${id[name]}`).join('&');
  return recordClass + '?' + idStr;
}

function createAttribute(meta, value) {
  return Object.create(meta, {
    value: { value, enumerable: true }
  });
}

export default { createStore, makeKey };
