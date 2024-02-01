package org.gusdb.wdk.session;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.gusdb.oauth2.client.OAuthClient;
import org.gusdb.oauth2.client.OAuthConfig;
import org.gusdb.oauth2.client.ValidatedToken;
import org.gusdb.wdk.model.WdkModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class WdkOAuthClientWrapper {

  private final OAuthConfig _config;
  private final OAuthClient _client;

  public WdkOAuthClientWrapper(WdkModel wdkModel) {
    _config = wdkModel.getModelConfig();
    _client = new OAuthClient(OAuthClient.getTrustManager(wdkModel.getModelConfig()));
  }

  public ValidatedToken getBearerTokenFromAuthCode(String authCode, String redirectUri) {
    return _client.getBearerTokenFromAuthCode(_config, authCode, redirectUri);
  }

  public ValidatedToken getBearerTokenFromCredentials(String email, String password, String redirectUrl) {
    return _client.getBearerTokenFromUsernamePassword(_config,  email, password, redirectUrl);
  }

  public ValidatedToken validateBearerToken(String rawToken) {
    return _client.getValidatedEcdsaSignedToken(_config.getOauthUrl(), rawToken);
  }

  public JSONObject getUserData(ValidatedToken token) {
    return _client.getUserData(_config.getOauthUrl(), token);
  }

  public List<JSONObject> getUserData(List<Long> userIds) {
    List<String> stringIds = userIds.stream().map(String::valueOf).collect(Collectors.toList());
    JSONArray usersJson = _client.getUserData(_config, stringIds);
    List<JSONObject> users = new ArrayList<>();
    for (int i = 0; i < usersJson.length(); i++) {
      users.add(usersJson.getJSONObject(i));
    }
    return users;
  }
  public ValidatedToken getNewGuestToken() {
    return _client.getNewGuestToken(_config);
  }

}
