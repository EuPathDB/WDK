package org.gusdb.wdk.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

public class RecordInstance {

    public static final int MAXIMUM_NESTED_RECORD_INSTANCES = 1000;

    private static Logger logger = Logger.getLogger(RecordInstance.class);

    PrimaryKeyValue primaryKey;
    RecordClass recordClass;
    Map<String, Map> attributesResultSetsMap;
    Map<String, Integer> summaryAttributeMap;
    Answer answer;
    Map<String, AttributeField> dynamicAttributeFields = new LinkedHashMap<String, AttributeField>();
    boolean timeAttributeQueries = false;

    public RecordInstance(RecordClass recordClass, String recordId)
            throws WdkModelException {
        this(recordClass, null, recordId);
    }

    public RecordInstance(RecordClass recordClass, String projectId,
            String recordId) throws WdkModelException {
        this.recordClass = recordClass;
        attributesResultSetsMap = new LinkedHashMap<String, Map>();
        summaryAttributeMap = new LinkedHashMap<String, Integer>();
        setPrimaryKey(projectId, recordId);
    }

    /**
     * @return The type that these records belong to.
     */
    public RecordClass getRecordClass() {
        return recordClass;
    }

    /**
     * Modified by Jerric - Use two parts as primarykeyValue, projectId and the
     * original recordId String
     * 
     * @param projectId The project key.
     * @param recordId The primary key this instance.
     * @throws WdkModelException If the recordId is null or empty.
     */
    private void setPrimaryKey(String projectId, String recordId)
            throws WdkModelException {
        PrimaryKeyField field = (PrimaryKeyField) recordClass.getAttributeField(RecordClass.PRIMARY_KEY_NAME);
        // create primary key
        if (recordId == null || "".equals(recordId)) {
            throw new WdkModelException(getRecordClass().getFullName()
                    + " encountered empty primaryKey value");
        }
        this.primaryKey = new PrimaryKeyValue(field, projectId, recordId);
    }

    /**
     * Modified by Jerric - use an object for primaryKeyValue
     * 
     * @return
     */
    public PrimaryKeyValue getPrimaryKey() {
        return primaryKey;
    }

    /**
     * @see org.gusdb.wdk.model.RecordInstance.getAttributeValue(AttributeField)
     */
    public Object getAttributeValue(String attributeName)
            throws WdkModelException {
        AttributeField attrField = null;
        try {
            attrField = recordClass.getAttributeField(attributeName);
        } catch (WdkModelException ex) {
            // the field is not defined in the RecordClass, then check it in the
            // DynamicAttributeSet
            if (dynamicAttributeFields != null)
                attrField = dynamicAttributeFields.get(attributeName);
            if (attrField == null) {
                String msg = "The attribute field '" + attributeName
                        + "' is not defined in the RecordClass "
                        + recordClass.getFullName();
                if (answer != null)
                    msg += ", neither in the DynamicAttributeSet of Question "
                            + answer.getQuestion().getFullName();
                logger.error(msg);
                throw new WdkModelException(msg);
            }
        }
        return getAttributeValue(attrField);
    }

    /**
     * Get the value for attributes defined on a Questions.
     * The most important of the attributes is the ColumnAttribute.
     * This Attribute is actually found by constructing a query that
     * will be circularly put into the Answer which will call this
     * class back to set Attributes value.
     * (who new useless circular references where so fun)
     * 
     * @param field The Attribute you want the actual value for.
     * @return The value for the Attribute.
     * @throws WdkModelException if a problem occurs with getting the
     * 		attribute's value.
     */
    public Object getAttributeValue(AttributeField field)
            throws WdkModelException {
        Object value;
        if (field instanceof PrimaryKeyField) {
            // modified by Jerric
            // value = recordClass.getIdPrefix() + getPrimaryKey();
            value = getPrimaryKey().getValue();

        } else if (field instanceof TextAttributeField) {
            TextAttributeField taField = (TextAttributeField) field;
            value = instantiateTextAttribute(taField.getName(), taField,
                    new LinkedHashMap<String, String>());

        } else if (field instanceof LinkAttributeField) {
            LinkAttributeField laField = (LinkAttributeField) field;
            value = instantiateLinkAttribute(laField.getName(), laField,
                    new LinkedHashMap<String, String>());

        } else if (field instanceof ColumnAttributeField) {
            ColumnAttributeField aField = (ColumnAttributeField) field;
            Query query = aField.getQuery();

            // TEST
            // logger.debug("Field: " + aField.name);
            // logger.debug("Query: " + query.getFullName());

            String queryName = query.getName();
            String attributeName = field.getName();

            if (!attributesResultSetsMap.containsKey(queryName)) {
                runAttributesQuery(query);
            }
            Map resultMap = attributesResultSetsMap.get(queryName);
            if (resultMap == null) {
                throw new WdkModelException(
                        "Attempting to find a value for attribute '"
                                + attributeName
                                + "' in recordClass '"
                                + recordClass.getName()
                                + "'.  It is claiming to come from query amed '"
                                + queryName
                                + "' but there is no resultMap for that name.");
            }
            value = resultMap.get(attributeName);
        } else {
            throw new WdkModelException("Unsupported field type: "
                    + field.getClass());
        }
        return value;
    }

