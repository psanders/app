package com.fonoster.exception;

public class InvalidPhoneNumberException extends ApiException {

   	private static final long serialVersionUID = 1L;

	public InvalidPhoneNumberException() {
        super("Invalid phone number.");
    }

    public InvalidPhoneNumberException(String msg) {
        super(msg);
    }
}
