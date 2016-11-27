package com.fonoster.rest;

import com.fonoster.core.api.AnalyticsAPI;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.rest.filters.AuthUtil;
import com.fonoster.model.Account;
import com.fonoster.model.CallStats;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/accounts/{accountId}/analytics")
public class AnalyticsService {

    @GET
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/calls/{period}")
    public Response getTrafficAnalysis(@PathParam("period") CallStats.Period period,
        @Context HttpServletRequest httpRequest) {
        Account account;

        try {
            account = AuthUtil.getAccount(httpRequest);
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        }

        CallStats cs = AnalyticsAPI.getInstance().getStats(account, period, DateTime.now());

        return Response.ok(cs).build();
    }
}
