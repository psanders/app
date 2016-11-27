package com.fonoster.exception;

public class InvalidParameterException extends ApiException {

    private static final long serialVersionUID = 1L;

	public InvalidParameterException() {
        super("Invalid parameter.");
    }

    public InvalidParameterException(String msg) {
        super(msg);
    }
}
