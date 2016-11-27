package com.fonoster.exception;

public class ResourceNotFoundException extends ApiException {

  	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException() {
        super("Resource not found.");
    }

    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
