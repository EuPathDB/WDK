package org.gusdb.wdk.model;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;

public class RecordInstance {
    
    String primaryKey;
    RecordClass recordClass;
    HashMap attributesResultSetsMap;
    HashMap summaryAttributeMap;
    Answer answer;

    public RecordInstance(RecordClass recordClass) {
	this.recordClass = recordClass;
	attributesResultSetsMap = new HashMap();
	summaryAttributeMap = new HashMap();
    }

    public RecordClass getRecordClass() { return recordClass; }

    public void setPrimaryKey(String primaryKey) {
	this.primaryKey = primaryKey;
    }

    public String getPrimaryKey() {
	return primaryKey;
    }

    /**
     * Get the value for a attribute or a text attribute
     */
    public Object getAttributeValue(String attributeName) throws WdkModelException {
	Object value;
	FieldI field = (FieldI)recordClass.getField(attributeName); 
	if (field instanceof PrimaryKeyField) {
	    value = recordClass.getIdPrefix() + getPrimaryKey();

	} else if (field instanceof TextAttributeField) {
	    TextAttributeField taField = (TextAttributeField)field;
	    value = instantiateTextAttribute(attributeName, 
					     taField, 
					     new HashMap());

	} else if (field instanceof LinkAttributeField) {
	    LinkAttributeField laField = (LinkAttributeField)field;
	    value = instantiateLinkAttribute(attributeName, 
					     laField, 
					     new HashMap());

	} else if (field instanceof AttributeField){
	    AttributeField aField = (AttributeField)field;
	    Query query = aField.getQuery();
	    String queryName = query.getName();

	    if (!attributesResultSetsMap.containsKey(queryName)) {
		runAttributesQuery(query);
	    }
	    HashMap resultMap = (HashMap)attributesResultSetsMap.get(queryName);
	    if (resultMap == null) {
	        throw new WdkModelException("Unable to get resultMap for queryName of '"+queryName+"'");
	    }
	    value = resultMap.get(attributeName);
	} else {
	    throw new WdkModelException("Unsupported field type: " + field.getClass());
	}
	return value;
    }

    public ResultList getTableValue(String tableName) throws WdkModelException {
	Query query = recordClass.getTableField(tableName).getQuery();
	QueryInstance instance = query.makeInstance();
	instance.setIsCacheable(false);
	HashMap paramHash = new HashMap();
	if (primaryKey == null) 
	    throw new WdkModelException("primaryKey is null");
	paramHash.put("primaryKey", primaryKey);
	try {
	    instance.setValues(paramHash);
	} catch (WdkUserException e) {
	    throw new WdkModelException(e);
	}
	return instance.getResult();
    }

    /**
     * @return Map of tableName -> TableFieldValue
     */
    public Map getTables() {
	return new FieldValueMap(recordClass, this, true);
    }

    /**
     * @return Map of tableName -> AttributeFieldValue
     */

    public Map getAttributes() {
	return new FieldValueMap(recordClass, this, false);
	
    }

    public String print() throws WdkModelException, WdkUserException {

	String newline = System.getProperty( "line.separator" );
	StringBuffer buf = new StringBuffer();
	
	Map attributeFields = getAttributes();
	
	HashMap summaryAttributes = new HashMap();
	HashMap nonSummaryAttributes = new HashMap();
	
	splitSummaryAttributes(attributeFields, summaryAttributes, nonSummaryAttributes);

	printAtts_Aux(buf, "Summary Attributes: " + newline, summaryAttributes);
	printAtts_Aux(buf, "Non-Summary Attributes: " + newline, nonSummaryAttributes);
	
	Map tableFields = getTables();
	Iterator fieldNames = tableFields.keySet().iterator();
	
	while (fieldNames.hasNext()) {
	    
	    String fieldName = (String)fieldNames.next();
	    TableFieldValue field = 
		(TableFieldValue)tableFields.get(fieldName);
	    buf.append("Table " + field.getDisplayName()).append( newline );
	    ResultList resultList = getTableValue(fieldName);
	    resultList.write(buf);
	    resultList.close();
	    field.closeResult();
	    buf.append(newline);
	}
	
	buf.append("Nested Records belonging to this RecordInstance:" + newline);
	NestedRecord nr[] = this.recordClass.getNestedRecords();
	if (nr != null){
	    for (int i = 0; i < nr.length; i++){
		NestedRecord nextNr = nr[i];
		RecordInstance ri = nextNr.getRecordInstance(this);
		buf.append(nextNr.getFullName() + newline);
		//decide whether record instances should keep track of their nested records' record instances.
		buf.append (ri.printSummary());
	    }
	}

	buf.append("Nested Record Lists belonging to this RecordInstance:" + newline);
	NestedRecordList nrList[] = this.recordClass.getNestedRecordLists();
	if (nrList != null){
	    for (int i = 0; i < nrList.length; i++){
		NestedRecordList nextNr = nrList[i];
		RecordInstance riList[] = nextNr.getRecordInstances(this);
		buf.append(nextNr.getFullName() + newline);
		for (int j = 0; j < riList.length; j++){

		    buf.append (riList[j].printSummary() + newline);
		}
	    }
	}
	
	return buf.toString();
    }
    
