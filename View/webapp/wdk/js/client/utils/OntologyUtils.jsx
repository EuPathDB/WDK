import {pruneDescendantNodes} from './TreeUtils';

/**
 * Get a sub-tree from an Ontology. The `leafPredicate` function
 * is used to find the leaves of the tree to return.
 *
 * @param {Ontology} ontology
 * @param {Function} leafPredicate
 */
export function getTree(ontology, leafPredicate) {
  return pruneDescendantNodes(node => nodeHasChildren(node) || leafPredicate(node), ontology.tree);
}

/**
 * Callback to provide the node children
 * @param node - given node
 * @returns {Array}  child nodes
 */
export let getNodeChildren = node =>
  node.children;

export let nodeHasChildren = node =>
  getNodeChildren(node).length > 0;

let includes = (array, value) =>
  array != null && array.indexOf(value) > -1;

export let nodeHasProperty = (name, value, node) =>
  includes(node.properties[name], value);

export let getPropertyValues = (name, node) =>
  (node.properties && node.properties[name]) || [];

export let getPropertyValue = (name, node) =>
  node.properties && node.properties[name] && node.properties[name][0];