    public TableFieldValue getTableValue(String tableName)
            throws WdkModelException {
        // get the table field
        TableField tableField = recordClass.getTableField(tableName);
        Query query = tableField.getQuery();
        QueryInstance instance = query.makeInstance();
        instance.setIsCacheable(false);
        Map<String, Object> paramHash = new LinkedHashMap<String, Object>();
        if (primaryKey == null)
            throw new WdkModelException("primaryKey is null");

        String projectId = primaryKey.getProjectId();
        if (projectId != null)
            paramHash.put(RecordClass.PROJECT_ID_NAME, projectId);
        paramHash.put(RecordClass.PRIMARY_KEY_NAME, primaryKey.getRecordId());

        instance.setValues(paramHash);
        return new TableFieldValue(tableField, instance.getResult());
    }

    /**
     * @return Map of tableName -> TableFieldValue
     */
    public Map getTables() {
        return new FieldValueMap(recordClass, this, FieldValueMap.TABLE_MAP,
                null);
    }

    /**
     * @return Map of attributeName -> AttributeFieldValue
     */

    public Map<String, AttributeFieldValue> getAttributes() {
        return new FieldValueMap(recordClass, this,
                FieldValueMap.ATTRIBUTE_MAP, dynamicAttributeFields);

    }

    /**
     * This will include dynamic attributes if they appear in the
     * summaryAttributeList
     * 
     * @return Map of summaryAttributeName -> AttributeFieldValue
     */

    public Map getSummaryAttributes() {
        return new FieldValueMap(recordClass, this,
                FieldValueMap.SUMMARY_ATTRIBUTE_MAP, dynamicAttributeFields);
    }

    // change name of method?
    public Map<String, RecordInstance> getNestedRecordInstances()
            throws WdkModelException, WdkUserException {

        Map<String, RecordInstance> riMap = new LinkedHashMap<String, RecordInstance>();
        Question nq[] = this.recordClass.getNestedRecordQuestions();

        if (nq != null) {
            for (int i = 0; i < nq.length; i++) {
                Question nextNq = nq[i];
                Answer a = getNestedRecordAnswer(nextNq);
                // TODO
                // the reset function is no longer available; instead call
                // cloneAnswer() to get a new answer object and work on it
                // a.resetRecordInstanceCounter();
                a = a.newAnswer();
                RecordInstance nextRi = a.getNextRecordInstance();

                if (a.getNextRecordInstance() != null) {
                    throw new WdkModelException(
                            "NestedQuestion "
                                    + nextNq.getName()
                                    + " returned more than one RecordInstance when called from "
                                    + this.recordClass.getName());
                }
                if (nextRi != null) {
                    riMap.put(nextNq.getName(), nextRi);
                }
            }
        }
        return riMap;
    }

    public Map<String, RecordInstance[]> getNestedRecordInstanceLists()
            throws WdkModelException, WdkUserException {

        Question nql[] = this.recordClass.getNestedRecordListQuestions();
        Map<String, RecordInstance[]> riListMap = new LinkedHashMap<String, RecordInstance[]>();

        if (nql != null) {
            for (int i = 0; i < nql.length; i++) {
                Question nextNql = nql[i];
                Answer a = getNestedRecordAnswer(nextNql);
                Vector<RecordInstance> riVector = new Vector<RecordInstance>();
                while (a.hasMoreRecordInstances()) {
                    RecordInstance nextRi = a.getNextRecordInstance();
                    riVector.add(nextRi);
                }
                RecordInstance[] riList = new RecordInstance[riVector.size()];
                riVector.toArray(riList);
                if (riList != null) {
                    riListMap.put(nextNql.getName(), riList);
                }
            }
        }
        return riListMap;
    }

