/**
 * 
 */
package org.gusdb.wdk.model.dbms;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.gusdb.wdk.model.Utilities;
import org.gusdb.wdk.model.WdkModel;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.query.Query;
import org.gusdb.wdk.model.query.QueryInstance;

/**
 * @author Jerric Gao
 * 
 */
public class ResultFactory {

  private Logger logger = Logger.getLogger(ResultFactory.class);

  private DBPlatform platform;
  private CacheFactory cacheFactory;
  private WdkModel wdkModel;

  public ResultFactory(WdkModel wdkModel) throws SQLException {
    this.platform = wdkModel.getQueryPlatform();
    this.cacheFactory = new CacheFactory(wdkModel, platform);
    this.wdkModel = platform.getWdkModel();
  }

  public CacheFactory getCacheFactory() {
    return cacheFactory;
  }

  public String getCachedSql(QueryInstance queryInstance)
      throws WdkModelException {
    // get query info
    Query query = queryInstance.getQuery();
    QueryInfo queryInfo = cacheFactory.getQueryInfo(query);

    int instanceId;
    try {
      if (queryInfo.isExist()) { // cache table exists
        instanceId = getInstanceId(queryInfo, queryInstance);
        if (instanceId == 0) { // instance doesn't exist
          logger.debug("creating cache instance and cache...");
          // create cache
          instanceId = createCache(queryInfo, queryInstance);
          createCacheInstance(queryInfo, queryInstance, instanceId);
        }
      } else { // cache table doesn't exist
        logger.debug("creating cache query, instance and cache...");
        // get a new instance id, and created cache
        instanceId = createCache(queryInfo, queryInstance);
        // create query info
        cacheFactory.createQueryInfo(queryInfo);
        createCacheInstance(queryInfo, queryInstance, instanceId);
      }
    } catch (SQLException ex) {
      throw new WdkModelException(ex);
    }
    queryInstance.setInstanceId(instanceId);

    // get the cached sql
    StringBuffer sql = new StringBuffer("SELECT * ");
    sql.append(" FROM ").append(queryInfo.getCacheTable());
    sql.append(" WHERE ").append(CacheFactory.COLUMN_INSTANCE_ID);
    sql.append(" = ").append(instanceId);
    return sql.toString();
  }

  public ResultList getCachedResults(QueryInstance queryInstance)
      throws WdkModelException {
    String sql = getCachedSql(queryInstance);
    // get the resultList
    try {
      DataSource dataSource = platform.getDataSource();
      ResultSet resultSet = SqlUtils.executeQuery(wdkModel, dataSource,
          sql.toString(), queryInstance.getQuery().getFullName()
              + "__select-cache");
      return new SqlResultList(resultSet);
    } catch (SQLException e) {
      throw new WdkModelException("Unable to retrieve cached results.", e);
    }
  }

  private int getInstanceId(QueryInfo queryInfo, QueryInstance instance)
      throws WdkModelException {
    // get the query instance id; null if not exist
    String checksum = instance.getChecksum();
    StringBuffer sql = new StringBuffer("SELECT ");
    sql.append(CacheFactory.COLUMN_INSTANCE_ID).append(", ");
    sql.append(CacheFactory.COLUMN_RESULT_MESSAGE);
    sql.append(" FROM ").append(CacheFactory.TABLE_INSTANCE);
    sql.append(" WHERE ").append(CacheFactory.COLUMN_QUERY_ID);
    sql.append(" = ").append(queryInfo.getQueryId());
    sql.append(" AND " + CacheFactory.COLUMN_INSTANCE_CHECKSUM);
    sql.append(" = '").append(checksum).append("'");
    sql.append(" ORDER BY " + CacheFactory.COLUMN_INSTANCE_ID);

    DataSource dataSource = platform.getDataSource();
    ResultSet resultSet = null;
    try {
      resultSet = SqlUtils.executeQuery(wdkModel, dataSource, sql.toString(),
          "wdk-check-instance-exist");

      int instanceId = 0;
      if (resultSet.next()) {
        instanceId = resultSet.getInt(CacheFactory.COLUMN_INSTANCE_ID);
        String message = platform.getClobData(resultSet,
            CacheFactory.COLUMN_RESULT_MESSAGE);
        instance.setResultMessage(message);
      }
      return instanceId;
    } catch (SQLException e) {
      throw new WdkModelException("Unable to check instance ID.", e);
    } finally {
      SqlUtils.closeResultSetAndStatement(resultSet);
    }
  }

  private int createCache(QueryInfo queryInfo, QueryInstance instance)
      throws WdkModelException, SQLException {
    int instanceId = platform.getNextId(null, CacheFactory.TABLE_INSTANCE);

    // check whether need to create the cache;
    String cacheTable = queryInfo.getCacheTable();
    if (!platform.checkTableExists(null, cacheTable)) {
      // create the cache using the result of the first query
      instance.createCache(cacheTable, instanceId);
      // disable the stats on the new cache table
      String schema = platform.getWdkModel().getModelConfig().getAppDB().getLogin();
      platform.disableStatistics(schema, cacheTable);
      createCacheTableIndex(queryInfo.getCacheTable(), instance.getQuery());
    } else {// insert result into existing cache table
      instance.insertToCache(cacheTable, instanceId);
    }

    return instanceId;
  }

