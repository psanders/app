/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
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