    /**
  	 * Give a maximum time to do the filling in.
  	 * 
     * @param doTiming Whether there is a time limit.
     */
    public void setTimeAttributeQueries(boolean doTiming) {
        timeAttributeQueries = doTiming;
    }

    /**
     * @return True if the AttributeQueries are timed.
     */
    public boolean getTimeAttributeQueries() {
        return timeAttributeQueries;
    }

    private Answer getNestedRecordAnswer(Question q) throws WdkModelException,
            WdkUserException {

        Param nestedQueryParams[] = q.getQuery().getParams();
        Map<String, Object> queryValues = new LinkedHashMap<String, Object>();
        for (int j = 0; j < nestedQueryParams.length; j++) {
            Param nextParam = nestedQueryParams[j];
            String paramName = nextParam.getName();

            String value;
            if (paramName.equalsIgnoreCase("projectId")) {
                value = this.getPrimaryKey().getProjectId();
            } else {
                AttributeField field = this.getRecordClass().getAttributeField(
                        paramName);
                if (field instanceof PrimaryKeyField) {
                    value = this.getPrimaryKey().getRecordId();
                } else if (field instanceof AttributeField) {
                    value = this.getAttributeValue(paramName).toString();
                } else {
                    throw new WdkModelException(
                            "Illegal to link NestedRecordList " + q.getName()
                                    + " on attribute of type "
                                    + field.getClass().getName());
                }
            }

            queryValues.put(paramName, value);
        }
        Answer a = q.makeAnswer(queryValues, 1, MAXIMUM_NESTED_RECORD_INSTANCES);
        return a;
    }

    // maybe change this to RecordInstance[][] for jspwrap purposes?
    /*
     * public Vector getNestedRecordListInstances() throws WdkModelException,
     * WdkUserException{ NestedRecordList nrLists[] =
     * this.recordClass.getNestedRecordLists(); Vector nrVector = new Vector();
     * if (nrLists != null){ for (int i = 0; i < nrLists.length; i++){
     * NestedRecordList nextNrList = nrLists[i]; RecordInstance riList[] =
     * nextNrList.getRecordInstances(this); nrVector.add(riList); } } return
     * nrVector; }
     */

    public String print() throws WdkModelException, WdkUserException {

        String newline = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer();

        Map<String, AttributeFieldValue> attributeFields = getAttributes();

        Map<String, AttributeFieldValue> summaryAttributes = new LinkedHashMap<String, AttributeFieldValue>();
        Map<String, AttributeFieldValue> nonSummaryAttributes = new LinkedHashMap<String, AttributeFieldValue>();

        splitSummaryAttributes(attributeFields, summaryAttributes,
                nonSummaryAttributes);

        printAtts_Aux(buf, summaryAttributes);
        printAtts_Aux(buf, nonSummaryAttributes);

        Map tableFields = getTables();
        Iterator fieldNames = tableFields.keySet().iterator();

        while (fieldNames.hasNext()) {

            String fieldName = (String) fieldNames.next();

            long startTime = System.currentTimeMillis();

            TableFieldValue fieldValue = (TableFieldValue) tableFields.get(fieldName);

            buf.append(newline);
            buf.append("[Table]: " + fieldValue.getDisplayName()).append(newline);
            fieldValue.write(buf);
            fieldValue.closeResult();

            if (getTimeAttributeQueries()) {
                buf.append("TIME INFO: " + fieldName + " took "
                        + (System.currentTimeMillis() - startTime) / 1000F
                        + " seconds to retrieve.\n");
            }

        }

        buf.append(newline);
        buf.append("Nested Records belonging to this RecordInstance:" + newline);
        Map nestedRecords = getNestedRecordInstances();
        Iterator recordNames = nestedRecords.keySet().iterator();
        while (recordNames.hasNext()) {
            String nextRecordName = (String) recordNames.next();
            RecordInstance nextNr = (RecordInstance) nestedRecords.get(nextRecordName);
            buf.append("***" + nextRecordName + "***" + newline
                    + nextNr.printSummary() + newline);
        }

        buf.append("Nested Record Lists belonging to this RecordInstance:"
                + newline);

        Map nestedRecordLists = getNestedRecordInstanceLists();
        Iterator recordListNames = nestedRecordLists.keySet().iterator();
        while (recordListNames.hasNext()) {
            String nextRecordListName = (String) recordListNames.next();
            RecordInstance nextNrList[] = (RecordInstance[]) nestedRecordLists.get(nextRecordListName);
            buf.append("***" + nextRecordListName + "***" + newline);
            for (int i = 0; i < nextNrList.length; i++) {
                buf.append(nextNrList[i].printSummary() + newline);
            }
        }

        return buf.toString();
    }

