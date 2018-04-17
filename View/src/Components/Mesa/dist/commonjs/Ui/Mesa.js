'use strict';var _createClass=function(){function defineProperties(target,props){for(var descriptor,i=0;i<props.length;i++)descriptor=props[i],descriptor.enumerable=descriptor.enumerable||!1,descriptor.configurable=!0,'value'in descriptor&&(descriptor.writable=!0),Object.defineProperty(target,descriptor.key,descriptor)}return function(Constructor,protoProps,staticProps){return protoProps&&defineProperties(Constructor.prototype,protoProps),staticProps&&defineProperties(Constructor,staticProps),Constructor}}(),_react=require('react'),_react2=_interopRequireDefault(_react),_propTypes=require('prop-types'),_propTypes2=_interopRequireDefault(_propTypes),_DataTable=require('../Ui/DataTable'),_DataTable2=_interopRequireDefault(_DataTable),_TableToolbar=require('../Ui/TableToolbar'),_TableToolbar2=_interopRequireDefault(_TableToolbar),_ActionToolbar=require('../Ui/ActionToolbar'),_ActionToolbar2=_interopRequireDefault(_ActionToolbar),_PaginationMenu=require('../Ui/PaginationMenu'),_PaginationMenu2=_interopRequireDefault(_PaginationMenu),_EmptyState=require('../Ui/EmptyState'),_EmptyState2=_interopRequireDefault(_EmptyState);Object.defineProperty(exports,'__esModule',{value:!0});function _interopRequireDefault(obj){return obj&&obj.__esModule?obj:{default:obj}}function _classCallCheck(instance,Constructor){if(!(instance instanceof Constructor))throw new TypeError('Cannot call a class as a function')}function _possibleConstructorReturn(self,call){if(!self)throw new ReferenceError('this hasn\'t been initialised - super() hasn\'t been called');return call&&('object'==typeof call||'function'==typeof call)?call:self}function _inherits(subClass,superClass){if('function'!=typeof superClass&&null!==superClass)throw new TypeError('Super expression must either be null or a function, not '+typeof superClass);subClass.prototype=Object.create(superClass&&superClass.prototype,{constructor:{value:subClass,enumerable:!1,writable:!0,configurable:!0}}),superClass&&(Object.setPrototypeOf?Object.setPrototypeOf(subClass,superClass):subClass.__proto__=superClass)}var Mesa=function(_React$Component){function Mesa(props){_classCallCheck(this,Mesa);var _this=_possibleConstructorReturn(this,(Mesa.__proto__||Object.getPrototypeOf(Mesa)).call(this,props));return _this.renderToolbar=_this.renderToolbar.bind(_this),_this.renderActionBar=_this.renderActionBar.bind(_this),_this.renderPaginationMenu=_this.renderPaginationMenu.bind(_this),_this.renderEmptyState=_this.renderEmptyState.bind(_this),_this.renderBody=_this.renderBody.bind(_this),_this}return _inherits(Mesa,_React$Component),_createClass(Mesa,[{key:'renderPaginationMenu',value:function renderPaginationMenu(){var _props=this.props,uiState=_props.uiState,eventHandlers=_props.eventHandlers,_ref=uiState?uiState.pagination:{},currentPage=_ref.currentPage,totalPages=_ref.totalPages,rowsPerPage=_ref.rowsPerPage,_ref2=eventHandlers?eventHandlers:{},onPageChange=_ref2.onPageChange,onRowsPerPageChange=_ref2.onRowsPerPageChange;if(!onPageChange)return null;return _react2.default.createElement(_PaginationMenu2.default,{currentPage:currentPage,totalPages:totalPages,rowsPerPage:rowsPerPage,onPageChange:onPageChange,onRowsPerPageChange:onRowsPerPageChange})}},{key:'renderToolbar',value:function renderToolbar(){var _props2=this.props,rows=_props2.rows,options=_props2.options,columns=_props2.columns,uiState=_props2.uiState,eventHandlers=_props2.eventHandlers,children=_props2.children;return options.toolbar?_react2.default.createElement(_TableToolbar2.default,{rows:rows,options:options,columns:columns,uiState:uiState,eventHandlers:eventHandlers,children:children}):_react2.default.createElement('div',null,children)}},{key:'renderActionBar',value:function renderActionBar(){var _props3=this.props,rows=_props3.rows,options=_props3.options,actions=_props3.actions,eventHandlers=_props3.eventHandlers;return actions.length?_react2.default.createElement(_ActionToolbar2.default,{rows:rows,options:options,actions:actions,eventHandlers:eventHandlers}):null}},{key:'renderEmptyState',value:function renderEmptyState(){var _props4=this.props,rows=_props4.rows,options=_props4.options,columns=_props4.columns,actions=_props4.actions,uiState=_props4.uiState,eventHandlers=_props4.eventHandlers,emptinessCulprit=uiState.emptinessCulprit,sort=uiState.sort,hasSelectionColumn='function'==typeof options.isRowSelected&&'function'==typeof eventHandlers.onRowSelect&&'function'==typeof eventHandlers.onRowDeselect,colspan=columns.filter(function(column){return!column.hidden}).length+(hasSelectionColumn?1:0);return _react2.default.createElement(_EmptyState2.default,{colspan:colspan,culprit:emptinessCulprit})}},{key:'renderBody',value:function renderBody(){var _props5=this.props,rows=_props5.rows,options=_props5.options,columns=_props5.columns,actions=_props5.actions,uiState=_props5.uiState,eventHandlers=_props5.eventHandlers,Empty=this.renderEmptyState;return rows.length?_react2.default.createElement(_DataTable2.default,{rows:rows,options:options,columns:columns,actions:actions,uiState:uiState,eventHandlers:eventHandlers}):_react2.default.createElement(Empty,null)}},{key:'render',value:function render(){var _props6=this.props,rows=_props6.rows,options=_props6.options,columns=_props6.columns,actions=_props6.actions,uiState=_props6.uiState,eventHandlers=_props6.eventHandlers,Body=this.renderBody,Toolbar=this.renderToolbar,ActionBar=this.renderActionBar,PageNav=this.renderPaginationMenu;return _react2.default.createElement('div',{className:'Mesa'},_react2.default.createElement(Toolbar,null),_react2.default.createElement(ActionBar,null),_react2.default.createElement(PageNav,null),_react2.default.createElement(Body,null),_react2.default.createElement(PageNav,null))}}]),Mesa}(_react2.default.Component);_DataTable2.default.propTypes={rows:_propTypes2.default.array,columns:_propTypes2.default.array,options:_propTypes2.default.object,actions:_propTypes2.default.arrayOf(_propTypes2.default.shape({element:_propTypes2.default.oneOfType([_propTypes2.default.func,_propTypes2.default.node,_propTypes2.default.element]),handler:_propTypes2.default.func,callback:_propTypes2.default.func})),uiState:_propTypes2.default.object,eventHandlers:_propTypes2.default.objectOf(_propTypes2.default.func)},exports.default=Mesa;