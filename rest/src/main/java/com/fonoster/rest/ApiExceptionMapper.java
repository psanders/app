package com.fonoster.rest;

import com.fonoster.exception.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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