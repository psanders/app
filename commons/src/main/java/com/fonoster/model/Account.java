/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Since("1.0")
@Entity
@XmlRootElement
public class Account {
  @Id private ObjectId id;
  private DateTime created;
  private DateTime modified;

  @NotNull @Reference private User user;

  @NotNull private String name;
  @NotNull private String token;
  @Reference private Account parentAccount;
  private boolean deleted;
  @NotNull private String apiVersion;

  // Must have no-argument constructor
  public Account() {}

  public Account(User user, String name) {
    this.id = new ObjectId();
    this.name = name;
    this.token = UUID.randomUUID().toString().replaceAll("-", "");
    this.modified = new DateTime();
    this.created = new DateTime();
    this.user = user;
    this.deleted = false;
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

  public DateTime getModified() {
    return modified;
  }

  public void setModified(DateTime modified) {
    this.modified = modified;
  }

  @JsonIgnore
  @XmlTransient
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Account getParentAccount() {
    return parentAccount;
  }

  public void setParentAccount(Account parentAccount) {
    this.parentAccount = parentAccount;
  }

  // Can only be deleted if is a sub-account
  @JsonIgnore
  @XmlTransient
  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public boolean isSubAccount() {
    return parentAccount != null;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  // I know you don't like this, because you are a JavaPurist :)
  public void regenerateToken() {
    this.token = UUID.randomUUID().toString().replaceAll("-", "");
  }

  // Creates toString using reflection
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
