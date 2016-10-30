package org.gusdb.wdk.model.user.dataset.event;

import java.util.Set;

import org.gusdb.wdk.model.user.dataset.UserDatasetType;

public class UserDatasetUninstallEvent extends UserDatasetEvent {
  UserDatasetUninstallEvent(Integer eventId, Set<String> projectsFilter, Integer userDatasetId, UserDatasetType userDatasetType) {
    super(eventId, projectsFilter, userDatasetId, userDatasetType);
  }
}
