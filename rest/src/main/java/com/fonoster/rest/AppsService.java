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
import com.fonoster.core.api.AppsAPI;
import com.fonoster.core.api.DBManager;
import com.fonoster.exception.ApiException;
import com.fonoster.model.Account;
import com.fonoster.model.App;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@Since("1.0")
@RolesAllowed({"USER"})
@Path("/accounts/{accountId}/apps")
public class AppsService {

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Response getApps(
      @QueryParam("start") String start,
      @QueryParam("end") String end,
      @QueryParam("page") @DefaultValue("0") int page,
      @QueryParam("pageSize") @DefaultValue("1000") int pageSize,
      @QueryParam("starred") @DefaultValue("false") boolean starred,
      @QueryParam("status") @DefaultValue("NORMAL") App.Status status,
      @Context HttpServletRequest httpRequest)
      throws ApiException {

    Account account = AuthUtil.getAccount(httpRequest);

    DateTime jStart = null;
    DateTime jEnd = null;

    if (start != null && !start.isEmpty()) jStart = new DateTime(start);
    if (end != null && !end.isEmpty()) jEnd = new DateTime(end);

    List<App> apps =
        AppsAPI.getInstance()
            .getApps(account.getUser(), jStart, jEnd, pageSize, pageSize * page, starred, status);

    int total =
        AppsAPI.getInstance()
            .getApps(
                account.getUser(),
                jStart,
                jEnd,
                // Max allow
                1000,
                // To ensure that there is a least 1000 elements
                0,
                starred,
                status)
            .size();

    Apps appPages = new Apps(page, pageSize, total, apps);

    return Response.ok(appPages).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{appId}")
  public Response getApp(@PathParam("appId") String appId, @Context HttpServletRequest httpRequest)
      throws ApiException {
    Account account = AuthUtil.getAccount(httpRequest);
    App app = AppsAPI.getInstance().getAppById(account.getUser(), new ObjectId(appId), false);
    return Response.ok(app).build();
  }

  @POST
  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Response saveApp(App app, @Context HttpServletRequest httpRequest) throws ApiException {
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

    return Response.ok(appFromDB).build();
  }

  @DELETE
  @Path("/{appId}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Response deleteApp(
      @PathParam("appId") String appId, @Context HttpServletRequest httpRequest)
      throws ApiException {
    Account account = AuthUtil.getAccount(httpRequest);

    App app = AppsAPI.getInstance().getAppById(account.getUser(), new ObjectId(appId), false);
    app.setStatus(App.Status.DELETED);
    DBManager.getInstance().getDS().save(app);

    return Response.ok().build();
  }

  // For media type "xml", this inner class must be static have the @XmlRootElement annotation
  // and a no-argument constructor.
  @XmlRootElement
  static class Apps {
    private int page;
    private int total;
    private int pageSize;
    private List<App> apps;

    // Must have no-argument constructor
    public Apps() {}

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
