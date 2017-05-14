/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
/*
 *Copyright (C) 2014 PhonyTive LLC
 *http://fonoster.com
 *
 *This file is part of Fonoster
 */
package com.fonoster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Since("1.0")
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
public class Broadcast {
  @Id private ObjectId id;
  private String message;
  private DateTime created;
  @NotNull private String apiVersion;

  // Must have no-argument constructor
  public Broadcast() {
    created = new DateTime();
    this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public DateTime getCreated() {
    return created;
  }

  public void setCreated(DateTime created) {
    this.created = created;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  // Creates toString using reflection
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