    public String printSummary() throws WdkModelException {

	String newline = System.getProperty( "line.separator" );
	StringBuffer buf = new StringBuffer();
	
	Map attributeFields = getAttributes();
	
	HashMap summaryAttributes = new HashMap();
	HashMap nonSummaryAttributes = new HashMap();
	
	splitSummaryAttributes(attributeFields, summaryAttributes, nonSummaryAttributes);

	printAtts_Aux(buf, "Summary Attributes: " + newline, summaryAttributes);
	return buf.toString();
    }
	


    ///////////////////////////////////////////////////////////////////////////
    // package methods
    ///////////////////////////////////////////////////////////////////////////

    void setAnswer(Answer answer){
	this.answer = answer;
    }

    void setSummaryAttributeList(String[] summaryAttributeList){
	if (summaryAttributeList != null){
	    for (int i = 0; i < summaryAttributeList.length; i++){
		summaryAttributeMap.put(summaryAttributeList[i], new Integer(1));
	    }
	}
    }

    public boolean isSummaryAttribute(String attName){
	
	if (answer != null){
	    return answer.isSummaryAttribute(attName);
	}
	else return false;

    }

    ///////////////////////////////////////////////////////////////////////////
    // protected methods
    ///////////////////////////////////////////////////////////////////////////

    protected void setAttributeValue(String attributeName, Object attributeValue) throws WdkModelException{
	
	AttributeField field 
	    = (AttributeField)recordClass.getField(attributeName);
	String queryName = field.getQuery().getName();
	HashMap resultMap = (HashMap)attributesResultSetsMap.get(queryName);
	if (resultMap == null){
	    resultMap = new HashMap();
	    attributesResultSetsMap.put(queryName, resultMap);
	}
	resultMap.put(attributeName, attributeValue);
    }

    /**
     * Place hash of single row result into hash keyed on query name
     */
    protected void runAttributesQuery(Query query) throws WdkModelException {
	QueryInstance qInstance = query.makeInstance();
	qInstance.setIsCacheable(false);

	// If in the context of an Answer, then we are doing a "summary"
	// and need to be in multi mode
	if (answer != null){
	    answer.setMultiMode(qInstance);
	    ResultList rl = qInstance.getResult();
	    answer.setQueryResult(rl);
	    rl.close();
	}	
	else{ //do it all myself
	    HashMap paramHash = new HashMap();
	    if (primaryKey == null) 
		throw new WdkModelException("primaryKey is null");
	    paramHash.put("primaryKey", primaryKey);
	    try {
		qInstance.setValues(paramHash);
	    } catch (WdkUserException e) {
		throw new WdkModelException(e);
	    }
	    ResultList rl = qInstance.getResult();
	    //	rl.checkQueryColumns(query, true);
	    
	    Column[] columns = query.getColumns();
	    if (!rl.next()) {
		String msg = "Attributes query '" + query.getFullName() + "' in Record '" + recordClass.getFullName() + "' does not return any rows";
		throw new WdkModelException(msg);
	    }
	    for (int i=0; i<columns.length; i++) {
		String columnName = columns[i].getName();
		setAttributeValue(columnName, 
				  rl.getAttributeFieldValue(columnName).getValue());
	    }
	    if (rl.next()) {
		String msg = "Attributes query '" + query.getFullName() + "' in Record '" + recordClass.getFullName() + "' returns more than one row";
		throw new WdkModelException(msg);
	    }
	    rl.close();
	}
    }

