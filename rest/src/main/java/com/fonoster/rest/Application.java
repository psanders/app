package com.fonoster.rest;

import com.fonoster.rest.filters.AuthFilter;
import com.fonoster.rest.filters.ContentTypeFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig  {
    public Application()  {
        packages("com.fonoster.rest");
        register(LoggingFilter.class);
        register(ContentTypeFilter.class);
        register(AuthFilter.class);
    }
}