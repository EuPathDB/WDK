import { PropTypes } from 'react';
import { compose } from 'lodash';
import RecordMainCategorySection from './RecordMainCategorySection';
import { pure, wrappable } from '../utils/componentUtils';
import { getId, getLabel } from '../utils/CategoryUtils';

let RecordMainSection$;

const RecordMainSection = ({
  depth = 0,
  record,
  recordClass,
  categories,
  collapsedSections,
  parentEnumeration,
  onSectionToggle
}) => (categories == null ? null : (
  <div>
    {categories.map((category, index) => {
      let categoryName = getLabel(category);
      let categoryId = getId(category);
      let enumeration = String(parentEnumeration == null
        ? index + 1
        : parentEnumeration + '.' + (index + 1));

      return (
        <RecordMainCategorySection
          key={categoryName}
          category={category}
          depth={depth}
          enumeration={enumeration}
          isCollapsed={collapsedSections.includes(categoryId)}
          onSectionToggle={onSectionToggle}
          record={record}
          recordClass={recordClass}
        >
          <RecordMainSection$
            depth={depth + 1}
            record={record}
            recordClass={recordClass}
            categories={category.children}
            collapsedSections={collapsedSections}
            parentEnumeration={enumeration}
            onSectionToggle={onSectionToggle}
          />
        </RecordMainCategorySection>
      );
    })}
  </div>
))

RecordMainSection.propTypes = {
  record: PropTypes.object.isRequired,
  recordClass: PropTypes.object.isRequired,
  categories: PropTypes.array.isRequired,
  collapsedSections: PropTypes.array.isRequired,
  onSectionToggle: PropTypes.func.isRequired,
  depth: PropTypes.number,
  parentEnumeration: PropTypes.string
};

// Append `$` so we can refer to this component recursively. We want to reserve
// the normal name `RecordMainSection` for the inner function for debugging purposes.
RecordMainSection$ = compose(wrappable, pure)(RecordMainSection);

export default RecordMainSection$;
