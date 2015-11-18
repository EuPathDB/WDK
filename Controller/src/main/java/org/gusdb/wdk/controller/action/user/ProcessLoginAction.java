package org.gusdb.wdk.controller.action.user;

import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;
import org.gusdb.wdk.controller.AuthenticationService;
import org.gusdb.wdk.controller.CConstants;
import org.gusdb.wdk.controller.LoginCookieFactory;
import org.gusdb.wdk.controller.OAuthClient;
import org.gusdb.wdk.controller.actionutil.ActionResult;
import org.gusdb.wdk.controller.actionutil.ParamDef;
import org.gusdb.wdk.controller.actionutil.ParamDef.Count;
import org.gusdb.wdk.controller.actionutil.ParamDef.DataType;
import org.gusdb.wdk.controller.actionutil.ParamDef.Required;
import org.gusdb.wdk.controller.actionutil.ParamDefMapBuilder;
import org.gusdb.wdk.controller.actionutil.ParamGroup;
import org.gusdb.wdk.controller.actionutil.RequestData;
import org.gusdb.wdk.controller.actionutil.WdkAction;
import org.gusdb.wdk.model.WdkModelException;
import org.gusdb.wdk.model.WdkUserException;
import org.gusdb.wdk.model.config.ModelConfig.AuthenticationMethod;
import org.gusdb.wdk.model.jspwrap.UserBean;
import org.gusdb.wdk.model.jspwrap.UserFactoryBean;
import org.gusdb.wdk.model.jspwrap.WdkModelBean;
import org.gusdb.wdk.session.OAuthUtil;

/**
 * @author: Jerric
 * @created: May 26, 2006
 * @modified by: Ryan
 * @modified at: June 30, 2012
 */
public class ProcessLoginAction extends WdkAction {

  private static final Logger LOG = Logger.getLogger(ProcessLoginAction.class.getName());
  
  private static final String REMEMBER_PARAM_KEY = "remember";
  
  private static final Map<String, ParamDef> PARAM_DEFS = new ParamDefMapBuilder()
      .addParam(CConstants.WDK_REDIRECT_URL_KEY,
          new ParamDef(Required.OPTIONAL, Count.SINGULAR, DataType.STRING, new String[]{ "/" }))
      .addParam(CConstants.WDK_EMAIL_KEY, new ParamDef(Required.OPTIONAL))
      .addParam(CConstants.WDK_PASSWORD_KEY, new ParamDef(Required.OPTIONAL))
      .addParam(CConstants.WDK_OPENID_KEY, new ParamDef(Required.OPTIONAL))
      .addParam(REMEMBER_PARAM_KEY, new ParamDef(Required.OPTIONAL)).toMap();
  
  @Override
  protected boolean shouldValidateParams() {
    // only validate params if using traditional form
    return (getWdkModel().getModel().getModelConfig()
        .getAuthenticationMethodEnum().equals(AuthenticationMethod.USER_DB));
  }

  @Override
  protected Map<String, ParamDef> getParamDefs() {
    return PARAM_DEFS;
  }

  /**
   * Original logic for resulting page:
   *   check for referrer on request and on header (but not session)
   *   check for origin url on request
   *   redirect = true
   *   if (user is logged in)
   *     if (origin url exists)
   *       use origin url
   *       wipe origin url from session (how did it get there?)
   *     else
   *       use referrer
   *   else (i.e. user needs to log in)
   *     if (successful login)
   *       if (origin url exists)
   *         use origin url
   *         wipe origin url from session (how did it get there?)
   *       else
   *         use referrer
   *     else
   *       set referrer on session
   *       set origin url on session
   *       redirect = false
   *       look for custom Login.jsp
   *       if (custom Login.jsp exists)
   *         use custom Login.jsp
   *       else
   *         use wdk login page name
   *         
   * New logic for resulting page:
   *   check for referrer-form as request parameters
   *   read referrer on request (submitting page)
   *   if (user is logged in or successful login)
   *     redirect = true
   *     if (referrer-form exists)
   *       use referrer-form
   *     else
   *       use referrer
   *   else
   *     redirect = false
   *     use login page name
   *     if (referrer-form exists)
   *       set referrer-form on request
   *     else
   *       set referrer on request
   */
  @Override
  protected ActionResult handleRequest(ParamGroup params) throws Exception {

    // get the current user
    UserBean guest = getCurrentUser();
    UserFactoryBean factory = getWdkModel().getUserFactory();

    if (!guest.isGuest()) {
      // then user is already logged in
      return new ActionResult().setViewName(SUCCESS);
    }
    else {
      //return oldHandleLogin(params, guest, factory);
      return handleLogin(params, guest, factory);
    }
  }

