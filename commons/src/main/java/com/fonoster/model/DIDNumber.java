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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.AssertFalse;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement
public class DIDNumber {
  @Id private ObjectId id;
  @NotNull private DateTime created;
  @NotNull private DateTime modified;
  @Reference private App ingressApp; // Entry point for ingress calls

  @Reference
  private Account
      ingressAcct; // If set will be use to bill ingress calls, otherwise 'main' will be billed

  @NotNull @Reference private Gateway gateway;
  @NotNull private String kind;
  @NotNull private Map<String, Object> metadata;
  @NotNull private String apiVersion;
  @NotNull private Status status;
  @NotNull private Spec spec;
  @AssertFalse private boolean deleted;
  @AssertFalse private boolean preferred;
  @Reference private User user; // Renter

  // Must have no-argument constructor
  public DIDNumber() {}

  public DIDNumber(
      Gateway gateway,
      Spec.Location location,
      Map<String, String> geoInfo,
      Map<String, Boolean> tech) {
    this.id = new ObjectId();
    this.spec = new Spec();
    this.gateway = gateway;
    this.spec.setLocation(location);
    this.user = user;
    this.kind = "DID";
    this.setModified(new DateTime());
    this.setCreated(new DateTime());
    this.setDeleted(false);
    this.setPreferred(false);
    this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    this.metadata = new HashMap();
    this.setStatus(Status.ACTIVE);
    getMetadata().put("ref", this.id.toString());
    getMetadata().put("gwRef", gateway.getId().toString());
    getMetadata().put("geoInfo", geoInfo);
    getMetadata().put("tech", tech);
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  @JsonIgnore
  @XmlTransient
  public Gateway getGateway() {
    return gateway;
  }

  public void setGateway(Gateway gateway) {
    this.gateway = gateway;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Spec getSpec() {
    return spec;
  }

  public void setSpec(Spec spec) {
    this.spec = spec;
  }

  @JsonIgnore
  @XmlTransient
  public DateTime getCreated() {
    return created;
  }

  public void setCreated(DateTime created) {
    this.created = created;
  }

  @JsonIgnore
  @XmlTransient
  public DateTime getModified() {
    return modified;
  }

  public void setModified(DateTime modified) {
    this.modified = modified;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  @JsonIgnore
  @XmlTransient
  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public boolean isPreferred() {
    return preferred;
  }

  public void setPreferred(boolean preferred) {
    this.preferred = preferred;
  }

  @JsonIgnore
  @XmlTransient
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public App getIngressApp() {
    return ingressApp;
  }

  public void setIngressApp(App ingressApp) {
    this.ingressApp = ingressApp;
  }

  public Account getIngressAcct() {
    return ingressAcct;
  }

  public void setIngressAcct(Account ingressAcct) {
    this.ingressAcct = ingressAcct;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public static class Spec {
    @NotNull private Location location;

    public Location getLocation() {
      return location;
    }

    public void setLocation(Location location) {
      this.location = location;
    }

    public static class Location {
      @NotNull private String telUrl;
      @NotNull private String aorLink;

      // Must have no-argument constructor
      public Location() {}

      public Location(String telUrl, String aorLink) {
        this.telUrl = telUrl;
        this.aorLink = aorLink;
      }

      public String getTelUrl() {
        return telUrl;
      }

      public void setTelUrl(String telUrl) {
        this.telUrl = telUrl;
      }

      public String getAorLink() {
        return aorLink;
      }

      public void setAorLink(String aorLink) {
        this.aorLink = aorLink;
      }
    }
  }

  public enum Status {
    ACTIVE,
    AWAITING_REGISTRATION,
    EXPIRING_SOON,
    EXPIRED,
    TERMINATED
  }

  // Creates toString using reflection
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
