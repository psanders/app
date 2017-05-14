/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
package com.fonoster.exception;

import com.fonoster.annotations.Since;

@Since("1.0")
public class InvalidParameterException extends ApiException {

    private static final long serialVersionUID = 1L;

	public InvalidParameterException() {
        super("Invalid parameter.");
    }

    public InvalidParameterException(String parameter) {
        super("Invalid parameter '" + parameter + "'");
    }
}
