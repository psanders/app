/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.rest;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.User;
import org.bson.types.ObjectId;
import org.glassfish.jersey.internal.util.Base64;

import javax.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Since("1.0")
public class AuthUtil {
  private static final String BASIC_AUTH = "Basic";

  // Obtain account from http request
  public static Account getAccount(HttpServletRequest httpRequest)
          throws ApiException {
    final Credentials credentials = getCredentialsFromRequest(httpRequest);
    final AccountCredentials accountCredentials = new AccountCredentials(credentials.getUsername(), credentials.getSecret());
    final Account account =
        UsersAPI.getInstance().getAccountById(new ObjectId(accountCredentials.getAccountId()));

    if (account == null || !account.getToken().equals(accountCredentials.getToken())) {
      throw new UnauthorizedAccessException();
    }

    return account;
  }

  // Obtain user from http request
  public static User getUser(HttpServletRequest httpRequest) throws ApiException {
    final Credentials credentials = getCredentialsFromRequest(httpRequest);
    final UserCredentials userCredentials = new UserCredentials(credentials.getUsername(), credentials.getSecret());
    final User user = UsersAPI.getInstance().getUserByEmail(userCredentials.getEmail());

    if (user == null || !user.getPassword().equals(Base64.encodeAsString(credentials.getSecret())))
      throw new UnauthorizedAccessException();

    return user;
  }

  // Verify admin
  public static boolean isAdmin(HttpServletRequest httpRequest) {
    final Credentials credentials = getCredentialsFromRequest(httpRequest);
    return isAdmin(credentials.getUsername(), credentials.getSecret());
  }

  // Verify admin
  public static boolean isAdmin(String username, String secret)  {
    return CommonsConfig.getInstance().getAdminUsername().equals(username)
            && CommonsConfig.getInstance().getAdminSecret().equals(secret);
  }

  static Credentials getCredentialsFromRequest(HttpServletRequest request) {
    //Fetch authorization header
    final String authorization = request.getHeader(AUTHORIZATION);
    //Get encoded username and password
    final String encodedUserSecret = authorization.replace(BASIC_AUTH + " ", "");
    //Decode username and password
    final String usernameAndSecret = Base64.decodeAsString(encodedUserSecret);
    //Split username and password tokens
    final StringTokenizer tokenizer = new StringTokenizer(usernameAndSecret, ":");
    final String username = tokenizer.nextToken();
    final String secret = tokenizer.nextToken();

    return new Credentials(username, secret);
  }
}
