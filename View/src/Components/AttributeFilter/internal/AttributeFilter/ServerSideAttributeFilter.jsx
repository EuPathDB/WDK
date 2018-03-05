import React from 'react';
import PropTypes from 'prop-types';

import FieldList from 'Components/AttributeFilter/internal/AttributeFilter/FieldList';
import FilterList from 'Components/AttributeFilter/internal/AttributeFilter/FilterList';
import FieldFilter from 'Components/AttributeFilter/internal/AttributeFilter/FieldFilter';

/**
 * Filtering UI for server-side filtering.
 */
export default function ServerSideAttributeFilter (props) {
  var { hideFilterPanel, hideFieldPanel, } = props;

  return (
    <div>
      {hideFilterPanel || <FilterList {...props} /> }

      {/* Main selection UI */}
      <div className="filters ui-helper-clearfix">
        {hideFieldPanel || <FieldList {...props} /> }
        <FieldFilter {...props } />
      </div>
    </div>
  );
}

ServerSideAttributeFilter.propTypes = {

  // options
  displayName: PropTypes.string,
  autoFocus: PropTypes.bool,
  hideFilterPanel: PropTypes.bool,
  hideFieldPanel: PropTypes.bool,
  hideGlobalCounts: PropTypes.bool,
  selectByDefault: PropTypes.bool, // affects UI state for when no filter is applied

  // state
  fields: PropTypes.instanceOf(Map).isRequired,
  filters: PropTypes.array.isRequired,
  dataCount: PropTypes.number,
  filteredDataCount: PropTypes.number,
  loadingFilteredCount: PropTypes.bool,

  activeField: FieldFilter.propTypes.activeField,
  activeFieldState: FieldFilter.propTypes.activeFieldState,
  activeFieldSummary: FieldFilter.propTypes.activeFieldSummary,

  // not sure if these belong here
  isLoading: PropTypes.bool,

  // event handlers
  onActiveFieldChange: PropTypes.func.isRequired,
  onFiltersChange: PropTypes.func.isRequired,
  onMemberSort: PropTypes.func.isRequired,
  onMemberSearch: PropTypes.func.isRequired,
  onRangeScaleChange: PropTypes.func.isRequired

};

ServerSideAttributeFilter.defaultProps = {
  displayName: 'Items',
  hideFilterPanel: false,
  hideFieldPanel: false,
  hideGlobalCounts: false,
  selectByDefault: false
};