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
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Since("1.0")
@XmlRootElement
public class Response {
  private int status;
  private String message;

  // Must have no-argument constructor
  public Response() {}

  public Response(int status, String message) {
    this.status = status;
    this.message = message;
  }

  @XmlElement
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @XmlElement
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  // Creates toString using reflection
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
