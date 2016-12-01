import { Action } from '../dispatcher/Dispatcher';
import WdkStore, { BaseState } from './WdkStore';
import { actionTypes } from '../actioncreators/UserDatasetsActionCreators';
import { UserDataset } from '../utils/WdkModel';

/**
 * If isLoading is false, and resource is undefined,
 * then assume the user dataset does not exist
 */
type UserDatasetEntry = {
  isLoading: boolean;
  resource: UserDataset | void;
};

interface State extends BaseState {
  userDatasetsById: { [key: number]: UserDatasetEntry };
  userDatasetUpdating: boolean;
  userDatasetUpdateError?: Error;
  loadError?: Error;
}

/**
 * Stores a map of userDatasets by id. By not storing the current userDataset,
 * we avoid race conditions where the DATASET_ITEM_RECEIVED actions are
 * dispatched in a different order than the corresponding action creators are
 * invoked.
 */
export default class UserDatasetItemStore extends WdkStore<State> {

  getInitialState() {
    return Object.assign({
      userDatasetsById: {},
      userDatasetUpdating: false
    }, super.getInitialState());
  }

  handleAction(state: State, {type, payload}: Action) {
    switch (type) {
      case actionTypes.DATASET_ITEM_LOADING: return Object.assign({}, state, {
        userDatasetsById: Object.assign({}, state.userDatasetsById, {
          [payload.id]: {
            isLoading: true
          }
        })
      });

      case actionTypes.DATASET_ITEM_RECEIVED: return Object.assign({}, state, {
        userDatasetLoading: false,
        userDatasetsById: Object.assign({}, state.userDatasetsById, {
          [payload.id]: {
            isLoading: false,
            resource: payload.userDataset
          }
        })
      });

      case actionTypes.DATASET_ITEM_ERROR: return Object.assign({}, state, {
        userDatasetLoading: false,
        loadError: payload.error
      });

      case actionTypes.DATASET_ITEM_UPDATING: return Object.assign({}, state, {
        userDatasetUpdating: true
      });

      case actionTypes.DATASET_ITEM_UPDATE_SUCCESS: return Object.assign({}, state, {
        userDatasetUpdating: false,
        userDatasetsById: Object.assign({}, state.userDatasetsById, {
          [payload.id]: {
            isLoading: false,
            resource: Object.assign({}, state.userDatasetsById[payload.id].resource, {
              meta: payload.meta
            })
          }
        })
      });

      default:
        return state;
    }
  }
}
