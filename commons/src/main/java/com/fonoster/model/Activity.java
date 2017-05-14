/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity
@Since("1.0")
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
public class Activity {
  @Id private ObjectId id;
  private DateTime created;
  @Reference @NotNull private User user;
  @NotNull private String description;
  @NotNull private Type type;
  @NotNull private String apiVersion;

  // Must have no-argument constructor
  public Activity() {}

  public Activity(User user, String description, Type type) {
    this.id = new ObjectId();
    this.user = user;
    this.description = description;
    this.created = new DateTime();
    this.type = type;
    this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
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

  //@XmlTransient
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  // Creates toString using reflection
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  @XmlType(name = "activityType")
  public static enum Type {
    SYS,
    BUG,
    INFO,
    PAYMENT,
    ALERT,
    SETTING;

    public static Type getByValue(String value) {
      if (value == null) return null;
      value = value.toUpperCase();
      switch (value) {
        case "SYS":
          return SYS;
        case "BUG":
          return BUG;
        case "INFO":
          return INFO;
        case "PAYMENT":
          return PAYMENT;
        case "ALERT":
          return ALERT;
        case "SETTING":
          return SETTING;
      }
      return null;
    }
  }
}