    protected String instantiateTextAttribute(String textAttributeName, 
					      TextAttributeField field, 
					      HashMap alreadyVisited) throws WdkModelException {

	if (alreadyVisited.containsKey(textAttributeName)) {
	    throw new WdkModelException("Circular text attribute subsitution involving text attribute '" 
					+ textAttributeName + "'");
	}

	alreadyVisited.put(textAttributeName, textAttributeName);
	return instantiateAttr(field.getText(), textAttributeName);
    }

    protected LinkValue instantiateLinkAttribute(String linkAttributeName, 
						 LinkAttributeField field, 
						 HashMap alreadyVisited) throws WdkModelException {

	if (alreadyVisited.containsKey(linkAttributeName)) {
	    throw new WdkModelException("Circular link attribute subsitution involving text attribute '" 
					+ linkAttributeName + "'");
	}

	alreadyVisited.put(linkAttributeName, linkAttributeName);

	return new LinkValue(instantiateAttr(field.getVisible(), 
					     linkAttributeName),
			     instantiateAttr(field.getUrl(), 
					     linkAttributeName));
    }

    private String instantiateAttr(String rawText, String targetAttrName) throws WdkModelException { 
	String instantiatedText = rawText;

	Iterator attributeNames = getAttributes().keySet().iterator();
	while (attributeNames.hasNext()) {
	    String attrName = (String)attributeNames.next();
	    if (attrName.equals(targetAttrName)) continue;
	    if (containsMacro(instantiatedText, attrName)) {
		String valString =  
		    getAttributeValue(attrName.toString()).toString();
		instantiatedText = instantiateText(instantiatedText, 
						   attrName, 
						   valString);
	    }
	}

	checkInstantiatedText(instantiatedText);

	return instantiatedText;
    }

    /**
     * Given a map of all attributes in this recordInstance, separate them into those that are summary attributes
     * and those that are not summary attributes.  Place results into @param summaryAttributes and @param
     * nonSummaryAttributes.
     */
    
    private void splitSummaryAttributes(Map attributeFields, Map summaryAttributes, Map nonSummaryAttributes){

	Iterator fieldNames = attributeFields.keySet().iterator();
	while (fieldNames.hasNext()) {
	    String fieldName = (String)fieldNames.next();
	    AttributeFieldValue field = 
		(AttributeFieldValue)attributeFields.get(fieldName);
	    if (field.isSummary()){
		summaryAttributes.put(fieldName, field);
	    }
	    else {
		nonSummaryAttributes.put(fieldName, field);
	    }
	}
    }


    private void printAtts_Aux(StringBuffer buf, String header, Map attributeFields){
	String newline = System.getProperty( "line.separator" );
	buf.append(header);
	
	Iterator fieldNames = attributeFields.keySet().iterator();
	while (fieldNames.hasNext()) {
	    String fieldName = (String)fieldNames.next();

	    AttributeFieldValue field = 
		(AttributeFieldValue)attributeFields.get(fieldName);
	    buf.append(field.getDisplayName() + ":   " + 
		       field.getBriefValue()).append( newline );
	}
	buf.append(newline);
    }

    ////////////////////////////////////////////////////////////////////
    //   static
    ////////////////////////////////////////////////////////////////////

    /**
     * substitute a value for a macro in a text string.  The macro is delimited by $$
     @param text the text which contains the macro
     @param macroName the name of the macro, without the delimiter
     @param value the value to substitute in
     */
    public static String instantiateText(String text, String macroName, String value) {
	String macro = "$$" + macroName + "$$";
	String macroRegex = "\\$\\$" + macroName + "\\$\\$";
	if (text.indexOf(macro) != -1) {
	    text = text.replaceAll(macroRegex, value);
	}
	return text;
    }

    public static boolean containsMacro(String text, String macroName) {
	String macro = "$$" + macroName + "$$";
	return text.indexOf(macro) != -1;
    }

    public static void checkInstantiatedText(String instantiatedText) throws WdkModelException {
	if (instantiatedText.matches("\\$\\$\\w+\\$\\$")) 
	    throw new WdkModelException ("'" + instantiatedText + 
				 "' contains unrecognized macro");
    }
	
}
