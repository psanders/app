package com.fonoster.rest;

import com.fonoster.core.api.UsersAPI;
import com.fonoster.config.CommonsConfig;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.Activity;
import com.fonoster.model.User;
import com.fonoster.rest.filters.AuthUtil;
import com.fonoster.services.MailManager;
import com.sun.xml.txw2.annotation.XmlElement;
import org.apache.commons.codec.binary.Base64;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jettison.json.JSONException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
public class UsersService {
    private static final Logger LOG = LoggerFactory.getLogger(CredentialsService.class);
    private static final CommonsConfig config = CommonsConfig.getInstance();

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createUser(User u, @Context HttpServletRequest httpRequest) {

        User uFromDB = UsersAPI.getInstance().getUserByEmail(u.getEmail());

        if (uFromDB != null) {
            Account account;

            try {
                account = AuthUtil.getAccount(httpRequest);
            } catch (UnauthorizedAccessException e) {
                return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
            }

            if (!uFromDB.getAccount().getId().equals(account.getId())) return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);

            uFromDB.setFirstName(u.getFirstName());
            uFromDB.setLastName(u.getLastName());
            uFromDB.setPhone(u.getPhone());
            uFromDB.setCompany(u.getCompany());
            uFromDB.setTimezone(u.getTimezone());
            uFromDB.setCountryCode(u.getCountryCode());
            // This should never happens. Instead /users/{email} which uses the users credentials
            //uFromDB.setPassword(password);
            uFromDB.setModified(new DateTime());
            UsersAPI.getInstance().updateUser(uFromDB);
            return Response.ok(uFromDB).build();
        } else {
            try {
                byte[] encodedBytes = Base64.encodeBase64(u.getPassword().getBytes());
                String password = new String(encodedBytes);
                User user = UsersAPI.getInstance().createUser(u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhone(), password);

                return Response.ok(user).build();
            } catch (ApiException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            }
        }
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{email}")
    public Response getUser(@PathParam("email") String email,
        @Context HttpServletRequest httpRequest) {

        Account account;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        if (!account.getUser().getEmail().equals(email.trim())) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        User u = UsersAPI.getInstance().getUserByEmail(email);

        return Response.ok(u).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{email}/password")
    public Response changePassword(ChangePasswordRequest cpr,
        @Context HttpServletRequest httpRequest) {

        LOG.debug("Changing password for: " + cpr.email + " new pass is " + cpr.password);

        Account account = null;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            //return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        String pass;
        User uFromDB;

        // Resetting from webapp
        if (account == null) {
            uFromDB = UsersAPI.getInstance().getUserByEmail(cpr.email);
            String id = new ObjectId().toHexString();
            pass = id.substring(id.length() - 5);
            if (uFromDB != null) {
                MailManager.getInstance().sendMsg(config.getTeamMail(), cpr.getEmail(), "Your temporal password", "Your temporal password is: " + pass);
            }
        } else {
            uFromDB = UsersAPI.getInstance().getUserByEmail(account.getUser().getEmail());
            if (!cpr.getPassword().isEmpty()) {
                pass = cpr.getPassword();
            } else {
                return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, "Can't assign an empty password");
            }
        }

        byte[] encodedBytes = Base64.encodeBase64(pass.getBytes());
        assert uFromDB != null;
        uFromDB.setPassword(new String(encodedBytes));
        UsersAPI.getInstance().updateUser(uFromDB);

        UsersAPI.getInstance().createActivity(account.getUser(), "Password changed",
                Activity.Type.SYS);

        return Response.ok().build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{email}/activities")
    public Response getActivities(@QueryParam("maxResults") @DefaultValue("10") int maxResults,
        @Context HttpServletRequest httpRequest) throws JSONException, ApiException {
        Account account;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        List<Activity> activities = UsersAPI.getInstance().getActivitiesFor(account.getUser(), maxResults);

        GenericEntity<List<Activity>> entity = new GenericEntity<List<Activity>>(activities) {
        };

        return Response.ok(entity).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{email}/signup")
    public Response signup(@PathParam("email") String email) {

        User user = UsersAPI.getInstance().getUserByEmail(email);

        if (user != null) {
            LOG.debug("User with email: " + email + " is requesting an signup, but an account already exist");
            MailManager.getInstance().sendMsg(config.getTeamMail(), config.getAdminMail(), "Alert: User attempt to re-signup",
                "User with email: " + email + " is requesting signup, but an account already exist");

            MailManager.getInstance().sendMsg(config.getTeamMail(), email, "You already have an account",
                    "You already have an account in Fonoster. Perhaps, you should try to recover your password.");

            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        MailManager.getInstance().sendMsg(config.getTeamMail(), config.getAdminMail(), "New signup", "Person with email " + email + " is signing-up for an account");

        String approvedMsg = "Hi! Welcome Fonoster.\n";
        approvedMsg = approvedMsg.concat("\nPlease click the link bellow to completed your profile.");
        approvedMsg = approvedMsg.concat("\n\n\thttps://console.fonoster.com/#/login?code=" + Base64.encodeBase64String(email.getBytes()));
        approvedMsg = approvedMsg.concat("\n\nFonoster Team.");

        MailManager.getInstance().sendMsg(config.getTeamMail(), email, "Your new account",
            approvedMsg);

        return Response.ok().build();
    }

    @XmlElement
    // Yes this class must be static or it will cause a :
    // java.lang.ArrayIndexOutOfBoundsException: 3
    // at org.codehaus.jackson.map.introspect.AnnotatedWithParams.getParameter(AnnotatedWithParams.java:138)
    // Solution found here: http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
    static class ChangePasswordRequest {
        private String email;
        private String password;

        // Not marking this with JsonProperty was causing;
        //  No suitable constructor found for type [simple type,
        // class CredentialsService$CredentialsRequest]:
        // can not instantiate from JSON object (need to add/enable type information?)
        public ChangePasswordRequest(@JsonProperty("email") String email,
            @JsonProperty("password") String password) {
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
