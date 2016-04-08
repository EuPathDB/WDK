// Make libraries available globally

import * as lodash from 'lodash';
window._ = lodash;

import * as Backbone from 'backbone';
window.Backbone = Backbone;

import * as React from 'react';
window.React = React;

import * as ReactDOM from 'react-dom';
window.ReactDOM = ReactDOM;

import * as ReactRouter from 'react-router';
window.ReactRouter = ReactRouter;

if (process.env.NODE_ENV !== 'production') {
  window.ReactPerf = require('react-addons-perf');
}
