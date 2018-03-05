import {
  getTree,
  nodeHasProperty,
  OntologyNode
} from 'Utils/OntologyUtils';

import { ActionThunk } from 'Utils/ActionCreatorUtils';

export type LoadingAction = {
  type: 'sitemap/loading'
}
export type InitializeAction = {
  type: 'sitemap/initialize',
  payload: {
    tree: OntologyNode
  }
}
export type ExpansionAction = {
  type: 'sitemap/updateExpanded',
  payload: {
    expandedList: string[]
  }
}
export type SearchAction = {
  type: 'sitemap/setSearchText',
  payload: {
    searchText: string
  }
}
export type ErrorAction = {
  type: 'sitemap/error',
  payload: {
    error: Error
  }
}

export function loadCurrentSiteMap(): ActionThunk<LoadingAction | ErrorAction | InitializeAction> {
  return function run(dispatch, { wdkService }) {
    dispatch({ type: 'sitemap/loading' });

    let ontologyPromise = wdkService.getOntology('SiteMap');

    let isQualifying = (node: OntologyNode) => {
      return (
        nodeHasProperty('scope', 'record', node) ||
        nodeHasProperty('scope', 'menu', node) ||
        nodeHasProperty('scope', 'webservice', node) ||
        nodeHasProperty('scope', 'gbrowse', node) ||
        nodeHasProperty('targetType', 'track', node)
      );
    }

    return ontologyPromise.then((ontology) => {
      dispatch({
        type: 'sitemap/initialize',
        payload: { tree: getTree(ontology, isQualifying) }
      });
    }).catch(error => {
      console.error(error);
      dispatch({
        type: 'sitemap/error',
        payload: { error }
      });
    });
  }
}

export function updateExpanded (expandedList: string[]): ExpansionAction {
  return {
    type: 'sitemap/updateExpanded',
    payload: { expandedList: expandedList}
  };
}

export function setSearchText (searchText: string): SearchAction {
  return {
    type: 'sitemap/setSearchText',
    payload: { searchText: searchText}
  };
}