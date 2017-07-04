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
import com.fonoster.core.api.DIDNumbersAPI;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Account;
import com.fonoster.model.Activity;
import com.fonoster.model.DIDNumber;

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
@Path("/accounts/{accountId}/dids")
public class DIDsService {

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Response getDIDs(
      @QueryParam("page") @DefaultValue("0") int page,
      @QueryParam("pageSize") @DefaultValue("1000") int pageSize,
      @QueryParam("status") DIDNumber.Status status,
      @Context HttpServletRequest httpRequest)
      throws ApiException {

    Account account = AuthUtil.getAccount(httpRequest);

    List<DIDNumber> didsList =
        DIDNumbersAPI.getInstance()
            .getDIDNumbersFor(account.getUser(), status, pageSize, pageSize * page);

    int total = DIDNumbersAPI.getInstance().getDIDNumbersFor(account.getUser(), status).size();

    DIDs dids = new DIDs(page, pageSize, total, didsList);

    return Response.ok(dids).build();
  }

  @POST
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/preferred")
  public Response setPreferred(DIDNumber phone, @Context HttpServletRequest httpRequest)
      throws UnauthorizedAccessException {

    Account account = AuthUtil.getAccount(httpRequest);

    try {
      // This is just to  ensure that he owns the number
      DIDNumber did =
          DIDNumbersAPI.getInstance().getDIDNumber(account.getUser(), phone.getSpec().getLocation().getTelUrl().replace("tel:",""));
      DIDNumbersAPI.getInstance().setDefault(account.getUser(), did);

      UsersAPI.getInstance()
          .createActivity(
              account.getUser(),
              "Your test number changed to ".concat(did.getSpec().getLocation().getTelUrl().replace("tel:", "")),
              Activity.Type.SETTING);

      return Response.ok().build();
    } catch (ApiException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/preferred")
  public Response getPreferred(@Context HttpServletRequest httpRequest) throws ApiException {
    Account account = AuthUtil.getAccount(httpRequest);
    DIDNumber did = DIDNumbersAPI.getInstance().getDefault(account.getUser());
    return Response.ok(did).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/regions")
  // TODO: This should be in a different service
  public Response getRegions(@Context HttpServletRequest httpRequest) {
    throw new UnsupportedOperationException();
  }

  // For media type "xml", this inner class must be static have the @XmlRootElement annotation
  // and a no-argument constructor.
  @XmlRootElement
  static class DIDs {
    private int page;
    private int total;
    private int pageSize;
    private List<DIDNumber> dids;

    // Must have no-argument constructor
    public DIDs() {}

    private DIDs(int page, int pageSize, int total, List<DIDNumber> dids) {
      this.page = page;
      this.pageSize = pageSize;
      this.total = total;
      this.dids = dids;
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

    public List<DIDNumber> getDIDs() {
      return dids;
    }

    public void setDIDs(List<DIDNumber> dids) {
      this.dids = dids;
    }
  }
}