    public String printSummary() throws WdkModelException {

        StringBuffer buf = new StringBuffer();

        Map<String, AttributeFieldValue> attributeFields = getAttributes();

        Map<String, AttributeFieldValue> summaryAttributes = new LinkedHashMap<String, AttributeFieldValue>();
        Map<String, AttributeFieldValue> nonSummaryAttributes = new LinkedHashMap<String, AttributeFieldValue>();

        splitSummaryAttributes(attributeFields, summaryAttributes,
                nonSummaryAttributes);

        printAtts_Aux(buf, summaryAttributes);
        return buf.toString();
    }

    public String toXML() throws WdkModelException, WdkUserException {
        return toXML("");
    }

    public String toXML(String ident) throws WdkModelException,
            WdkUserException {
        String newline = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer();

        String rootStart = ident + "<" + getRecordClass().getFullName() + ">"
                + newline + ident + "<li>" + newline;
        String rootEnd = ident + "</li>" + newline + ident + "</"
                + getRecordClass().getFullName() + ">" + newline;
        ident = ident + "    ";
        buf.append(rootStart);

        Map attributeFields = getAttributes();
        Iterator fieldNames = attributeFields.keySet().iterator();
        while (fieldNames.hasNext()) {
            String fieldName = (String) fieldNames.next();
            AttributeFieldValue field = (AttributeFieldValue) attributeFields.get(fieldName);
            buf.append(ident + "<" + field.getName() + ">" + field.getValue()
                    + "</" + field.getName() + ">" + newline);
        }

        Map tableFields = getTables();
        fieldNames = tableFields.keySet().iterator();
        while (fieldNames.hasNext()) {
            String fieldName = (String) fieldNames.next();
            buf.append(ident + "<" + fieldName + ">" + newline);

            TableFieldValue tableValue = getTableValue(fieldName);
            tableValue.toXML(buf, "li", ident);
            buf.append(ident + "</" + fieldName + ">" + newline);
        }

        Map nestedRecords = getNestedRecordInstances();
        Iterator recordNames = nestedRecords.keySet().iterator();
        while (recordNames.hasNext()) {
            String nextRecordName = (String) recordNames.next();
            RecordInstance nextNr = (RecordInstance) nestedRecords.get(nextRecordName);
            buf.append(nextNr.toXML(ident));
        }

        Map nestedRecordLists = getNestedRecordInstanceLists();
        Iterator recordListNames = nestedRecordLists.keySet().iterator();
        while (recordListNames.hasNext()) {
            String nextRecordListName = (String) recordListNames.next();
            RecordInstance nextNrList[] = (RecordInstance[]) nestedRecordLists.get(nextRecordListName);
            for (int i = 0; i < nextNrList.length; i++) {
                buf.append(nextNrList[i].toXML(ident) + newline);
            }
        }

        buf.append(rootEnd);

        return buf.toString();
    }

    // /////////////////////////////////////////////////////////////////////////
    // package methods
    // /////////////////////////////////////////////////////////////////////////

    void setDynamicAttributeFields(Map<String, AttributeField> dynaAttribs) {
        dynamicAttributeFields = dynaAttribs;
    }

    void setAnswer(Answer answer) {
        this.answer = answer;
    }

    void setSummaryAttributeList(String[] summaryAttributeList) {
        if (summaryAttributeList != null) {
            for (int i = 0; i < summaryAttributeList.length; i++) {
                summaryAttributeMap.put(summaryAttributeList[i], i);
            }
        }
    }

    Map<String, Integer> getSummaryAttributesMap() {
        return summaryAttributeMap;
    }

    public boolean isSummaryAttribute(String attName) {

        if (answer != null) {
            return answer.isSummaryAttribute(attName);
        } else return false;

    }

