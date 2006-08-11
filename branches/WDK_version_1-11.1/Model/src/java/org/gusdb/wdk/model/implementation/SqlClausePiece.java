package org.gusdb.wdk.model.implementation;

import org.gusdb.wdk.model.RecordClass;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.ResultFactory;

import java.util.regex.Pattern;

public class SqlClausePiece {

    String origSql;
    String joinTableName;
    //locations relative to original sql
    int start;
    int end;

    /**
     * A piece of sql that belongs to an sql clause and that has no kid
     * clauses.  If the sql clause has no kids, then the piece is its
     * full length (exluding bounding parens)
     *
     * @param start index of the start of this piece (non-paren)
     * @param end index of end of this piece (non-paren), ie, index of last char
     */
    public SqlClausePiece(String origSql, int start, int end, String joinTableName) {
	this.origSql = origSql;
	this.start = start;
	this.end = end;
	this.joinTableName = joinTableName;
    }

    String getFinalPieceSql(boolean needsSelectFix, 
			    boolean needsFromFix,
			    boolean needsWhereFix,
			    boolean needsGroupByFix,
			    int pageStartIndex,
			    int pageEndIndex) throws WdkModelException {

	String finalSql = origSql.substring(start, end+1);
	if (needsSelectFix) finalSql = addJoinTableIndexToSelect(finalSql);
	if (needsFromFix) finalSql = addJoinTableToFrom(finalSql);
	if (needsWhereFix) finalSql = addConstraintsToWhere(finalSql,
							    pageStartIndex,
							    pageEndIndex);
	if (needsGroupByFix) finalSql = addJoinTableIndexToGroupBy(finalSql);
	return finalSql;
    }

    String getSql() {
	return origSql.substring(start, end+1);
    }

    String addJoinTableIndexToSelect(String sql) {
	String regex = "\\b(select)\\b";
	int flag = Pattern.CASE_INSENSITIVE;
	String replace = "$1 " + ResultFactory.RESULT_TABLE_I + "," ;

	return Pattern.compile(regex,flag).matcher(sql).replaceAll(replace);
    }

    String addJoinTableToFrom(String sql) {
	String regex = "\\b(from)\\b";
	int flag = Pattern.CASE_INSENSITIVE;
	String replace = "$1 " + joinTableName + ",";

	return Pattern.compile(regex,flag).matcher(sql).replaceAll(replace);
    }

    String addConstraintsToWhere(String sql, int pageStartIndex, 
				 int pageEndIndex) throws WdkModelException {

	String macro = RecordClass.PRIMARY_KEY_MACRO; // shorter var. name

	// add AND clauses for page constraints
	String newline = System.getProperty("line.separator");
	String resultTableIndex = ResultFactory.RESULT_TABLE_I;

	String andClause = 
	    newline + "AND " + resultTableIndex + " >= " + pageStartIndex +
	    newline + "AND " + resultTableIndex + " <= " + pageEndIndex;
	
	String newSql = sql;
	int flag = Pattern.DOTALL;
	// case 1:  "blah = $$primaryKey$$"
	if (Pattern.compile(".*=\\s*" + macro + ".*", flag).matcher(newSql).matches()) {
	    newSql = newSql.replaceAll("(" + macro + ")", 
				       "$1" + andClause );	    
	    
	// case 2:  "$$primaryKey$$ = blah"
	} else if (Pattern.compile(".*"+macro+"\\s*=.*", flag).matcher(newSql).matches()) {
	    newSql = newSql.replaceAll("(" + macro + "\\s*=\\s*\\S+)", 
				       "$1" + andClause );	    
	    
	} else {
	    throw new WdkModelException("Invalid use of primary key macro in:"
					+ newline + sql);
	}

	return newSql;
    }

    String addJoinTableIndexToGroupBy(String sql) {
	String regex = "\\b(group\\s+by)\\b";
	int flag = Pattern.CASE_INSENSITIVE;
	String replace = "$1 " + ResultFactory.RESULT_TABLE_I + "," ;

	return Pattern.compile(regex,flag).matcher(sql).replaceAll(replace);
    }

    boolean containsSelect() {	
	return contains(".*\\bselect\\b.*", Pattern.CASE_INSENSITIVE);
    }

    boolean containsFrom() {
	return contains(".*\\bfrom\\b.*", Pattern.CASE_INSENSITIVE);
    }

    boolean containsPrimaryKey() {
	String regex = ".*" + RecordClass.PRIMARY_KEY_MACRO + ".*";
	return contains(regex, 0);
    }

    boolean containsGroupBy() {
	return contains(".*\\bgroup\\s+by\\b.*", Pattern.CASE_INSENSITIVE);
    }

    //////////////////////////////////////////////////////////////////
    // private methods
    //////////////////////////////////////////////////////////////////

    private boolean contains(String regex, int caseFlag) {
	int flag = Pattern.DOTALL | caseFlag;
	return Pattern.compile(regex, flag).matcher(origSql.substring(start, end+1)).matches();    }
}

