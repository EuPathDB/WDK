package org.gusdb.wdk.service.service.user;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.gusdb.fgputil.validation.ValidationLevel;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.user.Step;
import org.gusdb.wdk.model.user.User;
import org.gusdb.wdk.service.UserBundle;
import org.gusdb.wdk.service.service.AbstractWdkService;

/*
 * TODO: rename this AbstractUserService when this branch is merged into trunk
 */
@Path(UserService.USER_PATH)
public abstract class UserService extends AbstractWdkService {

  private static final String NOT_LOGGED_IN = "You must log in to use this functionality.";

  // subclasses must read the following path param to gain access to requested user
  protected static final String USER_ID_PATH_PARAM = "id";

  protected static final String USER_RESOURCE = "User ID ";
  protected static final String STEP_RESOURCE = "Step ID ";

  protected static final String USER_PATH = "/users/{"+USER_ID_PATH_PARAM+"}";

  protected enum Access { PUBLIC, PRIVATE, ADMIN; }

  private final String _userIdStr;

  protected UserService(@PathParam(USER_ID_PATH_PARAM) String userIdStr) {
    _userIdStr = userIdStr;
  }

  protected boolean isUserIdSpecialString(String specialString) {
    return _userIdStr.equals(specialString);
  }

  /**
   * Ensures the target user exists and that the session user has the
   * permissions requested.  If either condition is not true, the appropriate
   * exception (corresponding to 404 and 403 respectively) is thrown.
   * 
   * @param userIdStr id string of the target user
   * @param requestedAccess the access requested by the caller
   * @return a userBundle representing the target user and his relationship to the session user
   * @throws WdkModelException if error occurs creating user bundle (probably a DB problem)
   */
  protected UserBundle getUserBundle(Access requestedAccess) throws WdkModelException {
    UserBundle userBundle = parseTargetUserId(_userIdStr);
    if (!userBundle.isValidUserId()) {
      throw new NotFoundException(AbstractWdkService.formatNotFound(USER_RESOURCE + userBundle.getTargetUserIdString()));
    }
    if ((!userBundle.isSessionUser() && Access.PRIVATE.equals(requestedAccess)) ||
        (!userBundle.isAdminSession() && Access.ADMIN.equals(requestedAccess))) {
      throw new ForbiddenException(AbstractWdkService.PERMISSION_DENIED);
    }
    return userBundle;
  }

  protected User getPrivateRegisteredUser() throws WdkModelException {
    UserBundle userBundle = getUserBundle(Access.PRIVATE);
    User user = userBundle.getTargetUser();
    if (user.isGuest()) {
      throw new ForbiddenException(NOT_LOGGED_IN);
    }
    return user;
  }

  protected Step getStepForCurrentUser(long stepId, ValidationLevel level) throws WdkModelException {
    return getWdkModel()
        .getStepFactory()
        .getStepByIdAndUserId(
            stepId,
            getUserBundle(Access.PRIVATE).getSessionUser().getUserId(),
            level)
        .orElseThrow(
            () -> new NotFoundException(formatNotFound(STEP_RESOURCE + stepId)));
  }
}
