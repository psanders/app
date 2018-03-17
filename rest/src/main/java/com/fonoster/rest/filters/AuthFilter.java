/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.rest.filters;

import com.fonoster.annotations.Since;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.model.Account;
import com.fonoster.model.User;
import com.fonoster.rest.AuthUtil;
import org.apache.commons.validator.routines.EmailValidator;
import org.bson.types.ObjectId;
import org.glassfish.jersey.internal.util.Base64;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.*;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Since("1.0")
@Provider
public class AuthFilter implements ContainerRequestFilter {

  private static final String BASIC_AUTH = "Basic";
  private static final Response ACCESS_DENIED =
    Response.status(Response.Status.UNAUTHORIZED)
      .entity(new com.fonoster.rest.Response(Response.Status.UNAUTHORIZED.getStatusCode(),
        "Unauthorized access."))
         .build();
  private static final Response ACCESS_FORBIDDEN =
    Response.status(Response.Status.FORBIDDEN)
      .entity(new com.fonoster.rest.Response(Response.Status.FORBIDDEN.getStatusCode(),
      "Access forbidden."))
        .build();
  @Context private ResourceInfo resourceInfo;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    Class resourceClass = resourceInfo.getResourceClass();

    //Access allowed for all
    if (!resourceClass.isAnnotationPresent(PermitAll.class)) {
      //Access denied for all
      if (resourceClass.isAnnotationPresent(DenyAll.class)) {
        requestContext.abortWith(ACCESS_FORBIDDEN);
        return;
      }

      //Get request headers
      final MultivaluedMap<String, String> headers = requestContext.getHeaders();
      //Fetch authorization header
      final List<String> authorization = headers.get(AUTHORIZATION);

      //If no authorization information present; block access
      if (authorization == null || authorization.isEmpty()) {
        requestContext.abortWith(ACCESS_DENIED);
        return;
      }

      //Get encoded username and password
      final String encodedUserPassword = authorization.get(0).replace(BASIC_AUTH + " ", "");

      //Decode username and password
      String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));

      //Split username and password tokens
      final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
      final String username = tokenizer.nextToken();
      final String password = tokenizer.nextToken();

      //Verify user access
      if (resourceClass.isAnnotationPresent(RolesAllowed.class)) {
        RolesAllowed rolesAnnotation = (RolesAllowed) resourceClass.getAnnotation(RolesAllowed.class);
        Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));
        Iterator i = rolesSet.iterator();

        //Is user valid?
        try {
          if (!isAllowed(username.trim(), password.trim(), rolesSet)) {
            requestContext.abortWith(ACCESS_DENIED);
          }
        } catch (ApiException e) {
          // ?
          requestContext.abortWith(ACCESS_DENIED);
        }
      }
    }
  }

  // Role will be base on User, Account, Admin or SP
  private boolean isAllowed(
      final String username, final String secret, final Set<String> rolesSet) throws ApiException {
    boolean isAllowed = false;
    boolean access = false;
    String userRole;

    // Is an account request
    if(ObjectId.isValid(username)) {
      userRole = "USER";
      Account account = UsersAPI.getInstance().getAccountById(new ObjectId(username));
      if (account != null && account.getToken().equals(secret)) access = true;
    } else if (EmailValidator.getInstance().isValid(username)) {
      userRole = "USER";
      User user = UsersAPI.getInstance().getUserByEmail(username);
      if (user != null && user.getPassword().equals(Base64.encodeAsString(secret))) access = true;
    } else {
      userRole = "ADMIN";
      access = AuthUtil.isAdmin(username, secret);
    }

    if (access) {
      //Step 2. Verify user role
      if (rolesSet.contains(userRole)) {
        isAllowed = true;
      }
    }
    return isAllowed;
  }
}
