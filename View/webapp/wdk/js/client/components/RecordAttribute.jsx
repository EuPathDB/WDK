import { PropTypes } from 'react';
import { renderAttributeValue, wrappable } from '../utils/componentUtils';

/** Attribute value */
function RecordAttribute(props) {
  let { record, attribute } = props;
  return renderAttributeValue(record.attributes[attribute.name], null, 'div');
}

RecordAttribute.propTypes = {
  attribute: PropTypes.object.isRequired,
  record: PropTypes.object.isRequired,
  recordClass: PropTypes.object.isRequired
};

export default wrappable(RecordAttribute);
