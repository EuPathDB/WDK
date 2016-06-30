import React from 'react';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import classnames from 'classnames';
import RecordAttribute from './RecordAttribute';
import RecordTable from './RecordTable';
import CollapsibleSection from './CollapsibleSection';
import { wrappable } from '../utils/componentUtils';
import {
  getId,
  getTargetType,
  getRefName,
  getDisplayName
} from '../utils/CategoryUtils';

let RecordMainCategorySection = React.createClass({

  propTypes: {
    isCollapsed: React.PropTypes.bool.isRequired,
    category: React.PropTypes.object.isRequired,
    depth: React.PropTypes.number,
    record: React.PropTypes.object.isRequired,
    recordClass: React.PropTypes.object.isRequired
  },

  mixins: [ PureRenderMixin ],

  toggleCollapse() {
    let { depth, category, isCollapsed } = this.props;
    // only toggle non-top-level category and wdkReference nodes
    if ('wdkReference' in category || depth > 1) {
      this.props.onSectionToggle(
        getId(category),
        // It's tempting to negate this value, but we are sending the value
        // we want for isVisible here.
        isCollapsed
      );
    }
  },

  render() {
    let {
      category,
      depth,
      record,
      recordClass,
      attributes,
      tables,
      isCollapsed,
      enumeration
    } = this.props;
    let targetType = getTargetType(category);

    if (targetType === 'attribute') {
      // render attribute
      let attribute = category.wdkReference;
      let { name, displayName } = attribute;
      let value = record.attributes[name]
      if (value == null) return null;
      return (
        <RecordAttribute
          {...attribute}
          isCollapsed={isCollapsed}
          onCollapsedChange={this.toggleCollapse}
          value={value}
          record={record}
          recordClass={recordClass}
          id={name}
          className={`wdk-RecordAttributeSectionItem wdk-RecordAttributeSectionItem__${name}`}
        />
      );
    }

    if (targetType === 'table') {
      // render table
      let table = category.wdkReference;
      let { name, displayName } = table;
      let value = record.tables[name];
      let wrapperClassBase = 'wdk-RecordTableWrapper';
      let wrapperClass = classnames(
        wrapperClassBase,
        `${wrapperClassBase}__${name}`
      );

      let headerClassBase = 'wdk-RecordTableHeader';
      let headerClass = classnames({
        [headerClassBase]: true,
        [`${headerClassBase}__collapsed`]: isCollapsed
      });

      return (
        <CollapsibleSection
          id={name}
          className="wdk-RecordTableContainer"
          headerContent={displayName}
          isCollapsed={isCollapsed}
          onCollapsedChange={this.toggleCollapse}
        >
          {value == null
            ? <p>Loading...</p>
            : <RecordTable value={value} table={table} record={record} recordClass={recordClass}/>}
        </CollapsibleSection>
      );
    }

    let id = getId(category);
    let categoryName = getDisplayName(category);
    let Header = 'h' + Math.min(depth + 1, 6);
    let headerContent = (
      <div>
        <span className="wdk-RecordSectionEnumeration">{enumeration}</span> {categoryName}
        <a className="wdk-RecordSectionLink" onClick={e => e.stopPropagation()} href={'#' + id}>&sect;</a>
      </div>
    );
    return (
      <CollapsibleSection
        id={id}
        className={depth === 1 ? 'wdk-RecordSection' : 'wdk-RecordSubsection'}
        headerComponent={Header}
        headerContent={headerContent}
        isCollapsed={isCollapsed}
        onCollapsedChange={this.toggleCollapse}
      >
        {this.props.children}
      </CollapsibleSection>
    );
  }

});

export default wrappable(RecordMainCategorySection);
