/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.exception;

public class UnauthorizedAccessException extends ApiException {

  	private static final long serialVersionUID = 1L;

	public UnauthorizedAccessException() {
        super("Unauthorized access.");
    }

    public UnauthorizedAccessException(String msg) {
        super(msg);
    }
}
