package com.fonoster.rest;

import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Activity;
import com.fonoster.rest.filters.AuthUtil;
import com.fonoster.model.Account;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/accounts")
public class AccountsService {

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createAccount(String name, @Context HttpServletRequest httpRequest) {
        try {
            Account main = AuthUtil.getAccount(httpRequest);
            Account sub = UsersAPI.getInstance().createSubAccount(main, name);

            UsersAPI.getInstance().createActivity(main.getUser(), "New account created " + sub.getId ().toString (),
                    Activity.Type.INFO);

            return Response.ok(sub).build();
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        } catch (ApiException e) {
            return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, e.getMessage());
        }
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAccounts(@Context HttpServletRequest httpRequest) {
        try {
            Account main = AuthUtil.getAccount(httpRequest);
            List<Account> accounts = UsersAPI.getInstance().getAccountsFor(main.getUser());
            return Response.ok(accounts).build();
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{accountId}")
    public Response updateAccount(Account account,
        @Context HttpServletRequest httpRequest) {
        try {
            Account acct = AuthUtil.getAccount(httpRequest);

            Account a = UsersAPI.getInstance().getAccountById(account.getId());
            a.setName(account.getName());

            // Requesting user owns this resource.
            if (!a.getUser().equals(acct.getUser())) {
                throw new UnauthorizedAccessException();
            }

            UsersAPI.getInstance().createActivity(account.getUser(), "Updated account " + a.getId().toString(),
                    Activity.Type.INFO);

            return Response.ok(account).build();
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{accountId}")
    public Response getAccount(@PathParam("accountId") String accountId,
        @Context HttpServletRequest httpRequest) {

        try {
            Account acct = AuthUtil.getAccount(httpRequest);
            Account a = UsersAPI.getInstance().getAccountById(new ObjectId(accountId));

            // Requesting user owns this resource.
            if (!a.getUser().equals(acct.getUser())) {
                throw new UnauthorizedAccessException();
            }

            return Response.ok(a).build();
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }
    }

    @DELETE
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{accountId}")
    // This will only work for sub-accounts otherwise an error will be reported
    public Response removeAccount(@PathParam("accountId") String accountId,
                                     @Context HttpServletRequest httpRequest) {

        try {
            Account parent = AuthUtil.getAccount(httpRequest);
            Account sub = UsersAPI.getInstance().getAccountById(new ObjectId(accountId));

            if (sub.isSubAccount() && sub.getParentAccount().getId().equals(parent.getId())) {
                UsersAPI.getInstance().removeSubAccount(sub);
            } else {
                throw new UnauthorizedAccessException();
            }

            UsersAPI.getInstance().createActivity(parent.getUser(), "Removed account " + sub.getId().toString(),
                    Activity.Type.INFO);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        } catch (ApiException e) {
            return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, e.getMessage());
        }

        return Response.ok().build();
    }
}
