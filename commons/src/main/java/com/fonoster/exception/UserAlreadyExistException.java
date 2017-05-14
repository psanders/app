package com.fonoster.exception;

public class UserAlreadyExistException extends ApiException {

  	private static final long serialVersionUID = 1L;

	public UserAlreadyExistException() {
        super("An user with this username already exist.");
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