  private ActionResult handleLogin(ParamGroup params, UserBean guest, UserFactoryBean factory) throws WdkModelException {
    AuthenticationMethod authMethod = getWdkModel().getModel().getModelConfig().getAuthenticationMethodEnum();
    switch (authMethod) {
      case OAUTH2:
        return handleOauthLogin(params, guest, factory);
      case USER_DB:
        return handleUserDbLogin(params, guest, factory);
      default:
        throw new WdkModelException("Login action does not yet handle authentication method " + authMethod);
    }
  }

  private ActionResult handleOauthLogin(ParamGroup params, UserBean guest, UserFactoryBean factory) {
    // params used to fetch token and validate user
    String redirectUrl = params.getValue("redirectUrl");
    String authCode = params.getValue("code");
    String stateToken = params.getValue("state");

    // confirm state token for security, then remove regardless of result
    String storedStateToken = (String)getSessionAttribute(OAuthUtil.STATE_TOKEN_KEY);
    unsetSessionAttribute(OAuthUtil.STATE_TOKEN_KEY);
    if (stateToken == null || storedStateToken == null || !stateToken.equals(storedStateToken)) {
      return getFailedLoginResult(new WdkModelException("Unable to log in; " +
          "state token missing, incorrect, or expired.  Please try again."));
    }

    try {
      OAuthClient client = new OAuthClient(getWdkModel().getModel().getModelConfig());
      int userId = client.getUserIdFromAuthCode(authCode, getRequestData().getFullRequestUrl());

      UserBean user = factory.login(guest, userId);
      if (user == null) throw new WdkModelException("Unable to find user with ID " +
          userId + ", returned by OAuth service for authCode " + authCode);

      int wdkCookieMaxAge = addLoginCookie(user, true, getWdkModel(), this);
      setCurrentUser(user);
      setSessionAttribute(CConstants.WDK_LOGIN_ERROR_KEY, "");
      // go back to user's original page after successful login
      return getSuccessfulLoginResult(redirectUrl, wdkCookieMaxAge);
    }
    catch (Exception e) {
      LOG.error("Could not log user in with authCode " + authCode, e);
      return getFailedLoginResult(e);
    }
  }

  private ActionResult handleUserDbLogin(ParamGroup params, UserBean guest, UserFactoryBean factory) {
    // get user's input
    String openid = params.getValueOrEmpty(CConstants.WDK_OPENID_KEY);
    String email = params.getValue(CConstants.WDK_EMAIL_KEY);
    String password = params.getValue(CConstants.WDK_PASSWORD_KEY);
    boolean remember = params.getSingleCheckboxValue(REMEMBER_PARAM_KEY);
    
    // authenticate
    try {
      // user must enter something for either openid or email/password
      //checkExistenceOfParams(params);
        
      if (openid != null && openid.length() > 0) {
        // first make sure we have a user with this OpenID
        openid = AuthenticationService.normalizeOpenId(openid);
        UserBean potentialUser = factory.getUserByOpenId(openid);
        if (potentialUser == null) {
          throw new WdkUserException("The OpenID you specified does not correspond to a registered user.");
        }
        
        // try to authenticate with OpenID
        try {
          AuthenticationService auth = new AuthenticationService();
          LOG.info("Setting referrer on OpenID login to : " + getRequestData().getReferrer());
          auth.setReferringUrl(getOriginalReferrer(params, getRequestData()));
          auth.setRememberUser(params.getSingleCheckboxValue("remember"));
          String redirectUrl = auth.authRequest(openid, getWebAppRoot());
          // same AuthenticationService MUST be used for stage 2, store on session for later retrieval
          setSessionAttribute(CConstants.WDK_OPENID_AUTH_SERVICE_KEY, auth);
          return new ActionResult().setRedirect(true).setViewPath(redirectUrl);
        }
        catch (Exception e) {
          throw new WdkUserException("Your OpenID could not be authenticated.  Please try again.", e);
        }
      }
      else {
        UserBean user = factory.login(guest, email, password);
        int wdkCookieMaxAge = addLoginCookie(user, remember, getWdkModel(), this);
        setCurrentUser(user);
        setSessionAttribute(CConstants.WDK_LOGIN_ERROR_KEY, "");
        // go back to user's original page after successful login
        String redirectPage = getOriginalReferrer(params, getRequestData());
        return getSuccessfulLoginResult(redirectPage, wdkCookieMaxAge);
      }
    }
    catch (Exception ex) {
      LOG.info("Could not authenticate user's identity.  Exception thrown: ", ex);
      // user authentication failed, set the error message and the referring page
      return getFailedLoginResult(ex);
    }
  }

