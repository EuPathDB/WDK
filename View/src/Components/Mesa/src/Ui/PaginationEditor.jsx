import React from 'react';

import SelectBox from '../Components/SelectBox';
import { setPaginatedItemsPerPage } from '../State/Actions';

class PaginationEditor extends React.PureComponent {
  constructor (props) {
    super(props);
    this.handleItemsPerPageChange = this.handleItemsPerPageChange.bind(this);
  }

  handleItemsPerPageChange (itemsPerPage) {
    const { dispatch } = this.props;
    itemsPerPage = parseInt(itemsPerPage);
    dispatch(setPaginatedItemsPerPage(itemsPerPage));
  }

  render () {
    const { paginationState } = this.props;
    let options = [ 5, 10, 20, 35, 50, 100 ];

    return (
      <div className="PaginationEditor">
        <span>Rows per page: </span>
        <SelectBox
          options={options}
          selected={paginationState.itemsPerPage}
          onChange={this.handleItemsPerPageChange}
        />
      </div>
    );
  }
};

export default PaginationEditor;
