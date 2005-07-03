package org.gusdb.wdk.model;

public class TextAttributeField implements FieldI {

    String name;
    String displayName;
    String text;
    String help;
    Boolean isInternal = new Boolean(false);
    String truncateToRef;
    Integer truncateTo;


    public void setName(String name) {
	this.name = name;
    }
    
    public String getName() {
	return name;
    }
   
    public void setHelp(String help) {
	this.help = help;
    }
    
    public String getHelp() {
	return help;
    }
   
    public void setIsInternal(Boolean isInternal) {
	this.isInternal = isInternal;
    }
    
    public Boolean getIsInternal() {
	return isInternal;
    }
   
    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }
    
    public String getDisplayName() {
	return (displayName != null)? displayName : name;
    }
    
     public void setText(String text) {
	this.text = text;
    }

    public String getText() {
	return text;
    }

    public String getType() {
	return null;
    }

    public String toString() {
	return getDisplayName();
    }
    
    public void setTruncateToRef(String truncateToRef){
	this.truncateToRef = truncateToRef;
	
	this.truncateTo = new Integer(truncateToRef);
    }

    public Integer getTruncate(){
	return truncateTo;
    }

    
}
