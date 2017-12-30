/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.exception.UserAlreadyExistException;
import com.fonoster.model.Account;
import com.fonoster.model.Activity;
import com.fonoster.model.User;
import com.fonoster.services.MailManager;
import org.bson.types.ObjectId;
import org.glassfish.jersey.internal.util.Base64;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Since("1.0")
@RolesAllowed({"USER"}) // Is this needed?
@PermitAll
@Path("/users")
public class UsersService {
  private static final Logger LOG = LoggerFactory.getLogger(CredentialsService.class);
  private static final CommonsConfig config = CommonsConfig.getInstance();

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Response createUser(User u, @Context HttpServletRequest httpRequest) throws ApiException {

    User uFromDB = UsersAPI.getInstance().getUserByEmail(u.getEmail());

    if (uFromDB != null) {
      Account account = AuthUtil.getAccount(httpRequest);
      Account accountFromDB = UsersAPI.getInstance().getMainAccount(uFromDB);

      if (!accountFromDB.getId().equals(account.getId()))
        throw new UnauthorizedAccessException();

      uFromDB.setFirstName(u.getFirstName());
      uFromDB.setLastName(u.getLastName());
      uFromDB.setPhone(u.getPhone());
      uFromDB.setCompany(u.getCompany());
      uFromDB.setTimezone(u.getTimezone());
      uFromDB.setCountryCode(u.getCountryCode());
      // This should never happens. Instead /users/{email} which uses the users credentials
      //uFromDB.setSecret(secret);
      uFromDB.setModified(new DateTime());
      UsersAPI.getInstance().updateUser(uFromDB);
      return Response.ok(uFromDB).build();
    } else {
      String secret = Base64.encodeAsString(u.getPassword());
      User user =
          UsersAPI.getInstance()
              .createUser(u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhone(), secret);
      return Response.ok(user).build();
    }
  }

  @GET
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{email}")
  public Response getUser(@PathParam("email") String email, @Context HttpServletRequest httpRequest)
          throws ApiException {
    Account account = AuthUtil.getAccount(httpRequest);
    if (!account.getUser().getEmail().equals(email.trim())) throw new UnauthorizedAccessException();
    User u = UsersAPI.getInstance().getUserByEmail(email);
    return Response.ok(u).build();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{email}/password")
  public Response changePassword(ChangePasswordRequest cpr, @Context HttpServletRequest httpRequest)
      throws ApiException {

    LOG.debug("Changing secret for: " + cpr.email + " new pass is " + cpr.getPassword());

    Account account = AuthUtil.getAccount(httpRequest);
    String secret;
    User uFromDB;

    // Resetting from webapp
    if (account == null) {
      uFromDB = UsersAPI.getInstance().getUserByEmail(cpr.email);
      String id = new ObjectId().toHexString();
      secret = id.substring(id.length() - 5);
      if (uFromDB != null) {
        MailManager.getInstance().sendMsg(config.getTeamMail(), cpr.getEmail(), "Your temporal password", "Your temporal secret is: " + secret);
      }
    } else {
      uFromDB = UsersAPI.getInstance().getUserByEmail(account.getUser().getEmail());

      if (uFromDB == null) throw new UnauthorizedAccessException();
      if (cpr.getPassword().isEmpty()) throw new ApiException("Can't assign an empty password");

      secret = cpr.getPassword();
    }

    String encodedSecret = Base64.encodeAsString(secret);
    uFromDB.setPassword(encodedSecret);
    UsersAPI.getInstance().updateUser(uFromDB);

    UsersAPI.getInstance().createActivity(account.getUser(), "Password changed", Activity.Type.SYS);

    return Response.ok().build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{email}/activities")
  public Response getActivities(
      @QueryParam("maxResults") @DefaultValue("10") int maxResults,
      @Context HttpServletRequest httpRequest)
      throws ApiException {
    Account account = AuthUtil.getAccount(httpRequest);
    List<Activity> activities =
        UsersAPI.getInstance().getActivitiesFor(account.getUser(), maxResults);
    GenericEntity<List<Activity>> entity =
            new GenericEntity<List<Activity>>(activities) {};
    return Response.ok(entity).build();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{email}/signup")
  public Response signup(@PathParam("email") String email) throws ApiException {

    User user = UsersAPI.getInstance().getUserByEmail(email);

    if (user != null) {
      LOG.debug(
          "User with email: " + email + " is requesting an signup, but an account already exist");
      MailManager.getInstance().sendMsg(config.getTeamMail(), config.getAdminMail(), "Alert: User attempt to re-signup",
           "User with email: " + email + " is requesting signup, but an account already exist");

      MailManager.getInstance().sendMsg(config.getTeamMail(), email, "You already have an account",
              "You already have an account in Fonoster. Perhaps, you should try to recover your secret.");

      throw new UserAlreadyExistException();
    }

    MailManager.getInstance().sendMsg(config.getTeamMail(), config.getAdminMail(), "New signup", "Person with email " + email + " is signing-up for an account");

    String approvedMsg = "Hi! Welcome Fonoster.\n";
    approvedMsg = approvedMsg.concat("\nPlease click the link bellow to completed your profile.");
    approvedMsg =
        approvedMsg.concat(
            "\n\n\thttps://console.fonoster.com/#/login?code=" + Base64.encodeAsString(email));
    approvedMsg = approvedMsg.concat("\n\nFonoster Team.");

    MailManager.getInstance().sendMsg(config.getTeamMail(), email, "Your new account",
        approvedMsg);

    return Response.ok().build();
  }

  // For media type "xml", this inner class must be static have the @XmlRootElement annotation
  // and a no-argument constructor.
  @XmlRootElement
  static class ChangePasswordRequest {
    private String email;
    private String password;

    // Must have no-argument constructor
    public ChangePasswordRequest() {}

    // Not marking this with JsonProperty was causing;
    // No suitable constructor found for type [simple type,
    // class CredentialsService$CredentialsRequest]:
    // can not instantiate from JSON object (need to add/enable type information?)
    public ChangePasswordRequest(@JsonProperty("email") String email, @JsonProperty("password") String password) {
      this.setEmail(email);
      this.setPassword(password);
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }
}
