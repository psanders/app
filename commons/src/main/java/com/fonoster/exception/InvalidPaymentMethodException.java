package com.fonoster.exception;

public class InvalidPaymentMethodException extends ApiException {

	private static final long serialVersionUID = 1L;

	public InvalidPaymentMethodException() {
        super("Invalid payment method.");
    }

    public InvalidPaymentMethodException(String message) {
        super(message);
    }
}
