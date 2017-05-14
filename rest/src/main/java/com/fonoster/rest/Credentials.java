/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.rest;

import com.fonoster.annotations.Since;

import javax.xml.bind.annotation.XmlRootElement;

@Since("1.0")
@XmlRootElement
public class Credentials {
  private String username;
  private String secret;

  // Must have no-argument constructor
  public Credentials() {}

  public Credentials(String username, String secret) {
    this.username = username;
    this.secret = secret;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
