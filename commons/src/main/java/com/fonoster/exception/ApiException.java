/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.exception;

import com.fonoster.annotations.Since;

@Since("1.0")
public class ApiException extends Exception {

  private static final long serialVersionUID = 1L;

  public ApiException() {
    super("Service is temporarily unavailable, please try again later");
  }

  public ApiException(String message) {
    super(message);
  }
}
