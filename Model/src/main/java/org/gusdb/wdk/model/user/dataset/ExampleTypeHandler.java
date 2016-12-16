package org.gusdb.wdk.model.user.dataset;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

public class ExampleTypeHandler extends UserDatasetTypeHandler {

  @Override
  public UserDatasetCompatibility getCompatibility(UserDataset userDataset, DataSource appDbDataSource) {
    return new UserDatasetCompatibility(true, "");
  }

  @Override
  public UserDatasetType getUserDatasetType() {
    return UserDatasetTypeFactory.getUserDatasetType("example", "1.0");
  }

  @Override
  public String[] getUninstallInAppDbCommand(Integer userDatasetId, String projectId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String[] getInstallInAppDbCommand(UserDataset userDataset, Map<String, Path> fileNameToTempFileMap, String projectid) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getInstallInAppDbFileNames(UserDataset userDataset) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String[] getRelevantQuestionNames() {
    String[] empty = {};
    return empty;
  }




}
