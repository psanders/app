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

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;

@Since("1.0")
@RolesAllowed({"USER"})
@Path("/accounts/{accountId}/domains")
public class DomainsService {
  // Coming soon :)
}
