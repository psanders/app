package com.fonoster.exception;

public class InsufficientFundsException extends ApiException {

	private static final long serialVersionUID = 1L;

	public InsufficientFundsException() {
        super("Insufficient funds.");
    }

    public InsufficientFundsException(String msg) {
        super(msg);
    }
}
