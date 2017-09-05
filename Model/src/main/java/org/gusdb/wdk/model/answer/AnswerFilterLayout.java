package org.gusdb.wdk.model.answer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;

import org.gusdb.wdk.model.WdkModel;
import org.gusdb.wdk.model.WdkModelBase;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkModelText;
import org.gusdb.wdk.model.record.RecordClass;

/**
 * An object representation of {@code <answerFilterLayout>}. It is used to group
 * {@link AnswerFilterInstance}s into different display groups.
 * 
 * @author xingao
 * 
 */
public class AnswerFilterLayout extends WdkModelBase {

  private static final Logger LOG = Logger.getLogger(AnswerFilterLayout.class);
  
  private RecordClass _recordClass;

  private String _name;
  private String _displayName;
  private boolean _visible = true;
  private String _fileName;
  private boolean _vertical = false;

  private List<WdkModelText> _descriptionList = new ArrayList<WdkModelText>();
  private String _description;

  private List<AnswerFilterInstanceReference> _referenceList = new ArrayList<AnswerFilterInstanceReference>();
  private Map<String, AnswerFilterInstance> _instanceMap = new LinkedHashMap<String, AnswerFilterInstance>();

  // Dec 2013 cris: for the specific filter layout "gene_filters", 
  // we add three maps to handle new jsp for organism gene filters generated by the injector and shared by all websites
  // we should define a tableLayout class with this additional code to obtain colspans

  // all filter names (sorted by filter name which sets distinct filters before instance filters, for a given species)
  private Map<String, AnswerFilterInstance> _sortedInstanceMap;

  // organism count per species (default is sorted as filters in model)
  private Map<String, Integer> _instanceCountMap = new TreeMap<String, Integer>();
  // organism count per genus (family)
  private Map<String, Integer> _sortedFamilyCountMap = new TreeMap<String, Integer>();
  // organism count per phylum (superfamily)
  private Map<String, Integer> _superFamilyCountMap = new TreeMap<String, Integer>();

  /**
   * @return the recordClass
   */
  public RecordClass getRecordClass() {
    return _recordClass;
  }

  /**
   * @param recordClass
   *          the recordClass to set
   */
  public void setRecordClass(RecordClass recordClass) {
    _recordClass = recordClass;
    if (_referenceList != null) {
      for (AnswerFilterInstanceReference reference : _referenceList) {
        reference.setRecordClass(recordClass);
      }
    }
  }

  /**
   * @return the name
   */
  public String getName() {
    return _name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    _name = name;
  }

  /**
   * @return the displayName
   */
  public String getDisplayName() {
    return _displayName;
  }

  /**
   * @param displayName
   *          the displayName to set
   */
  public void setDisplayName(String displayName) {
    _displayName = displayName;
  }

  /**
   * @return the visible
   */
  public boolean isVisible() {
    return _visible;
  }

  /**
   * @param visible
   *          the visible to set
   */
  public void setVisible(boolean visible) {
    _visible = visible;
  }

  public void addDescription(WdkModelText description) {
    _descriptionList.add(description);
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return _description;
  }

  public void addReference(AnswerFilterInstanceReference reference) {
    if (_recordClass != null)
      reference.setRecordClass(_recordClass);
    _referenceList.add(reference);
  }

  @Override
  public void excludeResources(String projectId) throws WdkModelException {
    // exclude the descriptions
    for (WdkModelText text : _descriptionList) {
      if (text.include(projectId)) {
        text.excludeResources(projectId);
        if (_description != null)
          throw new WdkModelException("Description of "
              + "answerFilterLayout '" + _name + "' in "
              + _recordClass.getFullName() + " is included more than once.");
        _description = text.getText();
      }
    }
    _descriptionList = null;

    // exclude the instances
    List<AnswerFilterInstanceReference> newReferences = new ArrayList<AnswerFilterInstanceReference>();
    for (AnswerFilterInstanceReference reference : _referenceList) {
      if (reference.include(projectId)) {
        reference.excludeResources(projectId);
        newReferences.add(reference);
      }
    }
    _referenceList = newReferences;
  }

