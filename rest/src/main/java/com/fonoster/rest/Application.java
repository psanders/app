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
import com.fonoster.rest.filters.AuthFilter;
import com.fonoster.rest.filters.CORSFilter;
import com.fonoster.rest.filters.ContentTypeFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

@Since("1.0")
public class Application extends ResourceConfig {
  public Application() {
    packages("com.fonoster.rest");
    register(CORSFilter.class);
    register(LoggingFilter.class);
    register(ContentTypeFilter.class);
    register(AuthFilter.class);
  }
}
