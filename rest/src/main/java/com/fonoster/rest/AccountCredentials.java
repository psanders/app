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
public class AccountCredentials {
  private String accountId;
  private String token;

  // Must have no-argument constructor
  public AccountCredentials() {
  }

  public AccountCredentials(String accountId, String token) {
    this.accountId = accountId;
    this.token = token;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
