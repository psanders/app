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
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Since("1.0")
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@XmlRootElement
public class ServiceProvider {
  @Id private ObjectId id;
  @NotNull private DateTime created;
  @NotNull private DateTime modified;
  @NotNull private String name;
  @NotNull private String address;
  @NotNull private String contactNumber;
  @NotNull private String apiVersion;

  // Must have no-argument constructor
  public ServiceProvider() {}

  public ServiceProvider(String name, String address, String contactNumber) {
    this.name = name;
    this.address = address;
    this.contactNumber = contactNumber;
    this.created = DateTime.now();
    this.modified = DateTime.now();
    this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
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

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }
}
