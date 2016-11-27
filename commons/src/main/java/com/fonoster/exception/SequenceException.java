package com.fonoster.exception;

public class SequenceException extends Exception {

  	private static final long serialVersionUID = 1L;

	public SequenceException() {
        super("Invalid sequence. Call get() or post() first.");
    }

    public SequenceException(String msg) {
        super(msg);
    }
}