  @Override
  public void resolveReferences(WdkModel wodkModel) throws WdkModelException {
    if (_resolved)
      return;

    // resolve the instances
    for (AnswerFilterInstanceReference reference : _referenceList) {
      reference.resolveReferences(wodkModel);
      String ref = reference.getRef();
      // for genes_filter
      String[] filterName;
      String familySpecies;
      String family;
      String[] phylumGenusSpecies;
      String phylum;
      //String[] kingdomPhylumGenusSpecies;
      //String kingdom;

      if (_instanceMap.containsKey(ref))
        throw new WdkModelException("More than one instance [" + ref
            + "] are defined in filter layout [" + _name + "]");
      _instanceMap.put(ref, reference.getInstance());

      if (_name.equals("gene_filters")) {	
        _sortedInstanceMap = new TreeMap<String, AnswerFilterInstance>(_instanceMap);

        if (ref.contains("instances") && !ref.contains("distinct")) { 
          // parsing filter name
          filterName = ref.split("_");
          familySpecies = filterName[0]; // possibly KINGDOM-PHYLUM-GENUS-SPECIES
          phylumGenusSpecies = familySpecies.split("-");

          if(phylumGenusSpecies.length >= 3) { //4 if species contains dash...
            phylum = phylumGenusSpecies[0];  //
            family = phylumGenusSpecies[0] + "-" + phylumGenusSpecies[1];
            if (!_superFamilyCountMap.containsKey(phylum)) _superFamilyCountMap.put(phylum, 1);
            else _superFamilyCountMap.put(phylum, _superFamilyCountMap.get(phylum)+1);
          }
          else family = phylumGenusSpecies[0];

          if (!_instanceCountMap.containsKey(familySpecies))  _instanceCountMap.put(familySpecies, 1);
          else _instanceCountMap.put(familySpecies, _instanceCountMap.get(familySpecies)+1);

          if (!_sortedFamilyCountMap.containsKey(family)) _sortedFamilyCountMap.put(family, 1);
          else _sortedFamilyCountMap.put(family, _sortedFamilyCountMap.get(family)+1);
        } // end if it is not a distinct filter

      } //end if gene_filters
    } //end for all filter instances in this layout

    // debug lines
    if (_name.equals("gene_filters")) {	
      // all filter names
      LOG.debug("\n\n===========" + _sortedInstanceMap + "===========\n\n");
      // organism counts per species
      LOG.debug("\n\n===========" + _instanceCountMap + "===========\n\n");
      // organism counts per family (genus)
      LOG.debug("\n\n===========" + _sortedFamilyCountMap + "===========\n\n");
      // organism counts per phylum
      LOG.debug("\n\n===========" + _superFamilyCountMap + "===========\n\n");
    }

    _referenceList = null;
    _resolved = true;
  }

  public Map<String, AnswerFilterInstance> getInstanceMap() {
    return new LinkedHashMap<String, AnswerFilterInstance>(_instanceMap);
  }

  public AnswerFilterInstance[] getInstances() {
    AnswerFilterInstance[] array = new AnswerFilterInstance[_instanceMap.size()];
    _instanceMap.values().toArray(array);
    return array;
  }

 public Map<String, AnswerFilterInstance> getSortedInstanceMap() {
    return new TreeMap<String, AnswerFilterInstance>(_sortedInstanceMap);
  }

  public AnswerFilterInstance[] getSortedInstances() {
    AnswerFilterInstance[] array = new AnswerFilterInstance[_sortedInstanceMap.size()];
    _sortedInstanceMap.values().toArray(array);
    return array;
  }

 public Map<String, Integer> getInstanceCountMap() {
    return new TreeMap<String, Integer>(_instanceCountMap);
  }

 public Map<String, Integer> getSortedFamilyCountMap() {
    return new LinkedHashMap<String, Integer>(_sortedFamilyCountMap);
  }

 public Map<String, Integer> getSuperFamilyCountMap() {
    return new LinkedHashMap<String, Integer>(_superFamilyCountMap);
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return _fileName;
  }

  /**
   * @param fileName
   *          the fileName to set
   */
  public void setFileName(String fileName) {
    _fileName = (fileName == null ? null : fileName.trim());
  }

  /**
   * @return the vertical
   */
  public boolean isVertical() {
    return _vertical;
  }

  /**
   * @param vertical
   *          the vertical to set
   */
  public void setVertical(boolean vertical) {
    _vertical = vertical;
  }

}
