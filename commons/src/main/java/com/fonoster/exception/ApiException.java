/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.exception;

public class ApiException extends Exception {

 	private static final long serialVersionUID = 1L;

	public ApiException() {
    }

    public ApiException(String msg) {
        super(msg);
    }
}
