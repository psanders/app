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
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.Activity;
import com.fonoster.model.User;
import org.glassfish.jersey.internal.util.Base64;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** This service is intended to be use from the webapp (or any future clients). */
@Since("1.0")
@RolesAllowed({"USER"})
@Path("/users/credentials")
public class CredentialsService {

  @GET
  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @PermitAll
  public Response login(@Context HttpServletRequest httpRequest)
      throws UnauthorizedAccessException {
    User user = AuthUtil.getUser(httpRequest);
    Account main = UsersAPI.getInstance().getMainAccount(user);
    AccountCredentials accountCredentials = new AccountCredentials(main.getId().toHexString(), main.getToken());
    return Response.ok(accountCredentials).build();
  }

  @POST
  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  // Re-generates the users main account
  // Warning: Should I allow regen of sub-accounts with this method?
  public Response regenToken(UserCredentials credentials) throws UnauthorizedAccessException {
    User user = UsersAPI.getInstance().getUserByEmail(credentials.getEmail());
    String encodedSecret = new String(Base64.encodeAsString(credentials.getPassword()));

    if (user != null && user.getPassword().equals(encodedSecret)) {
      Account account = UsersAPI.getInstance().getMainAccount(user);
      account.regenerateToken();
      UsersAPI.getInstance().updateAccount(account);
      AccountCredentials newAccountCredentials =
          new AccountCredentials(account.getId().toHexString(), account.getToken());

      UsersAPI.getInstance()
          .createActivity(
              account.getUser(), "Account token has been regenerated", Activity.Type.SYS);

      return Response.ok(newAccountCredentials).build();
    }

    throw new UnauthorizedAccessException();
  }
}
