package com.fonoster.rest;

import com.fonoster.rest.filters.AuthUtil;
import com.fonoster.core.api.AppsAPI;
import com.fonoster.core.api.DBManager;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.Activity;
import com.fonoster.model.App;
import com.sun.xml.txw2.annotation.XmlElement;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/accounts/{accountId}/apps")
public class AppsService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getApps(@QueryParam("start") String start,
        @QueryParam("end") String end,
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("pageSize") @DefaultValue("1000") int pageSize,
        @QueryParam("starred") @DefaultValue("false") boolean starred,
        @QueryParam("status") @DefaultValue("NORMAL") App.Status status,
        @Context HttpServletRequest httpRequest) {

        List<App> apps;

        try {
            Account account = AuthUtil.getAccount(httpRequest);

            DateTime jStart = null;
            DateTime jEnd = null;

            if (start != null && !start.isEmpty()) jStart = new DateTime(start);
            if (end != null && !end.isEmpty()) jEnd = new DateTime(end);

            apps = AppsAPI.getInstance().getApps(account.getUser(),
                jStart,
                jEnd,
                pageSize,
                pageSize * page,
                starred,
                status);

            int total = AppsAPI.getInstance().getApps(account.getUser(),
                jStart,
                jEnd,
                // Max allow
                1000,
                // To ensure that there is a least 1000 elements
                0,
                starred,
                status).size();

            Apps appPages = new Apps(page, pageSize, total, apps);

            return Response.ok(appPages).build();
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        } catch (ApiException e) {
            return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, e.getMessage());
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{appId}")
    public Response getApp(@PathParam("appId") String appId, @Context HttpServletRequest httpRequest) {
        App app;

        try {
            Account account = AuthUtil.getAccount(httpRequest);

            if (!ObjectId.isValid(appId)) return ResponseUtil.getResponse(ResponseUtil.NOT_FOUND);

            app = AppsAPI.getInstance().getAppById(account.getUser(), new ObjectId(appId), false);

            return Response.ok(app).build();
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        } catch (ApiException e) {
            return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, e.getMessage());
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response saveApp(App app, @Context HttpServletRequest httpRequest) {
        try {
            Account account = AuthUtil.getAccount(httpRequest);

            // If app is null create a 'Untitled' document
            App appFromDB;

            if (app == null || app.getId() == null) {
                appFromDB = AppsAPI.getInstance().createApp(account.getUser(), app.getName(), "");
            } else {
                // Update object
                appFromDB = AppsAPI.getInstance().getAppById(account.getUser(), app.getId(), true);
                appFromDB.setName(app.getName());
                appFromDB.setScripts(app.getScripts());
                appFromDB.setStarred(app.isStarred());
                appFromDB.setStatus(app.getStatus());
                appFromDB.setModified(DateTime.now());
                appFromDB.setStarred(app.isStarred());

                DBManager.getInstance().getDS().save(appFromDB);
            }

            // This is a new app; therefore...
            if (app.getId() == null) {
                UsersAPI.getInstance().createActivity(account.getUser(), "You've created a " +
                                "<a href=\"/editor?appId=" + appFromDB.getId() + "\" class=\"ng-binding\">new app</a>",
                        Activity.Type.INFO);
            }

            return Response.ok(appFromDB).build();
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        } catch (ApiException e) {
            return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, e.getMessage());
        }
    }

    @DELETE
    @Path("/{appId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteApp(@PathParam("appId") String appId, @Context HttpServletRequest httpRequest) {
        try {
            Account account = AuthUtil.getAccount(httpRequest);

            App app = AppsAPI.getInstance().getAppById(account.getUser(), new ObjectId(appId), false);

            app.setStatus(App.Status.DELETED);
            DBManager.getInstance().getDS().save(app);

            return Response.ok().build();
        } catch (UnauthorizedAccessException e) {
            return ResponseUtil.getResponse(ResponseUtil.UNAUTHORIZED);
        } catch (ApiException e) {
            return ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, e.getMessage());
        }
    }

    @XmlElement
    public static class Apps {
        private int page;
        private int total;
        private int pageSize;
        private List<App> apps;

        private Apps(int page, int pageSize, int total, List<App> apps) {
            this.page = page;
            this.pageSize = pageSize;
            this.total = total;
            this.apps = apps;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<App> getApps() {
            return apps;
        }

        public void setApps(List<App> apps) {
            this.apps = apps;
        }
    }
}