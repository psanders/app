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
import com.fonoster.core.api.SipIOResourcesAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.model.*;
import net.minidev.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Since("1.0")
@RolesAllowed({"ADMIN"})
@Path("/admin")
public class AdminService {

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{collection}")
  public Response addResource(
          @PathParam("collection") String collection,
          JSONObject jsonObj, @Context HttpServletRequest httpRequest)
          throws ApiException, IOException {
    String result = SipIOResourcesAPI.getInstance().insert(jsonObj);

    com.fonoster.rest.Response res = new com.fonoster.rest.Response(
        Status.CREATED.getValue(),
        Status.getMessage(Status.CREATED)
    );

    res.setResult(result);

    return Response.ok(res.getStatus()).entity(res).build();
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{collection}/{ref}")
  public Response get(
          @PathParam("collection") String collection,
          @PathParam("ref") String ref,
          @Context HttpServletRequest httpRequest)
          throws ApiException {

    Object obj = null;

    switch (collection) {
      case "gateways":
        obj = SipIOResourcesAPI.getInstance().get(Gateway.class, ref);
        break;
      case "dids":
        obj = SipIOResourcesAPI.getInstance().get(DIDNumber.class, ref);
        break;
      case "agents":
        obj = SipIOResourcesAPI.getInstance().get(Agent.class, ref);
        break;
      case "domains":
        obj = SipIOResourcesAPI.getInstance().get(Domain.class, ref);
        break;
    }

    Status status = Status.OK;
    if (obj == null) status = Status.NOT_FOUND;

    com.fonoster.rest.Response res = new com.fonoster.rest.Response(
        status.getValue(),
        Status.getMessage(status)
    );

    res.setResult(obj);

    return Response.ok(res.getStatus()).entity(res).build();
  }

  @DELETE
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{collection}/{ref}")
  public Response remove(
          @PathParam("collection") String collection,
          @PathParam("ref") String ref,
          @Context HttpServletRequest httpRequest)
          throws ApiException {

    switch (collection) {
      case "gateways":
        SipIOResourcesAPI.getInstance().remove(Gateway.class, ref);
        break;
      case "dids":
        SipIOResourcesAPI.getInstance().remove(DIDNumber.class, ref);
        break;
      case "agents":
        SipIOResourcesAPI.getInstance().remove(Agent.class, ref);
        break;
      case "domains":
        SipIOResourcesAPI.getInstance().remove(Domain.class, ref);
        break;
    }

    Status status = Status.OK;

    com.fonoster.rest.Response res = new com.fonoster.rest.Response(
            status.getValue(),
            Status.getMessage(status)
    );

    return Response.ok(res.getStatus()).entity(res).build();
  }

  @PUT
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{collection}/{ref}")
  public Response updateResource(
          @PathParam("collection") String collection,
          @PathParam("ref") String ref,
          JSONObject jsonObj, @Context HttpServletRequest httpRequest)
          throws ApiException, IOException {
    SipIOResourcesAPI.getInstance().update(jsonObj);

    com.fonoster.rest.Response res = new com.fonoster.rest.Response(
            Status.OK.getValue(),
            Status.getMessage(Status.OK)
    );

    return Response.ok(res.getStatus()).entity(res).build();
  }


  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{collection}")
  public Response getCollection(
          @PathParam("collection") String collection,
          @QueryParam("filter") String filter,
          @Context HttpServletRequest httpRequest)
          throws ApiException {

    List<Agent> list = new ArrayList<>();

    switch (collection) {
      case "gateways":
        list = SipIOResourcesAPI.getInstance().find(Gateway.class, filter);
        break;
      case "dids":
        list = SipIOResourcesAPI.getInstance().find(DIDNumber.class, filter);
        break;
      case "agents":
        list = SipIOResourcesAPI.getInstance().find(Agent.class, filter);
        break;
      case "domains":
        list = SipIOResourcesAPI.getInstance().find(Domain.class, filter);
        break;
    }

    Status status = Status.OK;
    if (list.isEmpty()) status = Status.NOT_FOUND;

    com.fonoster.rest.Response res = new com.fonoster.rest.Response(
        status.getValue(),
        Status.getMessage(status)
    );

    res.setResult(list);

    return Response.ok(res.getStatus()).entity(res).build();
  }

}
