'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _Events = require('../Utils/Events');

var _Events2 = _interopRequireDefault(_Events);

var _Icon = require('../Components/Icon');

var _Icon2 = _interopRequireDefault(_Icon);

var _Modal = require('../Components/Modal');

var _Modal2 = _interopRequireDefault(_Modal);

var _Checkbox = require('../Components/Checkbox');

var _Checkbox2 = _interopRequireDefault(_Checkbox);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

var ColumnEditor = function (_React$PureComponent) {
  _inherits(ColumnEditor, _React$PureComponent);

  function ColumnEditor(props) {
    _classCallCheck(this, ColumnEditor);

    var _this = _possibleConstructorReturn(this, (ColumnEditor.__proto__ || Object.getPrototypeOf(ColumnEditor)).call(this, props));

    _this.state = {
      editorOpen: false
    };

    _this.openEditor = _this.openEditor.bind(_this);
    _this.closeEditor = _this.closeEditor.bind(_this);
    _this.toggleEditor = _this.toggleEditor.bind(_this);

    _this.renderModal = _this.renderModal.bind(_this);
    _this.renderTrigger = _this.renderTrigger.bind(_this);
    _this.renderColumnListItem = _this.renderColumnListItem.bind(_this);

    _this.showColumn = _this.showColumn.bind(_this);
    _this.hideColumn = _this.hideColumn.bind(_this);
    _this.showAllColumns = _this.showAllColumns.bind(_this);
    _this.hideAllColumns = _this.hideAllColumns.bind(_this);
    return _this;
  }

  _createClass(ColumnEditor, [{
    key: 'openEditor',
    value: function openEditor() {
      this.setState({ editorOpen: true });
      if (!this.closeListener) this.closeListener = _Events2.default.onKey('esc', this.closeEditor);
    }
  }, {
    key: 'closeEditor',
    value: function closeEditor() {
      this.setState({ editorOpen: false });
      if (this.closeListener) _Events2.default.remove(this.closeListener);
    }
  }, {
    key: 'toggleEditor',
    value: function toggleEditor() {
      var editorOpen = this.state.editorOpen;

      return editorOpen ? this.closeEditor() : this.openEditor();
    }
  }, {
    key: 'showColumn',
    value: function showColumn(column) {
      var eventHandlers = this.props.eventHandlers;
      var onShowColumn = eventHandlers.onShowColumn;

      if (onShowColumn) onShowColumn(column);
    }
  }, {
    key: 'hideColumn',
    value: function hideColumn(column) {
      var eventHandlers = this.props.eventHandlers;
      var onHideColumn = eventHandlers.onHideColumn;

      if (onHideColumn) onHideColumn(column);
    }
  }, {
    key: 'showAllColumns',
    value: function showAllColumns() {
      var _this2 = this;

      var _props = this.props,
          columns = _props.columns,
          eventHandlers = _props.eventHandlers;

      var hiddenColumns = columns.filter(function (col) {
        return col.hidden;
      });
      hiddenColumns.forEach(function (column) {
        return _this2.showColumn(column);
      });
    }
  }, {
    key: 'hideAllColumns',
    value: function hideAllColumns() {
      var _this3 = this;

      var _props2 = this.props,
          columns = _props2.columns,
          eventHandlers = _props2.eventHandlers;

      var shownColumns = columns.filter(function (col) {
        return !col.hidden;
      });
      shownColumns.forEach(function (column) {
        return _this3.hideColumn(column);
      });
    }
  }, {
    key: 'renderTrigger',
    value: function renderTrigger() {
      var children = this.props.children;

      return _react2.default.createElement(
        'div',
        { className: 'ColumnEditor-Trigger', onClick: this.toggleEditor },
        children
      );
    }
  }, {
    key: 'renderColumnListItem',
    value: function renderColumnListItem(column) {
      var _this4 = this;

      return _react2.default.createElement(
        'li',
        { className: 'ColumnEditor-List-Item', key: column.key },
        _react2.default.createElement(_Checkbox2.default, {
          checked: !column.hidden,
          disabled: !column.hideable,
          onChange: function onChange() {
            return (column.hidden ? _this4.showColumn : _this4.hideColumn)(column);
          }
        }),
        ' ' + (column.name || column.key)
      );
    }
  }, {
    key: 'renderModal',
    value: function renderModal() {
      var editorOpen = this.state.editorOpen;

      return _react2.default.createElement(
        _Modal2.default,
        { open: editorOpen, onClose: this.closeEditor },
        _react2.default.createElement(
          'h3',
          null,
          'Add / Remove Columns'
        ),
        _react2.default.createElement(
          'small',
          null,
          _react2.default.createElement(
            'a',
            { onClick: this.showAllColumns },
            'Select All'
          ),
          _react2.default.createElement(
            'span',
            null,
            ' | '
          ),
          _react2.default.createElement(
            'a',
            { onClick: this.hideAllColumns },
            'Clear All'
          )
        ),
        _react2.default.createElement(
          'ul',
          { className: 'ColumnEditor-List' },
          columns.map(this.renderColumnListItem)
        ),
        _react2.default.createElement(
          'button',
          { onClick: this.closeEditor, style: { margin: '0 auto', display: 'block' } },
          'Close'
        )
      );
    }
  }, {
    key: 'render',
    value: function render() {
      var modal = this.renderModal();
      var trigger = this.renderTrigger();

      return _react2.default.createElement(
        'div',
        { className: 'ColumnEditor' },
        trigger,
        modal
      );
    }
  }]);

  return ColumnEditor;
}(_react2.default.PureComponent);

exports.default = ColumnEditor;