  protected ActionResult getSuccessfulLoginResult(String redirectUrl, int wdkCookieMaxAge) {
    return new ActionResult().setRedirect(true).setViewPath(redirectUrl);
  }
  
  protected ActionResult getFailedLoginResult(Exception ex) {
    ActionResult result = new ActionResult()
      .setRequestAttribute(CConstants.WDK_LOGIN_ERROR_KEY, ex.getMessage())
      .setRequestAttribute(CConstants.WDK_REDIRECT_URL_KEY, getOriginalReferrer(getParams(), getRequestData()));
  
    String customViewFile = getCustomViewDir() + CConstants.WDK_LOGIN_PAGE;
    if (wdkResourceExists(customViewFile)) {
      return result.setRedirect(false).setViewPath(customViewFile);
    }
    else {
      return result.setViewName(INPUT);
    }
  }
  
  public static String getOriginalReferrer(ParamGroup params, RequestData requestData) {
    
    // always prefer the param value passed to us from a previous action
    String referrerParamValue = params.getValueOrEmpty(CConstants.WDK_REDIRECT_URL_KEY);
    if (!referrerParamValue.isEmpty()) {
      return referrerParamValue;
    }
      
    // if no referrer exists, then (presumably) user failed authentication, but in case they got
    // to the login page via bookmark or some other way, return to profile on successful login
    String referrer = requestData.getReferrer();
    if (referrer.indexOf("showLogin.do") != -1 ||
        referrer.indexOf("processLogin.do") != -1) {
        return "showProfile.do";
    }

    // otherwise, return referring page
    return referrer;
  }

  @SuppressWarnings("unused")
  private void checkExistenceOfParams(ParamGroup params) throws WdkUserException {
    // must make sure user submitted either username/password OR openid
    String openId = params.getValue(CConstants.WDK_OPENID_KEY);
    if (openId != null && openId.length() > 0) {
      // prefer openid
      return;
    }
    String email = params.getValue(CConstants.WDK_EMAIL_KEY);
    String password = params.getValue(CConstants.WDK_PASSWORD_KEY);
    if (email == null || email.length() == 0 || password == null || password.length() == 0) {
      throw new WdkUserException("You must enter either an OpenID or username/password.");
    }
  }
  
  static int addLoginCookie(UserBean user, boolean remember, WdkModelBean model, WdkAction wdkAction) {
    try {
      // Create & send cookie
      LoginCookieFactory auth = new LoginCookieFactory(model.getModel().getSecretKey());
      Cookie loginCookie = auth.createLoginCookie(user.getEmail(), remember);
      wdkAction.addCookieToResponse(loginCookie);
      return loginCookie.getMaxAge();
    }
    catch (WdkModelException e) {
      // This is a recoverable exception since this is not the session cookie, just
      //   one to remember the user.  An error, but not one we need to advertise
      LOG.error("Unable to add user cookie to response on login!", e);
      return -1;
    }
  }
}
