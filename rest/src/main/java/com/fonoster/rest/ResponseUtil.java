/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.rest;

import com.fonoster.model.Response;

public class ResponseUtil {
    // Common responses
    public static int UNAUTHORIZED = 401;
    public static int BAD_REQUEST = 400;
    public static int NOT_FOUND = 404;
    public static int OK = 200;
    public static int CREATED = 201;
    public static int NOT_IMPLEMENTED = 501;
    private static String DEFAULT_UNAUTHORIZED_MESSAGE = "Unauthorized.";
    private static String DEFAULT_BAD_REQUEST_MESSAGE = "Bad request. Please ensure proper input.";
    private static String DEFAULT_NOT_FOUND_MESSAGE = "Resource not found.";
    private static String DEFAULT_OK_MESSAGE = "Success.";
    private static String DEFAULT_CREATED_MESSAGE = "Successfully created.";
    private static String DEFAULT_NOT_IMPLEMENTED = "Not implemented.";

    static public javax.ws.rs.core.Response getResponse(int code) {

        Response response = null;

        switch (code) {
            case 501:
                response = new Response(code, DEFAULT_NOT_IMPLEMENTED);
                break;
            case 401:
                response = new Response(code, DEFAULT_UNAUTHORIZED_MESSAGE);
                break;
            case 400:
                response = new Response(code, DEFAULT_BAD_REQUEST_MESSAGE);
                break;
            case 404:
                response = new Response(code, DEFAULT_NOT_FOUND_MESSAGE);
                break;
            case 200:
                response = new Response(code, DEFAULT_OK_MESSAGE);
                break;
            case 201:
                response = new Response(code, DEFAULT_CREATED_MESSAGE);
                break;
        }

        assert response != null;

        return javax.ws.rs.core.Response.status(response.getCode()).entity(response).build();
    }

    static public javax.ws.rs.core.Response getResponse(int code, String message) {
        Response response = new Response(code, message);
        return javax.ws.rs.core.Response.status(response.getCode()).entity(response).build();
    }
}
