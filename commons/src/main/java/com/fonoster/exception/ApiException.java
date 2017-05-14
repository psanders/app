package com.fonoster.exception;

public class ApiException extends Exception {

 	private static final long serialVersionUID = 1L;

	public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }
}
