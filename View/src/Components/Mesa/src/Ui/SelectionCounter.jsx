import React from 'react';
import PropTypes from 'prop-types';

class SelectionCounter extends React.Component {
  constructor (props) {
    super(props);
    this.noun = this.noun.bind(this);
    this.selectAllRows = this.selectAllRows.bind(this);
    this.deselectAllRows = this.deselectAllRows.bind(this);
  }

  noun (size) {
    const { selectedNoun, selectedPluralNoun } = this.props;
    size = (typeof size === 'number' ? size : size.length);
    return (!selectedNoun && !selectedPluralNoun)
      ? 'row' + (size === 1 ? '' : 's')
      : size === 1
        ? (selectedNoun || 'row')
        : (selectedPluralNoun || 'rows');
  }

  selectAllRows () {
    const { rows, selection, onRowSelect, onMultipleRowSelect } = this.props;
    const unselectedRows = rows.map(row => !selection.includes(row));
    if (typeof onMultipleRowSelect === 'function') onMultipleRowSelect(unselectedRows);
    else unselectedRows.forEach(row => onRowSelect(row));
  }

  deselectAllRows () {
    const { selection, onRowDeselect, onMultipleRowDeselect } = this.props;
    if (typeof onMultipleRowDeselect === 'function') onMultipleRowDeselect(selection)
    else selection.forEach(row => onRowDeselect(row));
  }

  render () {
    const { rows, selection } = this.props;
    if (!selection || !selection.length) return null;
    const allSelected = rows.every(row => selection.includes(row));

    return (
      <div className="SelectionCounter">
        <b>{selection.length} </b>
        {this.noun(selection)} selected.
        <br />
        {!onRowDeselect && !onMultipleRowSelect ? null : (<a onClick={this.deselectAllRows}>Clear selection.</a>)}
      </div>
    );
  }
};

SelectionCounter.propTypes = {
  // all/total "rows" in the table
  rows: PropTypes.array,
  // exclusively the selected rows (checked by ref/inclusion)
  selection: PropTypes.array.isRequired,
  // noun and plural to use for selections (e.g. "25 Datasets selected")
  selectedNoun: PropTypes.string,
  selectedPluralNoun: PropTypes.string,
  // single and multiple select/deselect handlers
  onRowSelect: PropTypes.func,
  onRowDeselect: PropTypes.func,
  onMultipleRowSelect: PropTypes.func,
  onMultipleRowDeselect: PropTypes.func
};

export default SelectionCounter;
