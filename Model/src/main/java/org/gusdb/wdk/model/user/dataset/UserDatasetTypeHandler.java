package org.gusdb.wdk.model.user.dataset;

import javax.sql.DataSource;

/**
 * A handler for a particular type of dataset.  These are plugged in to the wdk.
 * If a particular type is not plugged in, then datasets of that type are not
 * compatible with this wdk application, for that reason.
 * @author steve
 *
 */
public interface UserDatasetTypeHandler {
    
  /**
   * Check if a dataset is compatible with this application, based on its data dependencies.
   * @param userDataset
   * @return
   */
  UserDatasetCompatibility getCompatibility(UserDataset userDataset, DataSource appDbDataSource);
  
  /**
   * The user dataset type this handler handles.
   * @return
   */
  UserDatasetType getUserDatasetType();
  
  void installInAppDb(UserDataset userDataset, DataSource appDbDataSource, String userDatasetSchemaName);
  
  void uninstallInAppDb(Integer userDatasetId, DataSource appDbDataSource, String userDatasetSchemaName);

}
