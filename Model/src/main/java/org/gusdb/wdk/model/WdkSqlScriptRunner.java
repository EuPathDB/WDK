package org.gusdb.wdk.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;

import org.gusdb.fgputil.IoUtil;
import org.gusdb.fgputil.db.SqlScriptRunner;
import org.gusdb.fgputil.db.SqlUtils;
import org.gusdb.fgputil.db.pool.DatabaseInstance;
import org.gusdb.fgputil.runtime.GusHome;
import org.gusdb.wdk.model.config.ModelConfig;
import org.gusdb.wdk.model.config.ModelConfigDB;
import org.gusdb.wdk.model.config.ModelConfigParser;

public class WdkSqlScriptRunner {

  private static enum DbType { APP, USER }

  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("USAGE: fgpJava " + WdkSqlScriptRunner.class.getName() + " <project_id> [APP|USER] <sql_file>");
      System.exit(1);
    }
    BufferedReader sqlReader = null;
    DatabaseInstance db = null;
    Connection conn = null;
    try {
      String projectId = args[0];
      DbType whichDb = DbType.valueOf(args[1]);
      sqlReader = new BufferedReader(new FileReader(args[2]));
      String gusHome = GusHome.getGusHome();

      ModelConfigParser parser = new ModelConfigParser(gusHome);
      ModelConfig modelConf = parser.parseConfig(projectId);
      ModelConfigDB dbConfig = (whichDb.equals(DbType.APP) ?
          modelConf.getAppDB() : modelConf.getUserDB());

      db = new DatabaseInstance(dbConfig);
      conn = db.getDataSource().getConnection();

      SqlScriptRunner runner = new SqlScriptRunner(db.getDataSource().getConnection(), true, false);
      runner.setLogWriter(new PrintWriter(System.err));
      runner.runScript(sqlReader);
    }
    catch (Exception e) {
      throw new WdkRuntimeException(e);
    }
    finally {
      SqlUtils.closeQuietly(conn, db);
      IoUtil.closeQuietly(sqlReader);
    }
  }
}
