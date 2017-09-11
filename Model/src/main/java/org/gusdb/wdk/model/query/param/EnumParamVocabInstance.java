package org.gusdb.wdk.model.query.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.user.User;

/**
 * This class encapsulates a vocabulary and default value for an AbstractEnumParam based on a set of context
 * values. It is generated by subclasses of AbstractEnumParam but is used by EnumParamBean to hold dependent
 * param info for the life of a request (since it can be expensive to generate).
 * 
 * History: When it was created, we were maintaining context state in AbstractEnumParam itself, which violated
 * the idea that Model components were stateless. Since a lot of information must be retrieved and stored in
 * EnumParamBean, this class was created to hold it all for a single context. EnumParamBean will create a new
 * cache if its context values change.
 * 
 * @author rdoherty
 */
public class EnumParamVocabInstance implements DependentParamInstance {

  private static Logger logger = Logger.getLogger(EnumParamVocabInstance.class);

  // param this cache was created by
  // context values used to create this cache
  private Map<String, String> _dependedParamValues;
  // default value based on vocabulary and select mode (or maybe "hard-coded" (in XML) default)
  private String _defaultValue;

  // vocabulary maps for display of this vocabulary in JSP; could be select element or tree
  private Map<String, String> _termInternalMap = new LinkedHashMap<String, String>();
  private Map<String, String> _termDisplayMap = new LinkedHashMap<String, String>();
  private Map<String, String> _termParentMap = new LinkedHashMap<String, String>();
  private List<EnumParamTermNode> _termTreeList = new ArrayList<EnumParamTermNode>();

  private AbstractEnumParam _aeParam;

  public EnumParamVocabInstance(Map<String, String> dependedParamValues, AbstractEnumParam aeParam) {
    _dependedParamValues = dependedParamValues;
    _aeParam = aeParam;
  }

  public String getDefaultValue() {
    return _defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    _defaultValue = defaultValue;
  }

  /**
   * Determines and returns the sanity default for this param in the following way: if sanitySelectMode is not
   * null, use it to choose params; if it is, use default (i.e. however param normally gets default)
   * 
   * @param sanitySelectMode
   *          select mode form model (ParamValuesSet)
   * @return default value for this param, based on cached vocab values
   */
  public String getSanityDefaultValue(SelectMode sanitySelectMode, boolean isMultiPick,
      String sanityDefaultNoSelectMode) {
    logger.info("Getting sanity default value with passed mode: " + sanitySelectMode);
    if (sanitySelectMode != null) {
      return AbstractEnumParam.getDefaultWithSelectMode(getTerms(), sanitySelectMode, isMultiPick,
          getTermTreeListRef().isEmpty() ? null : getTermTreeListRef().get(0));
    }
    String defaultVal;
    logger.info("Sanity select mode is null; using sanity default (" + sanityDefaultNoSelectMode +
        ") or default (" + getDefaultValue() + ")");
    return (((defaultVal = sanityDefaultNoSelectMode) != null) ? defaultVal : getDefaultValue());
  }

  void addTermValues(String term, String internalVal, String displayVal, String parentTerm) {
    if (internalVal == null || displayVal == null /* || parentTerm == null */ ) {
      StringBuilder badVals = new StringBuilder();
      badVals.append(internalVal == null ? ",internal " : "");
      badVals.append(displayVal == null ? ",display " : "");
      // badVals.append(parentTerm == null ? ",parent " : "");
      throw new IllegalArgumentException(
          "Null { " + badVals.toString().substring(1) + "} value(s) found for term " + term);
    }
    // strip off the comma from term
    term = term.replaceAll(",", " -");
    term = term.replaceAll("`", "'");
    term = term.replaceAll("``", "\"");

    _termInternalMap.put(term, internalVal);
    _termDisplayMap.put(term, displayVal);
    _termParentMap.put(term, parentTerm);
  }

  boolean isEmpty() {
    // all maps should contain the same keys (except top level nodes will have null parents)
    return _termInternalMap.isEmpty();
  }

  int getNumTerms() {
    return _termInternalMap.size();
  }

  Set<String> getTerms() {
    return new LinkedHashSet<String>(_termInternalMap.keySet());
  }

  boolean containsTerm(String term) {
    return _termInternalMap.containsKey(term);
  }

  String getInternal(String term) {
    return _termInternalMap.get(term);
  }

  String getDisplay(String term) {
    return _termDisplayMap.get(term);
  }

  String getParent(String term) {
    return _termParentMap.get(term);
  }

  public Map<String, String> getVocabMap() {
    return new LinkedHashMap<String, String>(_termInternalMap);
  }

  public Map<String, String> getDisplayMap() {
    return new LinkedHashMap<String, String>(_termDisplayMap);
  }

  public Map<String, String> getParentMap() {
    return new LinkedHashMap<String, String>(_termParentMap);
  }

  public String[] getVocab() {
    String[] array = new String[_termInternalMap.size()];
    _termInternalMap.keySet().toArray(array);
    return array;
  }

  public String[] getDisplays() {
    String[] displays = new String[_termDisplayMap.size()];
    _termDisplayMap.values().toArray(displays);
    return displays;
  }

  public EnumParamTermNode[] getVocabTreeRoots() {
    if (_termTreeList != null) {
      EnumParamTermNode[] array = new EnumParamTermNode[_termTreeList.size()];
      _termTreeList.toArray(array);
      return array;
    }
    return new EnumParamTermNode[0];
  }

  public String[] getVocabInternal(boolean isNoTranslation) {
    String[] array = new String[_termInternalMap.size()];
    if (isNoTranslation)
      _termInternalMap.keySet().toArray(array);
    else
      _termInternalMap.values().toArray(array);
    return array;
  }

  void addParentNodeToTree(EnumParamTermNode node) {
    _termTreeList.add(node);
  }

  void unsetParentTerm(String term) {
    _termParentMap.remove(term);
  }

  List<EnumParamTermNode> getTermTreeListRef() {
    return _termTreeList;
  }

  Map<String, String> getDependedValues() {
    return _dependedParamValues;
  }

  void removeTerm(String term) {
    // before removing the term, need to shortcut the children to its parent
    String parent = _termParentMap.get(term);
    for (String child : _termParentMap.keySet()) {
      if (term.equals(_termParentMap.get(child)))
        _termParentMap.put(child, parent);
    }

    _termDisplayMap.remove(term);
    _termInternalMap.remove(term);
    _termParentMap.remove(term);
  }

  /**
   * 
   * @return list of tuples (term, displayName, parentTerm)
   */
  public List<List<String>> getFullVocab() {
    List<List<String>> rows = new ArrayList<List<String>>();
    for (String term : _termDisplayMap.keySet()) {
      List<String> row = new ArrayList<String>();
      row.add(term);
      row.add(_termDisplayMap.get(term));
      row.add(_termParentMap.get(term));
      rows.add(row);
    }
    return rows;
  }

  @Override
  public String getValidStableValue(User user, String stableValue, Map<String, String> contextParamValues)
      throws WdkModelException {
    return _aeParam.getValidStableValue(user, stableValue, contextParamValues, this);
  }

  public String[] getTerms(User user, String stableValue, Map<String, String> contextParamValues)
      throws WdkModelException {
    return (String[]) _aeParam.getRawValue(user, stableValue, contextParamValues);
  }

}
