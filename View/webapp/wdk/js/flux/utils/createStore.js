import EventEmitter from 'events';

/**
 * Store meta-constructor. This createStore will create a new class based on the
 * `spec` object provided. Any instance of the new class will register the
 * method `spec.handleDispatch` with the application dispatcher. Likewise,
 * `spec.getState` will be used to return the state of the store to subscribers
 * of the store.
 *
 * Any method of the `spec` object will be called with the `spec` object as its
 * receiver. This means `this` will always can be used to refer to the `spec`
 * object. This makes it possible to refer to `this.state`, etc.
 *
 *
 * Example usage:
 *
 *     var MyStore = createStore({
 *       state: {
 *         items: []
 *       },
 *
 *       dispatchHandler(action, emitChange) {
 *         switch (action) {
 *           case ActionTypes.MY_ACTION:
 *             // do things to this.state.items
 *             emitChange();
 *             break;
 *         }
 *       },
 *
 *       getState() {
 *         return state;
 *       }
 *
 *     });
 *
 * @function
 * @param {object} spec A specialization object used to customize a Store. It
 * must contain two functions: `dispatchHandler(action, emitChange, waitFor)`
 * and `getState()`.
 *
 * In `dispatchHandler`, `action` is the dispatched action,
 * `emitChange` is a function to that will call callbacks registered with
 * {{#crossLink "Store/subscribe"}}{{/crossLink}}, and `waitFor` delegates to
 * the dispatcher's `waitFor` method (see http://facebook.github.io/flux/docs/dispatcher.html#api).
 *
 * `getState` should be a function that returns the Store's state.
 */

export default function createStore(spec) {
  ensureFunction(spec.dispatchHandler, "dispatchHandler");
  ensureFunction(spec.getState, "getState");

  // A flux store constructor that will get the application dispatcher
  // injected by the injection system. It will also call the `spec` methods
  // described above.
  return function createWdkStore(dispatcher) {
    var specCopy = Object.create(spec);
    var emitter = new EventEmitter();

    var emitChange = function emitChange() {
      emitter.emit('change', specCopy.getState());
    };

    var waitFor = function waitFor(tokens) {
      dispatcher.waitFor(tokens);
    };

    /**
     * Token generated by the dispatcher. This can be used with the `waitFor`
     * function provided to the `dispatchHandler` function.
     * @property dispatchToken
     * @type string
     */
    var dispatchToken = dispatcher.register(action => {
      specCopy.dispatchHandler(action, emitChange, waitFor);
    });


    return {

      // Define dispatchToken as a getter so that it cannot
      // be augmented externally.
      get dispatchToken() {
        return dispatchToken;
      },

      // Register a callback function to be invoked when the Store's internal
      // state has been changed. `callback` will be called with the result of
      // `specCopy.getState()`
      subscribe(callback) {
        emitter.on('change', callback);
      },

      // Remove a callback registered with `subscribe(callback)`
      unsubscribe(callback) {
        emitter.removeListener('change', callback);
      },

      getState() {
        return specCopy.getState();
      }

    };
  };
}

function ensureFunction(fn, name) {
  var message = "Store " + name + " " + fn + " is not a function";
  if (typeof fn !== 'function') throw new TypeError(message);
}