    // /////////////////////////////////////////////////////////////////////////
    // protected methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Set the value of any Attribute in the Query specified for this
     * ResultInstance.
     * 
     * @param attributeName The Attribute for which to set the value.
     * @param attributeValue The value for the Attribute.
     * @param query The Query this Attribute belongs to.
     * 
     * @throws WdkModelException Does not really throw this exception.
     */
    protected void setAttributeValue(String attributeName,
            Object attributeValue, Query query) throws WdkModelException {

        String queryName = query.getName();
        Map resultMap = attributesResultSetsMap.get(queryName);
        if (resultMap == null) {
            resultMap = new LinkedHashMap();
            attributesResultSetsMap.put(queryName, resultMap);
        }
        resultMap.put(attributeName, attributeValue);
    }

    /**
     * Set a value for a ColumnAttribute in this RecordInstance.
     * 
     * @param attributeName The name of the <code>ColumnAttribute</code> to give a value.
     * @param attributeValue The value to set for the ColumnAttribute.
     * @throws WdkModelException Doesn't really throw this exception.
     */
    protected void setAttributeValue(String attributeName, Object attributeValue)
            throws WdkModelException {
        ColumnAttributeField field = (ColumnAttributeField) recordClass.getAttributeField(attributeName);
        setAttributeValue(attributeName, attributeValue, field.getQuery());
    }

    /**
     * Place hash of single row result into hash keyed on query name
     * <br/><br/>
     * If if this RecordInstance is actually used by a <code>Answer</code>
     * object then this query is actually fed to the Answer
     * 
     * @param query The <code>Query</code> for the Attributes(Columns).
     *  
     * @see org.gusdb.wdk.model.Answer.integrateAttributesQueryResult
     */
    protected void runAttributesQuery(Query query) throws WdkModelException {
        // TEST
        // logger.debug("runAttributeQuery: " + query.getFullName());

        QueryInstance qInstance = query.makeInstance();
        qInstance.setIsCacheable(false);

        // If in the context of an Answer, then we are doing a "summary"
        // and need to do a join against the result table
        if (answer != null) {
            answer.integrateAttributesQueryResult(qInstance);
        }

        // otherwise, set values in record directly
        else {

            Map<String, Object> paramHash = new LinkedHashMap<String, Object>();
            if (primaryKey == null)
                throw new WdkModelException("primaryKey is null");

            String projectId = primaryKey.getProjectId();
            if (projectId != null)
                paramHash.put(RecordClass.PROJECT_ID_NAME, projectId);

            paramHash.put(RecordClass.PRIMARY_KEY_NAME,
                    primaryKey.getRecordId());

            qInstance.setValues(paramHash);

            ResultList rl = qInstance.getResult();

            boolean haveRow = rl.next();

            Column[] columns = query.getColumns();
            for (int i = 0; i < columns.length; i++) {
                String columnName = columns[i].getName();
                if (recordClass.getAttributeFieldMap().get(columnName) == null)
                    continue;
                Object val = haveRow ? rl.getValue(columnName) : "null";
                setAttributeValue(columnName, val);
            }

            if (rl.next()) {
                String msg = "Attributes query '" + query.getFullName()
                        + "' in Record '" + recordClass.getFullName()
                        + "' returns more than one row";
                throw new WdkModelException(msg);
            }

            rl.close();
        }
    }

    /**
     * Take a TextAttribute in the model and replace the macros contained
     * within with values generated by a Query.
     * 
     * @param textAttributeName The name the macro to replace.
     * @param field The TextAttribute on which to replace.
     * @param alreadyVisited Keep track of replaced macros.
     * @return A String with the macros replaced.
     * @throws WdkModelException If the macro was already replaced.
     */
    protected String instantiateTextAttribute(String textAttributeName,
            TextAttributeField field, Map<String, String> alreadyVisited)
            throws WdkModelException {

        if (alreadyVisited.containsKey(textAttributeName)) {
            throw new WdkModelException(
                    "Circular text attribute subsitution involving text attribute '"
                            + textAttributeName + "'");
        }

        alreadyVisited.put(textAttributeName, textAttributeName);
        return instantiateAttr(field.getText(), textAttributeName);
    }

