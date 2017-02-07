/**
 * Action creators related to routing.
 *
 * Note: These actions do not currently alter the URL. They mainly
 * propagate router state to stores.
 */

import { Location } from 'history';
import { ActionCreator } from '../ActionCreator';

export type LocationAction = {
  type: 'router/location-updated',
  payload: { location: Location }
}

export const updateLocation: ActionCreator<LocationAction> = (location: Location) => ({
  type: 'router/location-updated',
  payload: { location }
});
