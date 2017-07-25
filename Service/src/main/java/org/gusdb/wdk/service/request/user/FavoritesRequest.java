package org.gusdb.wdk.service.request.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gusdb.fgputil.FormatUtil;
import org.gusdb.wdk.model.WdkModel;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.record.RecordClass;
import org.gusdb.wdk.service.request.exception.RequestMisformatException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FavoritesRequest {
	
  private static Logger LOG = Logger.getLogger(FavoritesRequest.class);
  
  public static final List<String> ACTION_TYPES = new ArrayList<>(Arrays.asList("delete","undelete"));
  
  private Map<String,List<Long>> _favoriteActionMap;
  private RecordClass _recordClass;
  private Map<String,Object> _pkValues;
  private String _note;
  private String _group;
  
  public FavoritesRequest() {
	_favoriteActionMap = null;
    _recordClass = null;
    _pkValues = null;
    _note = null;
	_group = null;
	
  }
  
  public FavoritesRequest(Map<String,List<Long>> favoriteActionMap) {
	this();  
	_favoriteActionMap = favoriteActionMap;
  }

  public FavoritesRequest(RecordClass recordClass, Map<String,Object> pkValues,
		  String note, String group) {
	this();  
	_recordClass = recordClass;
	_pkValues = pkValues;
	_note = note;
	_group = group;
  }
  
  /**
   * Creates a list of multiple favorite ids
   * Input Format:
   * 
   * [Long, Long, ...]
   * 
   * @param jsonArray
   * @return
   */
  public static FavoritesRequest getFavoriteActionMapFromJson(JSONObject json) {
	List<String> actionTypes = ACTION_TYPES;
	List<Object> unrecognizedActions = new ArrayList<>();
	Map<String,List<Long>> favoriteActionMap = new HashMap<>();
	for(Object actionType : json.keySet()) {
	  if(actionTypes.contains(((String)actionType).trim())) {
		List<Long> favoriteIds = new ArrayList<>();
	    JSONArray jsonArray = json.getJSONArray((String)actionType);
	    for(int i = 0; i < jsonArray.length(); i++) {
	      Long favoriteId = jsonArray.getLong(i);	
	      favoriteIds.add(favoriteId);
	    }
	    favoriteActionMap.put((String)actionType, favoriteIds);
	  }
	  else {
		unrecognizedActions.add(actionType);
	  }
	}
	if(!unrecognizedActions.isEmpty()) {
      String unrecognized = FormatUtil.join(unrecognizedActions.toArray(), ",");
      LOG.warn("This user service request contains the following unrecognized sharing actions: " + unrecognized);
    }
    return new FavoritesRequest(favoriteActionMap);
  }
  
  /**
   * Input Format:
   * 
   * {
   *  recordClassName: String,
   *  note: String (optional),
   *  group: String (optional),
   *  id: [
   *    {name : record_id1_name, value : record_id1_value},
   *    {name : record_id2_name: value " record_id2_value},
   *    ...
   *  ]  
   * }
   * 
   * @param json
   * @param model
   * @return
   * @throws RequestMisformatException
   */
  public static FavoritesRequest createFromJson(JSONObject json, WdkModel wdkModel) throws RequestMisformatException {
    try {	
      RecordClass recordClass = null;
      Map<String, Object> pkValues = new LinkedHashMap<String, Object>();
      String recordClassName = json.getString("recordClassName");
      recordClass = wdkModel.getRecordClass(recordClassName);
      List<String> pkColumns = Arrays.asList(recordClass.getPrimaryKeyDefinition().getColumnRefs());
      JSONArray array = json.getJSONArray("id");
      for(int i = 0; i < array.length(); i++) {
        String name = array.getJSONObject(i).getString("name");
        if(!pkColumns.contains(name)) {
          throw new JSONException("Request contains an unknown primary key id " + name);
        }
        pkValues.put(name, array.getJSONObject(i).getString("value"));
      }
      String note = json.has("note") ? json.getString("note") : null;
      String group = json.has("group") ? json.getString("group") : null;
      return new FavoritesRequest(recordClass, pkValues, note, group);
    }
    catch (WdkModelException | JSONException e) {
      String detailMessage = e.getMessage() != null ? e.getMessage() : "No additional information.";
      throw new RequestMisformatException(detailMessage, e);
    }
  }
  
  /**
   * Creates a request with note and group only.  The favorite is identified by a favorite id
   * which appears in the url.
   * Input Format:
   * 
   * {
   *  note: String,
   *  group: String
   * }
   * 
   * @param json
   * @param model
   * @return
   * @throws RequestMisformatException
   */
  public static FavoritesRequest createNoteAndGroupFromJson(JSONObject json, WdkModel wdkModel) throws RequestMisformatException {
    try {
	  String note = json.getString("note");
	  String group = json.getString("group");
	  return new FavoritesRequest(null, null, note, group);
	}
	catch (JSONException e) {
      String detailMessage = e.getMessage() != null ? e.getMessage() : "No additional information.";
      throw new RequestMisformatException(detailMessage, e);
    }
  }

  public RecordClass getRecordClass() {
	return _recordClass;
  }

  public Map<String, Object> getPkValues() {
	return _pkValues;
  }
  
  public String getNote() {
	return _note;
  }
  
  public String getGroup() {
    return _group;
  }

  public Map<String, List<Long>> getFavoriteActionMap() {
	return _favoriteActionMap;
  }
  
  
  
}