    /**
     * Take a LinkAttribute and replace the macros in it with correct
     * Query generated values.
     * 
     * @param linkAttributeName Name of the macro to replace.
     * @param field The LinkAttribute to remove the macro in.
     * @param alreadyVisited A map to keep track of already replaced macros.
     * @return A LinkValue with the macro replaced.
     * @throws WdkModelException If we've already replaced the macro.
     */
    protected LinkValue instantiateLinkAttribute(String linkAttributeName,
            LinkAttributeField field, Map<String, String> alreadyVisited)
            throws WdkModelException {

        if (alreadyVisited.containsKey(linkAttributeName)) {
            throw new WdkModelException(
                    "Circular link attribute subsitution involving text attribute '"
                            + linkAttributeName + "'");
        }

        alreadyVisited.put(linkAttributeName, linkAttributeName);

        return new LinkValue(instantiateAttr(field.getVisible(),
                linkAttributeName), instantiateAttr(field.getUrl(),
                linkAttributeName), field);
    }

    /**
     * Replace "macros" in a String. Macros in this case refer to replaceable
     * text that the platform will replace with a Query generated string.
     * 
     * @param rawText The original text entered into the model.
     * @param targetAttrName The attribute name to replace, aka the macro name.
     * @return The String with the macro replaced by the real value.
     * @throws WdkModelException If a macro still exists.
     */
    private String instantiateAttr(String rawText, String targetAttrName)
            throws WdkModelException {
        String instantiatedText = rawText;

        Map<String, AttributeFieldValue> attributes = getAttributes();
        Iterator attributeNames = attributes.keySet().iterator();
        while (attributeNames.hasNext()) {
            String attrName = (String) attributeNames.next();
            if (attrName.equals(targetAttrName)) continue;
            if (containsMacro(instantiatedText, attrName)) {
                Object attributeValue = getAttributeValue(attrName);
                String valString = (attributeValue == null) ? ""
                        : attributeValue.toString();
                instantiatedText = instantiateText(instantiatedText, attrName,
                        valString);
            }
        }

        checkInstantiatedText(instantiatedText);

        return instantiatedText;
    }

    /**
     * Given a map of all attributes in this recordInstance, separate them into
     * those that are summary attributes and those that are not summary
     * attributes. Place results into
     * 
     * @param summaryAttributes
     *            and
     * @param nonSummaryAttributes.
     */

    private void splitSummaryAttributes(
            Map<String, AttributeFieldValue> attributes,
            Map<String, AttributeFieldValue> summaryAttributes,
            Map<String, AttributeFieldValue> nonSummaryAttributes) {

        Iterator<String> fieldNames = attributes.keySet().iterator();
        // if (fieldNames
        while (fieldNames.hasNext()) {
            String fieldName = (String) fieldNames.next();
            AttributeFieldValue attribute = (AttributeFieldValue) attributes.get(fieldName);
            if (attribute.isSummary()) {
                summaryAttributes.put(fieldName, attribute);
            } else {
                nonSummaryAttributes.put(fieldName, attribute);
            }
        }
    }

    private void printAtts_Aux(StringBuffer buf,
            Map<String, AttributeFieldValue> attributes) {
        String newline = System.getProperty("line.separator");
        Iterator<String> attributeNames = attributes.keySet().iterator();
        while (attributeNames.hasNext()) {
            String attributeName = attributeNames.next();

            AttributeFieldValue attribute = attributes.get(attributeName);
            long startTime = System.currentTimeMillis();
            buf.append(
                    attribute.getDisplayName() + ":   "
                            + attribute.getBriefValue()).append(newline);
            if (getTimeAttributeQueries()) {
                buf.append("TIME INFO: " + attributeName + " took "
                        + (System.currentTimeMillis() - startTime) / 1000F
                        + " seconds to retrieve.\n");
            }
        }
    }

    // //////////////////////////////////////////////////////////////////
    // static
    // //////////////////////////////////////////////////////////////////

    /**
     * substitute a value for a macro in a text string. The macro is delimited
     * by $$
     * 
     * @param text
     *            the text which contains the macro
     * @param macroName
     *            the name of the macro, without the delimiter
     * @param value
     *            the value to substitute in
     */
    public static String instantiateText(String text, String macroName,
            String value) {
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

    public static void checkInstantiatedText(String instantiatedText)
            throws WdkModelException {
        if (instantiatedText.matches("\\$\\$\\w+\\$\\$"))
            throw new WdkModelException("'" + instantiatedText
                    + "' contains unrecognized macro");
    }

}
