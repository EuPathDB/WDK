package org.gusdb.wdk.model.user.dataset;


/**
 * A simple data container with info needed to describe sharing of a user dataset
 * with another user.
 * @author steve
 *
 */
public interface UserDatasetShare {
  /**
   * The user the dataset is shared with
   * @return
   */
  Integer getUserId();
  
  /**
   * The time it was shared
   * @return
   */
  Long getTimeShared();
}
