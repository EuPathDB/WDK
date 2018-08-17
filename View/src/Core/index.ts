import 'Core/vendor';
import * as Components from 'Components';
import * as AttributeFilterUtils from 'Components/AttributeFilter/Utils';
import * as ActionCreators from 'Core/ActionCreators';
import * as Controllers from 'Core/Controllers';
import { initialize, wrapComponents } from 'Core/main';
import * as Plugins from 'Core/Plugins';
import * as Stores from 'Core/State/Stores';
import * as ActionCreatorUtils from 'Utils/ActionCreatorUtils';
import * as CategoryUtils from 'Utils/CategoryUtils';
import * as ComponentUtils from 'Utils/ComponentUtils';
import * as FormSubmitter from 'Utils/FormSubmitter';
import * as IterableUtils from 'Utils/IterableUtils';
import * as Json from 'Utils/Json';
import * as OntologyUtils from 'Utils/OntologyUtils';
import * as Platform from 'Utils/Platform';
import * as PromiseUtils from 'Utils/PromiseUtils';
import * as StaticDataUtils from 'Utils/StaticDataUtils';
import * as TreeUtils from 'Utils/TreeUtils';
import * as WdkModel from 'Utils/WdkModel';
import WdkService from 'Utils/WdkService';
import * as ReporterUtils from 'Views/ReporterForm/reporterUtils';

declare global {
  interface Window {
    __asset_path_remove_me_please__: string;
  }
}

__webpack_public_path__ = window.__asset_path_remove_me_please__; // eslint-disable-line

export {
  ActionCreatorUtils,
  ActionCreators,
  AttributeFilterUtils,
  CategoryUtils,
  ComponentUtils,
  Components,
  Controllers,
  FormSubmitter,
  IterableUtils,
  Json,
  OntologyUtils,
  Platform,
  Plugins,
  PromiseUtils,
  ReporterUtils,
  StaticDataUtils,
  Stores,
  TreeUtils,
  WdkModel,
  WdkService,
  initialize,
  wrapComponents,
};
