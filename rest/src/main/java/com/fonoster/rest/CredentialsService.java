package com.fonoster.rest;

import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Activity;
import com.fonoster.rest.filters.AuthUtil;
import com.fonoster.model.Account;
import com.fonoster.model.User;
import com.sun.xml.txw2.annotation.XmlElement;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This service is intended to be use from the webapp (or any future clients).
 */
@Path("/users/credentials")
public class CredentialsService {

    @GET
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response login(@Context HttpServletRequest httpRequest) {
        try {
            User user = AuthUtil.getUser(httpRequest);

            if (user == null) throw new UnauthorizedAccessException("Invalid username or password." );

            Account main = user.getAccount();
            CredentialsResponse credentials = new CredentialsResponse(main.getId().toHexString(), main.getToken());
            return Response.ok(credentials).build();
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED, "Invalid username or password.");
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // Re-generates the users main account
    // Warning: Should I allow regen of sub-accounts with this method?
    public Response regenToken(CredentialsRequest request) {

        if (request.email == null || request.password == null) return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED, "Invalid username or password.");

        User user = UsersAPI.getInstance().getUserByEmail(request.email.trim());

        byte[] encodedBytes = Base64.encodeBase64(request.getPassword().getBytes());
        String pass = new String(encodedBytes);

        if (user != null && user.getPassword().equals(pass.trim())) {
            Account account = user.getAccount();
            account.regenerateToken();
            UsersAPI.getInstance().updateAccount(account);
            CredentialsResponse credentials = new CredentialsResponse(account.getId().toHexString(), account.getToken());

            UsersAPI.getInstance().createActivity(account.getUser(), "Account token has ben regenerated",
                    Activity.Type.SYS);

            return Response.ok(credentials).build();
        }

        return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED, "Invalid username or password.");
    }

    @XmlElement
    // Yes this class must be static or it will cause a :
    // java.lang.ArrayIndexOutOfBoundsException: 3
    // at org.codehaus.jackson.map.introspect.AnnotatedWithParams.getParameter(AnnotatedWithParams.java:138)
    // Solution found here: http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
    static class CredentialsRequest {
        private String email;
        private String password;

        // Not marking this with JsonProperty was causing;
        //  No suitable constructor found for type [simple type,
        // class CredentialsService$CredentialsRequest]:
        // can not instantiate from JSON object (need to add/enable type information?)
        public CredentialsRequest(@JsonProperty("email") String email,
            @JsonProperty("password") String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @XmlRootElement
    static class CredentialsResponse {
        private String accountId;
        private String token;

        // Test before you remove this
        public CredentialsResponse() {}

        public CredentialsResponse(String accountId, String token) {
            this.accountId = accountId;
            this.token = token;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
