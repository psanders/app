package com.fonoster.rest;

import com.fonoster.config.CommonsConfig;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.User;
import org.bson.types.ObjectId;
import org.glassfish.jersey.internal.util.Base64;

import javax.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class AuthUtil {

    private static final String BASIC_AUTH = "Basic";

    // Obtain account from http request
    static public Account getAccount(HttpServletRequest httpRequest) throws UnauthorizedAccessException {
        final Credentials credentials = getCredentialsFromRequest(httpRequest);
        final Account account = UsersAPI.getInstance().getAccountById(new ObjectId(credentials.getUsername()));

        if (account == null || !account.getToken().equals(credentials.getSecret())) {
            throw new UnauthorizedAccessException();
        }

        return account;
    }

    // Obtain user from http request
    static public User getUser(HttpServletRequest httpRequest) throws UnauthorizedAccessException {
        final Credentials credentials = getCredentialsFromRequest(httpRequest);
        final User user = UsersAPI.getInstance().getUserByEmail(credentials.getUsername());

        if (user == null || !user.getPassword().equals(Base64.encodeAsString(credentials.getSecret())))
            throw new UnauthorizedAccessException();

        return user;
    }

    // Verify admin
    static public boolean isAdmin(HttpServletRequest httpRequest) throws UnauthorizedAccessException {
        final Credentials credentials = getCredentialsFromRequest(httpRequest);

        return CommonsConfig.getInstance().getAdminUsername().equals(credentials.getUsername()) &&
                CommonsConfig.getInstance().getAdminSecret().equals(credentials.getSecret());
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
        final String password = tokenizer.nextToken();

        return new Credentials(username, password);
    }
}
