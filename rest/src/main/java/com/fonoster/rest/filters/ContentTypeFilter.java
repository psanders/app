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
import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Since("1.0")
@Provider
public class ContentTypeFilter implements ContainerResponseFilter {

  @Override
  public void filter(ContainerRequestContext request, ContainerResponseContext response)
      throws IOException {
    if (request.getUriInfo().getQueryParameters().getFirst("result") != null
        && request.getUriInfo().getQueryParameters().getFirst("result").equals("xml")) {
      response.getHeaders().putSingle("content-type", "application/xml");
    } else {
      response.getHeaders().putSingle("content-type", "application/json");
    }
  }
}
