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
import com.fonoster.exception.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Since("1.0")
@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {
  private com.fonoster.rest.Response response;

  public Response toResponse(ApiException e) {
    int status = 400;

    if (e instanceof InvalidParameterException) status = 400;
    if (e instanceof InvalidPaymentMethodException) status = 400;
    if (e instanceof InvalidPhoneNumberException) status = 400;
    if (e instanceof UserAlreadyExistException) status = 400;
    if (e instanceof UnauthorizedAccessException) status = 401;
    if (e instanceof InsufficientFundsException) status = 402;
    if (e instanceof ResourceNotFoundException) status = 404;

    response = new com.fonoster.rest.Response(status, e.getMessage());
    return Response.status(status).entity(response).build();
  }
}
