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
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.Map;

@Since("1.0")
@Entity
@XmlRootElement
public class Peer {
  @Id private ObjectId id;
  @NotNull private DateTime created;
  @NotNull private DateTime modified;
  @AssertFalse private boolean deleted;
  @NotNull private String apiVersion;
  @NotNull private String kind = getClass().getSimpleName();
  @NotNull private Map<String, String> metadata;
  @NotNull private Spec spec;

  // Must have no-argument constructor
  public Peer() {}

  public Peer(String name, Spec spec) {
    this.id = new ObjectId();
    this.modified = new DateTime();
    this.created = new DateTime();
    this.deleted = false;
    this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    this.metadata = new HashMap();
    metadata.put("name", name);
    metadata.put("ref", this.id.toString());
    this.spec = spec;
  }

  @JsonIgnore
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

  // Can only be deleted if is a sub-account
  @JsonIgnore
  @XmlTransient
  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  public Spec getSpec() {
    return spec;
  }

  public void setSpec(Spec spec) {
    this.spec = spec;
  }

  public static class Spec {
    private String device;
    private String contactAddress;
    @NotNull private Credentials credentials;

    public Credentials getCredentials() {
      return credentials;
    }

    public void setCredentials(Credentials credentials) {
      this.credentials = credentials;
    }

    public String getDevice() {
      return device;
    }

    public void setDevice(String device) {
      this.device = device;
    }

    public String getContactAddress() {
      return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
      this.contactAddress = contactAddress;
    }

    public static class Credentials {
      @NotNull private String username;
      @NotNull private String secret;

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

  }

  // Creates toString using reflection
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
