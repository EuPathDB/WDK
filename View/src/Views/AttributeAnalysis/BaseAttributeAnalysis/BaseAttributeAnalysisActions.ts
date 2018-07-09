import { Observable } from 'rxjs';

import { Action, EpicServices, makeActionCreator } from 'Utils/ActionCreatorUtils';
import { Reporter } from 'Utils/WdkModel';
import { ServiceError } from 'Utils/WdkService';

// Actions
// -------

// Scoped analysis action
export const ScopedAnalysisAction =
  makeActionCreator<{ action: Action, reporter: Reporter, stepId: number }, 'attribute-reporter/scoped-action'>('attribute-reporter/scoped-action');

// Report requested
export const AttributeReportRequested =
  makeActionCreator<{ stepId: number; reporterName: string; }, 'attribute-reporter/requested'>('attribute-reporter/requested')

// Report success reposonse
export const AttributeReportReceived =
  makeActionCreator<{ report: any }, 'attribute-reporter/received'>('attribute-reporter/received')


// Report failed response
export const AttributeReportFailed =
  makeActionCreator<{ error: ServiceError }, 'attribute-reporter/failed'>('attribute-reporter/failed')


// Report cancelled
export const AttributeReportCancelled =
  makeActionCreator('attribute-reporter/cancelled')

export const TableSorted =
  makeActionCreator<{ key: string, direction: 'asc' | 'desc' }, 'attribute-reporter/table-sorted'>('attribute-reporter/table-sorted');

export const TableSearched =
  makeActionCreator<string, 'attribute-reporter/table-searched'>('attribute-reporter/table-searched');

export const TabSelected =
  makeActionCreator<string, 'attribute-reporter/tab-selected'>('attribute-reporter/tab-selected');

export function observeReportRequests(action$: Observable<Action>, { wdkService }: EpicServices): Observable<Action> {
  return action$.filter(AttributeReportRequested.test)
    .mergeMap(({ payload: { reporterName, stepId }}) =>
      Observable.from(
        wdkService.getStepAnswer(stepId, { format: reporterName }).then(
          report => AttributeReportReceived.create({ report }),
          error => AttributeReportFailed.create({ error })
        ))
        .takeUntil(action$.filter(AttributeReportCancelled.test)))
}
