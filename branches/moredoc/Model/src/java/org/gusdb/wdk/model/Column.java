package org.gusdb.wdk.model;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class Column implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -1895749085919850028L;
    
    // never used locally
    // private static final Logger logger =
    // WdkLogManager.getLogger("org.gusdb.wdk.model.Column");
    private static Logger logger = Logger.getLogger( Column.class );
    
    private String name;
    private Query query;
    private String dataTypeName;
    private int width; // for wsColumns (width of datatype)
    
    /**
     * The name is used by WSF service.
     */
    private String wsName;
    
    private boolean dynamicColumn = false;
    
    private String sortingTable;
    private String sortingColumn;
    private boolean lowerCase = false;
    
    public Column( ) {}
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setSpecialType( String specialType ) {
        this.dataTypeName = specialType;
    }
    
    public String getSpecialType() {
        return dataTypeName;
    }
    
    public void setQuery( Query query ) {
        // TEST
        // if (name != null && name.equalsIgnoreCase("score"))
        // logger.debug("Columns Query is set to: " + query.getFullName());
        
        this.query = query;
    }
    
    public void setWidth( int width ) {
        this.width = width;
    }
    
    public Query getQuery() {
        return query;
    }
    
    public int getWidth() {
        return width;
    }
    
    /**
     * @return Returns the wsName.
     */
    public String getWsName() {
        return this.wsName;
    }
    
    /**
     * @param wsName
     *            The wsName to set.
     */
    public void setWsName( String wsName ) {
        this.wsName = wsName;
    }
    
    /**
     * @return the dynamicColumn
     */
    public boolean isDynamicColumn() {
        return dynamicColumn;
    }
    
    /**
     * @param dynamicColumn
     *            the dynamicColumn to set
     */
    void setDynamicColumn( boolean dynamicColumn ) {
        this.dynamicColumn = dynamicColumn;
    }
    
    /**
     * @return the sortingTable
     */
    public String getSortingTable() {
        return sortingTable;
    }
    
    /**
     * @param sortingTable
     *            the sortingTable to set
     */
    public void setSortingTable( String sortingTable ) {
        this.sortingTable = sortingTable;
    }
    
    /**
     * @return the sortingColumn
     */
    public String getSortingColumn() {
        if ( sortingColumn != null ) return sortingColumn;
        else return name;
    }
    
    /**
     * @param sortingColumn
     *            the sortingColumn to set
     */
    public void setSortingColumn( String sortingColumn ) {
        this.sortingColumn = sortingColumn;
    }
    
    /**
     * @return the lowerCase
     */
    public boolean isLowerCase() {
        return lowerCase;
    }
    
    /**
     * @param lowerCase
     *            the lowerCase to set
     */
    public void setLowerCase( boolean lowerCase ) {
        this.lowerCase = lowerCase;
    }
    
    public String toString() {
        String newline = System.getProperty( "line.separator" );
        String classnm = this.getClass().getName();
        StringBuffer buf = new StringBuffer( classnm + ": name='" + name + "'"
                + newline + "  dataTypeName='" + dataTypeName + "'" + newline );
        
        return buf.toString();
    }
    
    public Column clone() {
        Column column = new Column();
        column.dataTypeName = this.dataTypeName;
        column.name = this.name;
        column.query = this.query;
        column.width = this.width;
        column.wsName = this.wsName;
        return column;
    }
}
