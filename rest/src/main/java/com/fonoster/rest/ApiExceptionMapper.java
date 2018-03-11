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
import com.jayway.jsonpath.InvalidPathException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Since("1.0")
@Provider
public class ApiExceptionMapper implements ExceptionMapper<Exception> {
  private com.fonoster.rest.Response response;

  public Response toResponse(Exception e) {
    Status status = Status.INTERNAL_SERVER_ERROR;
    String message;

    if (e instanceof DuplicateResourceException) status = Status.CONFLICT;
    if (e instanceof MissingDepencyException) status = Status.CONFLICT_1;
    if (e instanceof FoundDependentResourcesException) status = Status.CONFLICT_2;
    if (e instanceof InvalidPathException) status = Status.BAD_REQUEST;
    if (e instanceof InvalidParameterException) status = Status.BAD_REQUEST;
    if (e instanceof InvalidPaymentMethodException) status = Status.BAD_REQUEST;
    if (e instanceof InvalidPhoneNumberException) status = Status.BAD_REQUEST;
    if (e instanceof UserAlreadyExistException) status = Status.BAD_REQUEST;
    if (e instanceof UnauthorizedAccessException) status = Status.UNAUTHORIZED;
    if (e instanceof InsufficientFundsException) status = Status.INSUFFICIENT_FUNDS;
    if (e instanceof ResourceNotFoundException) status = Status.NOT_FOUND;

    message = Status.getMessage(status);

    if (status == Status.CONFLICT_1) {
      status = Status.CONFLICT;
      message = Status.getMessage(Status.CONFLICT_1);
    } if (status == Status.CONFLICT_2) {
      status = Status.CONFLICT;
      message = Status.getMessage(Status.CONFLICT_2);
    }

    response = new com.fonoster.rest.Response(status.getValue(), message);

    return Response.status(status.getValue()).entity(response).build();
  }
}
