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
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Since("1.0")
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement
public class Gateway {
  @NotNull @Id private ObjectId id;
  @NotNull private DateTime created;
  @NotNull private DateTime modified;
  @NotNull @Reference @Valid private ServiceProvider provider;
  @NotNull private String apiVersion;
  @NotNull private String kind = getClass().getSimpleName();
  @NotNull private Map<String, String> metadata;
  @NotNull private Spec spec;
  @AssertFalse private boolean deleted;

  // Must have no-argument constructor
  public Gateway() {}

  public Gateway(ServiceProvider provider, String name, Spec.RegService regService) {
    this.id = new ObjectId();
    this.modified = new DateTime();
    this.created = new DateTime();
    this.provider = provider;
    this.deleted = false;
    this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    this.metadata = new HashMap();
    metadata.put("name", name);
    metadata.put("ref", this.id.toString());
    this.spec = new Spec();
    this.spec.setRegService(regService);
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
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

  @JsonIgnore
  @XmlTransient
  public ServiceProvider getProvider() {
    return provider;
  }

  public void setProvider(ServiceProvider provider) {
    this.provider = provider;
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

  @JsonIgnore
  @XmlTransient
  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public static class Spec {
    @NotNull private RegService regService;

    public RegService getRegService() {
      return regService;
    }

    public void setRegService(RegService regService) {
      this.regService = regService;
    }

    public static class RegService {
      @NotNull private String host;
      @NotNull private String transport; // Default is UDP
      @NotNull private Credentials credentials;
      private List<String> registries;

      // Must have no-argument constructor
      public RegService() {}

      public RegService(String host, String transport, Credentials credentials) {
        this.host = host;
        this.transport = transport;
        this.credentials = credentials;
      }

      public String getHost() {
        return host;
      }

      public void setHost(String host) {
        this.host = host;
      }

      public String getTransport() {
        return transport;
      }

      public void setTransport(String transport) {
        this.transport = transport;
      }

      public Credentials getCredentials() {
        return credentials;
      }

      public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
      }

      public List<String> getRegistries() {
        return registries;
      }

      public void setRegistries(List<String> registries) {
        this.registries = registries;
      }

      public static class Credentials {
        @NotNull private String username;
        @NotNull private String secret;

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
    }
  }

  // Creates toString using reflection
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
