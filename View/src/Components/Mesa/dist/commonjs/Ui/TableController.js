'use strict';var _createClass=function(){function defineProperties(target,props){for(var descriptor,i=0;i<props.length;i++)descriptor=props[i],descriptor.enumerable=descriptor.enumerable||!1,descriptor.configurable=!0,'value'in descriptor&&(descriptor.writable=!0),Object.defineProperty(target,descriptor.key,descriptor)}return function(Constructor,protoProps,staticProps){return protoProps&&defineProperties(Constructor.prototype,protoProps),staticProps&&defineProperties(Constructor,staticProps),Constructor}}(),_react=require('react'),_react2=_interopRequireDefault(_react),_TableBody=require('../Ui/TableBody'),_TableBody2=_interopRequireDefault(_TableBody),_RowUtils=require('../Utils/RowUtils'),_RowUtils2=_interopRequireDefault(_RowUtils),_TableToolbar=require('../Ui/TableToolbar'),_TableToolbar2=_interopRequireDefault(_TableToolbar),_ActionToolbar=require('../Ui/ActionToolbar'),_ActionToolbar2=_interopRequireDefault(_ActionToolbar),_PaginationMenu=require('../Ui/PaginationMenu'),_PaginationMenu2=_interopRequireDefault(_PaginationMenu),_Actions=require('../State/Actions');Object.defineProperty(exports,'__esModule',{value:!0});function _interopRequireDefault(obj){return obj&&obj.__esModule?obj:{default:obj}}function _classCallCheck(instance,Constructor){if(!(instance instanceof Constructor))throw new TypeError('Cannot call a class as a function')}function _possibleConstructorReturn(self,call){if(!self)throw new ReferenceError('this hasn\'t been initialised - super() hasn\'t been called');return call&&('object'==typeof call||'function'==typeof call)?call:self}function _inherits(subClass,superClass){if('function'!=typeof superClass&&null!==superClass)throw new TypeError('Super expression must either be null or a function, not '+typeof superClass);subClass.prototype=Object.create(superClass&&superClass.prototype,{constructor:{value:subClass,enumerable:!1,writable:!0,configurable:!0}}),superClass&&(Object.setPrototypeOf?Object.setPrototypeOf(subClass,superClass):subClass.__proto__=superClass)}var TableController=function(_React$Component){function TableController(props){_classCallCheck(this,TableController);var _this=_possibleConstructorReturn(this,(TableController.__proto__||Object.getPrototypeOf(TableController)).call(this,props));return _this.getFilteredRows=_this.getFilteredRows.bind(_this),_this}return _inherits(TableController,_React$Component),_createClass(TableController,[{key:'getFilteredRows',value:function getFilteredRows(){var _props=this.props,state=_props.state,dispatch=_props.dispatch,rows=state.rows,uiState=state.uiState,columns=state.columns,searchQuery=uiState.searchQuery,sort=uiState.sort,emptinessCulprit=uiState.emptinessCulprit;return rows.length?(searchQuery&&searchQuery.length&&(rows=_RowUtils2.default.searchRowsForQuery(rows,columns,searchQuery)),!rows.length)?('search'!==emptinessCulprit&&dispatch((0,_Actions.setEmptinessCulprit)('search')),[]):(rows=_RowUtils2.default.filterRowsByColumns(rows,columns),!rows.length)?('filters'!==emptinessCulprit&&dispatch((0,_Actions.setEmptinessCulprit)('filters')),[]):(sort.byColumn&&(rows=_RowUtils2.default.sortRowsByColumn(rows,sort.byColumn,sort.ascending)),rows):(emptinessCulprit&&'nodata'!==emptinessCulprit&&dispatch((0,_Actions.setEmptinessCulprit)('nodata')),[])}},{key:'render',value:function render(){var _props2=this.props,state=_props2.state,dispatch=_props2.dispatch,children=_props2.children,uiState=state.uiState,options=state.options,actions=state.actions,paginationState=uiState.paginationState,filteredRows=this.getFilteredRows(),PageNav=function(){return options.paginate?_react2.default.createElement(_PaginationMenu2.default,{dispatch:dispatch,list:filteredRows,paginationState:paginationState}):null};return _react2.default.createElement('div',{className:'TableController'},options.toolbar?_react2.default.createElement(_TableToolbar2.default,{state:state,dispatch:dispatch,filteredRows:filteredRows},children):_react2.default.createElement('div',null,children),actions.length?_react2.default.createElement(_ActionToolbar2.default,{state:state,dispatch:dispatch,filteredRows:filteredRows}):null,_react2.default.createElement(PageNav,null),_react2.default.createElement(_TableBody2.default,{state:state,dispatch:dispatch,filteredRows:filteredRows}),_react2.default.createElement(PageNav,null))}}]),TableController}(_react2.default.Component);exports.default=TableController;