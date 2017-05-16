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
public class UserCredentials {
  private String email;
  private String password;

  // Must have no-argument constructor
  public UserCredentials() {
  }

  public UserCredentials(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setToken(String password) {
    this.password = password;
  }
}
