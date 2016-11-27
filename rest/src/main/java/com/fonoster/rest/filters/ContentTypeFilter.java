package com.fonoster.rest.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.ext.Provider;

@Provider
public class ContentTypeFilter implements ContainerResponseFilter {

    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        if (request.getQueryParameters().getFirst("result") != null &&
                request.getQueryParameters().getFirst("result").equals("xml")) {
            response.getHttpHeaders().putSingle("content-type", "application/xml");
        } else {
            response.getHttpHeaders().putSingle("content-type", "application/json");
        }

        return response;
    }
}