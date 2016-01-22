import ReactDOM from 'react-dom';
import React from 'react';

export default class IndeterminateCheckbox extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      "id": props.id,
      "indeterminate": props.indeterminate,
      "checked": props.checked
    };
    // hard bind the handleChange functions to the IndeterminateCheckbox object
    this.handleChange = this.handleChange.bind(this);
  }

  componentDidMount() {
    if(this.props.indeterminate === true) {
      this._setIndeterminate(true);
    }
  }

  componentDidUpdate(previousProps) {
    this._setIndeterminate(this.props.indeterminate);
  }

  _setIndeterminate(indeterminate) {
    const node = ReactDOM.findDOMNode(this);
    node.indeterminate = indeterminate;
  }

  handleChange(e) {
    let selected = e.target.checked;
    this.props.toggleCheckbox(this.state.id, selected);
  }

  render() {
    let handleChange = this.handleChange;
    let id = this.props.id;
    return (
        <input {...this.props} type="checkbox" onChange={handleChange.bind(this)} />
    )
  }
}