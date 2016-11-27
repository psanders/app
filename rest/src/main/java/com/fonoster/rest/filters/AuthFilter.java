package com.fonoster.rest.filters;

import com.fonoster.rest.ResponseUtil;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.model.Account;
import com.fonoster.model.Response;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;

@Provider
public class AuthFilter implements ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ContainerResponseFilter.class);

    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        LOG.debug("Filtering!");

        // The following resources must stay "insecure"
        if (request.getAbsolutePath().toString().endsWith("credentials")) return response;
        if (request.getAbsolutePath().toString().contains("users")) return response;
        if (request.getAbsolutePath().toString().contains("recordings")) return response;

        String authorization = request.getHeaderValue("Authorization");
        String[] lap = BasicAuth.decode(authorization);

        if (authorization == null || authorization.isEmpty()) {
            return getUnauthorizedResponse(response,  "Unauthenticated");
        }

        new ContentTypeFilter().filter(request, response);

        if (lap == null || lap.length != 2 || !ObjectId.isValid(lap[0])) {
            return getUnauthorizedResponse(response, "Unauthenticated");
        }

        Account account = UsersAPI.getInstance().getAccountById(new ObjectId(lap[0]));

        if (account == null || !account.getToken().equals(lap[1])) {
            return getUnauthorizedResponse(response, "Unauthenticated");
        }

        return response;
    }

    private ContainerResponse getUnauthorizedResponse(ContainerResponse response, String message) {
        Response r = new Response();
        r.setCode(ResponseUtil.UNAUTHORIZED);
        r.setMessage(message);
        response.setEntity(r);
        return response;
    }
}

class BasicAuth {
    public static String[] decode(String auth) {
        auth = auth.replaceFirst("[B|b]asic ", "");

        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);

        if (decodedBytes == null || decodedBytes.length == 0) {
            return null;
        }

        return new String(decodedBytes).split(":", 2);
    }
}