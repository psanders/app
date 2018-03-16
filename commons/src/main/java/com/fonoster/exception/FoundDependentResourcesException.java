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
public class FoundDependentResourcesException extends ApiException {

  private static final long serialVersionUID = 1L;

  public FoundDependentResourcesException() {}

  public FoundDependentResourcesException(String message) {
    super(message);
  }
}