  private void createCacheTableIndex(String cacheTable, Query query)
      throws WdkModelException {
    String[] indexColumns = query.getIndexColumns();

    // resize the index columns first
    resizeIndexColumns(cacheTable, indexColumns);

    // create index on query instance id
    StringBuffer sqlId = new StringBuffer("CREATE INDEX ");
    sqlId.append(cacheTable).append("_idx01 ON ").append(cacheTable);
    sqlId.append(" (").append(CacheFactory.COLUMN_INSTANCE_ID).append(")");

    // create index on other columns
    StringBuffer sqlOther = null;
    if (indexColumns != null) {
      sqlOther = new StringBuffer("CREATE INDEX ");
      sqlOther.append(cacheTable).append("_idx02 ON ").append(cacheTable);
      sqlOther.append(" (");
      boolean firstColumn = true;
      for (String column : indexColumns) {
        if (firstColumn)
          firstColumn = false;
        else
          sqlOther.append(", ");
        sqlOther.append(column);
      }
      sqlOther.append(")");
    }

    DataSource dataSource = platform.getDataSource();
    SqlUtils.executeUpdate(wdkModel, dataSource, sqlId.toString(),
        query.getFullName() + "__create-cache-index01");

    if (indexColumns != null) {
      SqlUtils.executeUpdate(wdkModel, dataSource, sqlOther.toString(),
          query.getFullName() + "__create-cache-index02");
    }
  }

  /**
   * Resize the index columns to the max size defined in the model config file.
   * Please note, only varchar columns are resized.
   * 
   * @param connection
   * @param cacheTable
   * @param indexColumns
   * @throws SQLException
   */
  private void resizeIndexColumns(String cacheTable, String[] indexColumns)
      throws WdkModelException {
    if (indexColumns == null || indexColumns.length == 0)
      return;

    // get the type of the columns
    DataSource dataSource = platform.getDataSource();
    Map<String, Integer> columnSizes = new LinkedHashMap<>();
    ResultSet resultSet = null;
    try {
      resultSet = SqlUtils.executeQuery(wdkModel, dataSource, "SELECT * FROM "
          + cacheTable, cacheTable + "__ge-cache-metadata");
      ResultSetMetaData metaData = resultSet.getMetaData();
      for (int i = 1; i <= metaData.getColumnCount(); i++) {
        String column = metaData.getColumnName(i).toLowerCase();
        String type = metaData.getColumnTypeName(i).toLowerCase();
        if (type.contains("char"))
          columnSizes.put(column, metaData.getColumnDisplaySize(i));
      }
      metaData = null;
    } catch (SQLException ex) {
      throw new WdkModelException(ex);
    } finally {
      SqlUtils.closeResultSetAndStatement(resultSet);
    }

    try {
      // resize columns
      int maxSize = wdkModel.getModelConfig().getAppDB().getMaxPkColumnWidth();
      for (String column : indexColumns) {
        column = column.toLowerCase();
        if (!columnSizes.containsKey(column))
          continue;
        // throw new WdkModelException("The required index column '" + column
        // + "' doesn't exist in the cache table: " + cacheTable
        // + ". Please look up the query in 'Query' table that generates " +
        // "this cache table, and make sure it returns the index column.");

        String sql = platform.getResizeColumnSql(cacheTable, column, maxSize);
        SqlUtils.executeUpdate(wdkModel, dataSource, sql, cacheTable
            + "__change-column-size");
      }
    } catch (WdkModelException ex) {
      throw new WdkModelException("Failed to alter the sizes of index columns"
          + " '" + Utilities.fromArray(indexColumns) + "' on cache table "
          + cacheTable + ". Please look up the query in 'Query' table that "
          + "creates this cache, and make sure the maxPkColumnWidth property "
          + "defined in the model-config.xml is the same or bigger than the "
          + "size of primary key columns from that query.", ex);
    }
  }

  private void createCacheInstance(QueryInfo queryInfo, QueryInstance instance,
      int instanceId) throws WdkModelException {
    StringBuffer sql = new StringBuffer("INSERT INTO ");
    sql.append(CacheFactory.TABLE_INSTANCE).append(" (");
    sql.append(CacheFactory.COLUMN_INSTANCE_ID).append(", ");
    sql.append(CacheFactory.COLUMN_QUERY_ID).append(", ");
    sql.append(CacheFactory.COLUMN_INSTANCE_CHECKSUM).append(", ");
    sql.append(CacheFactory.COLUMN_RESULT_MESSAGE);
    sql.append(") VALUES (?, ?, ?, ?)");

    PreparedStatement ps = null;
    try {
      DataSource dataSource = platform.getDataSource();
      ps = SqlUtils.getPreparedStatement(dataSource, sql.toString());
      ps.setInt(1, instanceId);
      ps.setInt(2, queryInfo.getQueryId());
      ps.setString(3, instance.getChecksum());
      platform.setClobData(ps, 4, instance.getResultMessage(), false);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new WdkModelException("Unable to add cache instance.", e);
    } finally {
      // close the statement manually, since we cannot close the
      // connection; it's not committed yet.
      SqlUtils.closeStatement(ps);
    }
  }
}
