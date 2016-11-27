package com.fonoster.rest.filters;

import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.User;
import com.sun.jersey.core.util.Base64;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

public class AuthUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AuthUtil.class);

    // Obtain account from http request
    static public Account getAccount(HttpServletRequest httpRequest) throws UnauthorizedAccessException {
        ObjectId accountId;
        try {
            String authorization = httpRequest.getHeader("Authorization");
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.decode(base64Credentials.getBytes()), Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);

            if (values.length != 2 || !ObjectId.isValid(values[0])) throw new UnauthorizedAccessException();
            accountId = new ObjectId(values[0]);
        } catch (NullPointerException e) {
            LOG.debug("Missing authorization headers.", e);
            throw new UnauthorizedAccessException("Missing authorization headers.");
        }
        return UsersAPI.getInstance().getAccountById(accountId);
    }

    // Obtain user from http request
    static public User getUser(HttpServletRequest httpRequest) throws UnauthorizedAccessException {
        String username;
        String password;
        try {
            String authorization = httpRequest.getHeader("Authorization");
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.decode(base64Credentials.getBytes()), Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);

            if (values.length != 2) throw new UnauthorizedAccessException();
            username = values[0];
            password = new String(Base64.encode(values[1]));
        } catch (NullPointerException e) {
            LOG.debug("Missing authorization headers.", e);
            throw new UnauthorizedAccessException("Missing authorization headers.");
        }
        User user = UsersAPI.getInstance().getUserByEmail(username);

        if (user == null || !user.getPassword().equals(password.trim())) {
            throw new UnauthorizedAccessException();
        }

        return user;
    }
}
