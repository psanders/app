/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.rest.filters;

import com.fonoster.annotations.Since;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Since("1.0")
@Provider
public class ContentTypeFilter implements ContainerResponseFilter {

  private static final Response NOT_ACCEPTABLE =
    Response.status(Response.Status.NOT_ACCEPTABLE)
      .entity(new com.fonoster.rest.Response(Response.Status.NOT_ACCEPTABLE.getStatusCode(),
        "Invalid format.")).build();

  @Override
  public void filter(ContainerRequestContext request, ContainerResponseContext response)
      throws IOException {
    String result = request.getUriInfo().getQueryParameters().getFirst("result");

    if (result == null || result.equals("json")){
      response.getHeaders().putSingle("content-type", "application/json");
    } else if (result.equals("xml")) {
      response.getHeaders().putSingle("content-type", "application/xml");
    } else {
      request.abortWith(NOT_ACCEPTABLE);
    }
  }
}
