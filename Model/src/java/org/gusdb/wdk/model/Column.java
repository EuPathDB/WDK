package org.gusdb.wdk.model;

import org.apache.log4j.Logger;





public class Column {

    // never used locally
    //private static final Logger logger = WdkLogManager.getLogger("org.gusdb.wdk.model.Column");
    private static Logger logger = Logger.getLogger(Column.class);
    
    private String name;
    private Query query;
    private String dataTypeName;
    private int width;  // for wsColumns (width of datatype)

    public Column() {} 

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSpecialType(String specialType) {
        this.dataTypeName = specialType;
    }

    public String getSpecialType() {
        return dataTypeName;
    }

    public void setQuery(Query query) {
        // TEST
//        if (name != null && name.equalsIgnoreCase("score"))
//            logger.debug("Columns Query is set to: " + query.getFullName());

        this.query = query;
    }

    public void setWidth(int width) {
	this.width = width;
    }

    public Query getQuery() {
        return query;
    }

    public int getWidth() {
	return width;
    }

    public String toString() {
       String newline = System.getProperty( "line.separator" );
       String classnm = this.getClass().getName();
       StringBuffer buf = 
	   new StringBuffer(classnm + ": name='" + name + "'" + newline +
			    "  dataTypeName='" + dataTypeName + "'" + newline);

       return buf.toString();
    }
    
    public Column clone() {
        Column column = new Column();
        column.dataTypeName = this.dataTypeName;
        column.name = this.name;
        column.query = this.query;
        column.width = this.width;
        return column;
    }
}

