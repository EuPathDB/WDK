import $ from 'jquery';
import {Component} from 'react';
import PropTypes from 'prop-types';
import {Router, useRouterHistory} from 'react-router';
import {createHistory} from 'history';
import wdkRoutes from '../routes';

let REACT_ROUTER_LINK_CLASSNAME = 'wdk-ReactRouterLink';
let GLOBAL_CLICK_HANDLER_SELECTOR = `a:not(.${REACT_ROUTER_LINK_CLASSNAME})`;
let RELATIVE_LINK_REGEXP = new RegExp('^((' + location.protocol + ')?//)?' + location.host);

/** WDK Application Root */
export default class Root extends Component {

  constructor(props, context) {
    super(props, context);
    this.history = useRouterHistory(createHistory)({ basename: this.props.rootUrl });
    // Used to inject wdk content as props of Route Component
    this.createElement = (RouteComponent, routerProps) => {
      let { makeDispatchAction, stores } = this.props;
      return (
        <RouteComponent {...routerProps} makeDispatchAction={makeDispatchAction} stores={stores}/>
      );
    };
    this.routes = this.props.wrapRoutes(wdkRoutes);
    this.handleGlobalClick = this.handleGlobalClick.bind(this);
    this.removeHistoryListener = this.history.listen(location => this.props.onLocationChange(location));
    this.props.onLocationChange(this.history.getCurrentLocation());
  }

  handleGlobalClick(event) {
    if (!event.currentTarget.href) return;

    let hasModifiers = event.metaKey || event.altKey || event.shiftKey || event.ctrlKey || event.which !== 1;
    let href = event.currentTarget.getAttribute('href').replace(RELATIVE_LINK_REGEXP, '');
    if (!hasModifiers && href.startsWith(this.props.rootUrl)) {
      this.history.push(href.slice(this.props.rootUrl.length));
      event.preventDefault();
    }
  }

  componentDidMount() {
    /** install global click handler */
    $(document).on('click', GLOBAL_CLICK_HANDLER_SELECTOR, this.handleGlobalClick);
  }

  componentWillUnmount() {
    $(document).off('click', GLOBAL_CLICK_HANDLER_SELECTOR, this.handleGlobalClick);
    this.removeHistoryListener();
  }

  render() {
    return (
      <Router history={this.history} createElement={this.createElement} routes={this.routes}/>
    );
  }
}

Root.propTypes = {
  rootUrl: PropTypes.string,
  makeDispatchAction: PropTypes.func.isRequired,
  stores: PropTypes.object.isRequired,
  wrapRoutes: PropTypes.func,
  onLocationChange: PropTypes.func
};

Root.defaultProps = {
  rootUrl: '/',
  wrapRoutes: routes => routes, // identity
  onLocationChange: () => {}    // noop
};

