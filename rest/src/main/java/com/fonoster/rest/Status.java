package com.fonoster.rest;

public enum Status {
    OK(200),
    CREATED(201),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    INSUFFICIENT_FUNDS(402),
    NOT_FOUND(404),
    NOT_SUPPORTED(405),
    CONFLICT(409),
    CONFLICT_1(4091),
    CONFLICT_2(4092),
    INTERNAL_SERVER_ERROR(500);

    private int value;

    Status(int value) {
        this.value = value;
    }

    public static String getMessage(Status status) {
        String statusStr = status.toString().toUpperCase();

        switch (statusStr) {
            case "OK":
                return "Successful request";
            case "CREATED":
                return "Created";
            case "BAD_REQUEST":
                return "Bad request";
            case "UNAUTHORIZED":
                return "Unauthorized";
            case "NOT_FOUND":
                return "Not found";
            case "NOT_SUPPORTED":
                return "Operation not supported by data source provider";
            case "CONFLICT":
                return "An attempt was made to create an object that already exists";
            case "CONFLICT_1":
                return "Found one or more unfulfilled dependencies";
            case "CONFLICT_2":
                return "Found one or more dependent objects";
            case "INTERNAL_SERVER_ERROR":
                return "Internal Server Error";
        }
        return "The execution of the service failed";
    }

    public int getValue() {
        return value;
    }
}
