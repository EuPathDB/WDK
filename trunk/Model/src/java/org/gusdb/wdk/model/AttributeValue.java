package org.gusdb.wdk.model;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.JSONException;

public abstract class AttributeValue {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(AttributeValue.class.getName());

    protected AttributeField field;
    protected Object value;

    public abstract Object getValue() throws WdkModelException, WdkUserException;

    public AttributeValue(AttributeField field) {
        this.field = field;
    }

    public AttributeField getAttributeField() {
        return this.field;
    }

    /**
     * @return
     * @see org.gusdb.wdk.model.Field#getDisplayName()
     */
    public String getDisplayName() {
        return field.getDisplayName();
    }

    /**
     * @return
     * @see org.gusdb.wdk.model.Field#getName()
     */
    public String getName() {
        return field.getName();
    }

    public String getBriefDisplay() throws WdkModelException,
            NoSuchAlgorithmException, SQLException, JSONException,
            WdkUserException {
        String display = getDisplay();
        int truncateTo = field.getTruncateTo();
        if (truncateTo == 0) truncateTo = Utilities.TRUNCATE_DEFAULT;
        if (display.length() > truncateTo)
            display = display.substring(0, truncateTo) + "...";
        return display;
    }

    public String getDisplay() throws WdkModelException,
            NoSuchAlgorithmException, WdkUserException, SQLException,
            JSONException {
        Object value = getValue();
        return (value != null) ? value.toString() : "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        try {
            Object value = getValue();
            return (value == null) ? null : value.toString();
        } catch (WdkModelException ex) {
            throw new RuntimeException(ex);
        } catch (WdkUserException ex) {
            throw new RuntimeException(ex);
        }
    }
}
