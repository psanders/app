package com.fonoster.rest.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ContentTypeFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        if (request.getUriInfo().getQueryParameters().getFirst("result") != null &&
                request.getUriInfo().getQueryParameters().getFirst("result").equals("xml")) {
            response.getHeaders().putSingle("content-type", "application/xml");
        } else {
            response.getHeaders().putSingle("content-type", "application/json");
        }
    }
}