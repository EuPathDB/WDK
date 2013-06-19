/**
 * 
 */
package org.gusdb.wdk.model.dbms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gusdb.wdk.model.WdkModelException;

/**
 * @author Jerric Gao
 */
public class PostgreSQL extends DBPlatform {

  public PostgreSQL() {
    super();
  }

  @Override
  protected String getDriverClassName() {
    return "org.postgresql.Driver";
  }

  @Override
  protected String getValidationQuery() {
    return "SELECT 'ok'";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#createSequence(java.lang.String,
   * int, int)
   */
  @Override
  public void createSequence(String sequence, int start, int increment)
      throws WdkModelException {
    StringBuffer sql = new StringBuffer("CREATE SEQUENCE ");
    sql.append(sequence);
    sql.append(" START ");
    sql.append(start);
    sql.append(" INCREMENT ");
    sql.append(increment);
    SqlUtils.executeUpdate(wdkModel, dataSource, sql.toString(),
        "wdk-create-sequence");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getBooleanDataType()
   */
  @Override
  public String getBooleanDataType() {
    return "boolean";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getClobData(java.sql.ResultSet,
   * java.lang.String)
   */
  @Override
  public String getClobData(ResultSet rs, String columnName)
      throws SQLException {
    return rs.getString(columnName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getClobDataType()
   */
  @Override
  public String getClobDataType() {
    return "text";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getMinusOperator()
   */
  public String getMinusOperator() {
    return "EXCEPT";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getNextId(java.lang.String,
   * java.lang.String)
   */
  public int getNextId(String schema, String table) throws WdkModelException {
    schema = normalizeSchema(schema);

    StringBuffer sql = new StringBuffer("SELECT nextval('");
    sql.append(schema).append(table).append(ID_SEQUENCE_SUFFIX);
    sql.append("')");
    long id = (Long) SqlUtils.executeScalar(wdkModel, dataSource,
        sql.toString(), "wdk-select-next-id");
    return (int) id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getNextId(java.lang.String,
   * java.lang.String)
   */
  public String getNextIdSqlExpression(String schema, String table) {
    schema = normalizeSchema(schema);

    StringBuffer sql = new StringBuffer("nextval('");
    sql.append(schema).append(table).append(ID_SEQUENCE_SUFFIX);
    sql.append("')");
    return sql.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getNumberDataType(int)
   */
  public String getNumberDataType(int size) {
    return "NUMERIC(" + size + ")";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getPagedSql(java.lang.String, int,
   * int)
   */
  public String getPagedSql(String sql, int startIndex, int endIndex) {
    StringBuffer buffer = new StringBuffer("SELECT f.* FROM ");
    buffer.append("(").append(sql).append(") f ");
    buffer.append(" LIMIT ").append(endIndex - startIndex + 1);
    buffer.append(" OFFSET ").append(startIndex - 1);
    return buffer.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getStringDataType(int)
   */
  public String getStringDataType(int size) {
    return "VARCHAR(" + size + ")";
  }

  /**
   * Check the existence of a table. If the schema is null or empty, the schema
   * will will be ignored, and will look up the table in the public schema.
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#isTableExisted(java.lang.String)
   */
  @Override
  public boolean checkTableExists(String schema, String tableName)
      throws WdkModelException {
    if (schema == null || schema.trim().length() == 0)
      schema = "";
    if (schema.endsWith("."))
      schema = schema.substring(0, schema.length() - 1);
    tableName = tableName.toLowerCase();

    StringBuffer sql = new StringBuffer("SELECT count(*) FROM pg_tables ");
    sql.append("WHERE tablename = '").append(tableName).append("'");
    if (!schema.equals(""))
      sql.append(" AND schemaname = '").append(schema).append("'");
    long count = (Long) SqlUtils.executeScalar(wdkModel, dataSource,
        sql.toString(), "wdk-check-table-exist");
    return (count > 0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getDateDataType()
   */
  @Override
  public String getDateDataType() {
    return "TIMESTAMP";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getFloatDataType(int)
   */
  @Override
  public String getFloatDataType(int size) {
    return "FLOAT(" + size + ")";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#convertBoolean(boolean)
   */
  @Override
  public String convertBoolean(boolean value) {
    return value ? "TRUE" : "FALSE";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#dropTable(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void dropTable(String schema, String table, boolean purge)
      throws WdkModelException {
    String sql = "DROP TABLE ";
    if (schema != null)
      sql = schema;
    sql += table;
    // ignore purge option
    SqlUtils.executeUpdate(wdkModel, dataSource, sql, "wdk-drop-table" + table);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.gusdb.wdk.model.dbms.DBPlatform#disableStatistics(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void disableStatistics(String schema, String tableName) {
    // do nothing in PSQL.
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.gusdb.wdk.model.dbms.DBPlatform#getTables(java.lang.String,
   * java.lang.String)
   */
  @Override
  public String[] queryTableNames(String schema, String pattern)
      throws WdkModelException {
    String sql = "SELECT tablename FROM pg_tables WHERE schemaname = '"
        + schema + "' AND tablename LIKE '" + pattern + "'";
    ResultSet resultSet = null;
    try {
      resultSet = SqlUtils.executeQuery(wdkModel, dataSource, sql,
          "wdk-postgres-select-table-names");
      List<String> tables = new ArrayList<String>();
      while (resultSet.next()) {
        tables.add(resultSet.getString("tablename"));
      }
      String[] array = new String[tables.size()];
      tables.toArray(array);
      return array;
    } catch (SQLException e) {
      throw new WdkModelException("Unable to query table names for schema [ "
          + schema + " ].", e);
    } finally {
      SqlUtils.closeResultSetAndStatement(resultSet);
    }
  }

  @Override
  public String getDummyTable() {
    return " ";
  }

  @Override
  public String getResizeColumnSql(String tableName, String column, int size) {
    return "ALTER TABLE " + tableName + " ALTER COLUMN " + column
        + " TYPE varchar(" + size + ")";
  }
}
