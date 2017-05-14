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
public class SequenceException extends Exception {

  	private static final long serialVersionUID = 1L;

	public SequenceException() {
        super("Invalid sequence. Call get() or post() first.");
    }

    public SequenceException(String message) {
        super(message);
    }
}
