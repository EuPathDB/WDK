// TODO Break this into two stores: Answers and UI
import {
  assign,
  curry,
  flattenDeep,
  indexBy,
  property,
  values
} from 'lodash';
import Store from '../core/store';
import {
  AnswerAdded,
  AnswerMoveColumn,
  AnswerChangeAttributes,
  AnswerFilter,
  AnswerLoading
} from '../ActionType';

/**
 * This module is exporting a store class (not an instance).
 *
 * `new Store(...)` is returning a class. The class constructor is called during
 * runtime to create a singleton instance. An instance of this store can be
 * retreived by a Controller View  using the `lookup` function that is passed to
 * it via `props`, e.g.:
 *
 *     // Some Controller View
 *     ...
 *
 *     componentDidMount() {
 *       let customStore = this.props.lookup(CustomStore);
 *       // do stuff with customStore...
 *     }
 *
 *     ...
 *
 *
 * The class will contain three methods and one property:
 *
 *   - subscribe(callback)   // register a funtion to be called when emitChange is called
 *   - unsubscribe(callback) // unregister a function
 *   - getState()            // get the current state of the store
 *
 *
 * The spec object passed to Store is used to define the behavior of this store
 * when an action is dispatched. All methods on the spec object are called with
 * the spec object as the receiver, so `this` in methods will refer to the spec
 * object. This makes it possible to factor out action handlers into specific
 * methods on your spec object.
 *
 *
 * This store retains the UI state for the AnswerPage, including the current
 * Answer resource being displayed.. UI state includes things like loading
 * state, error state, table sorting options, etc.
 */


// Split terms on whitespace, unless wrapped in quotes
let parseSearchTerms = function parseSearchTerms(terms) {
  let match = terms.match(/\w+|"[\w\s]*"/g) || [];
  return match.map(function(term) {
    // remove wrapping quotes from phrases
    return term.replace(/(^")|("$)/g, '');
  });
};


// Search record for a term.
//
// The approach here is pretty basic and probably ineffecient:
//   - Convert all attribute values to an array of values.
//   - Convert all table values to a flat array of values.
//   - Combine the above arrays into a single array.
//   - Join the array with a control character.
//   - Search the resulting string for the index of 'term'.
//   - return (index !== -1).
//
// There is much room for performance tuning here.
let isTermInRecord = curry(function isTermInRecord(term, record) {
  let attributeValues = values(record.attributes);

  let tableValues = flattenDeep(values(record.tables)
    .map(function(table) {
      return table.map(values);
    }));

  let clob = attributeValues.concat(tableValues).join('\0');

  return clob.toLowerCase().indexOf(term.toLowerCase()) !== -1;
});

function createStore({ dispatcher }) {
  let value = {
    isLoading: false,
    filterTerm: '',
    filteredRecords: null,
    answers: {},
    displayInfo: {
      sorting: null,
      pagination: null,
      attributes: null,
      tables: null
    },
    questionDefinition: {
      questionName: null,
      params: null,
      filters: null
    }
  };
  return new Store(dispatcher, value, update);
}

function update(state, action) {
  switch(action.type) {
    case AnswerAdded: return addAnswer(state, action);
    case AnswerMoveColumn: return moveTableColumn(state, action);
    case AnswerChangeAttributes: return updateVisibleAttributes(state, action);
    case AnswerFilter: return filterAnswer(state, action);
    case AnswerLoading: return answerLoading(state, action);
  }
}


/**
 * answer = {
 *   meta,
 *   records: [{ id, attributes, tables }]
 * }
 *
 * requestData = {
 *   displayInfo,
 *   questionDefinition
 * }
 *
 * requestData is an object with the keys `displayInfo` and
 * `questionDefinition`. We will be merging these keys into `state`
 * below.
 */
function addAnswer(state, { answer, requestData }) {
  let questionName = requestData.questionDefinition.questionName;
  let previousQuestionName = state.questionDefinition.questionName;

  /*
   * If state.displayInfo.attributes isn't defined we want to use the
   * defaults. For now, we will just show whatever is in
   * answer.meta.attributes by default. This is probably wrong.
   * We probably also want to persist the user's choice somehow. Using
   * localStorage is one possble solution.
   */
  if (!requestData.displayInfo.visibleAttributes || previousQuestionName !== questionName) {
    requestData.displayInfo.visibleAttributes = answer.meta.summaryAttributes.map(attrName => {
      return answer.meta.attributes.find(attr => attr.name === attrName);
    });
  }

  answer.meta.attributes = answer.meta.attributes
    .filter(attr => attr.name != 'wdk_weight');

  /*
   * This will update the keys `filteredRecords`, `displayInfo`, and
   * `questionDefinition` in `state`.
   */
  assign(state, {
    filteredRecords: answer.records,
    displayInfo: requestData.displayInfo,
    questionDefinition: requestData.questionDefinition
  });

  state.answers[questionName] = answer;
  return state;
}

/**
 * Update the position of an attribute in the answer table.
 *
 * @param {string} columnName The name of the atribute being moved.
 * @param {number} newPosition The 0-based index to move the attribute to.
 */
function moveTableColumn(state, { columnName, newPosition }) {
  /* list of attributes we will be altering */
  let attributes = state.displayInfo.visibleAttributes;

  /* The current position of the attribute being moved */
  let currentPosition = attributes.findIndex(function(attribute) {
    return attribute.get('name') === columnName;
  });

  /* The attribute being moved */
  let attribute = attributes[currentPosition];

  attributes
    // remove attribute from array
    .splice(currentPosition, 1)
    // then, insert into new position
    .splice(newPosition, 0, attribute);

  return state;
}

function updateVisibleAttributes(state, { attributes }) {
  state.displayInfo.visibleAttributes = attributes;
  return state;
}

/**
 * Filter the results of an answer. The filtered results are stored in a
 * separate property.
 *
 * @param {string} terms The search phrase.
 * @param {string} questionName The questionName of the answer to filter.
 */
function filterAnswer(state, { terms, questionName }) {
  let parsedTerms = parseSearchTerms(terms);
  let records = state.answers[questionName].records;
  let filteredRecords = parsedTerms.reduce(function(records, term) {
    return records.filter(isTermInRecord(term));
  }, records);

  return assign(state, {
    filterTerm: terms,
    filteredRecords: filteredRecords
  });
}

function answerLoading(state, action) {
  state.isLoading = action.isLoading;
  return state;
}

export default {
  createStore
};
