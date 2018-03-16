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
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Since("1.0")
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement
public class Domain {
  @Id private URI id;
  @NotNull private DateTime created;
  @NotNull private DateTime modified;
  @NotNull @Reference @Valid private User user;
  @AssertFalse private boolean deleted;
  @NotNull private String apiVersion;
  @NotNull private String kind = getClass().getSimpleName();
  @NotNull private Map<String, String> metadata;
  @NotNull private Spec spec;

  // Must have no-argument constructor
  public Domain() {}

  public Domain(User user, String name, Spec.Context context) {
    this.id = context.domainUri;
    this.modified = new DateTime();
    this.created = new DateTime();
    this.user = user;
    this.deleted = false;
    this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    this.metadata = new HashMap();
    metadata.put("name", name);
    metadata.put("ref", this.id.toString());
    this.spec = new Spec();
    this.spec.setContext(context);
  }

  public URI getId() {
    return id;
  }

  public void setId(URI id) {
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
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

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
    @NotNull private Context context;

    public Context getContext() {
      return context;
    }

    public void setContext(Context context) {
      this.context = context;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Context {
      @NotNull private URI domainUri;
      private EgressPolicy egressPolicy;
      private AccessControlList accessControlList;

      public Context() {}

      public Context(URI domainUri) {
        this.domainUri = domainUri;
      }

      public Context(URI domainUri, String rule, String didRef) {
        this.domainUri = domainUri;
        this.egressPolicy = new EgressPolicy(rule, didRef);
      }

      public Context(
          URI domainUri, EgressPolicy egressPolicy, AccessControlList accessControlList) {
        this.domainUri = domainUri;
        this.egressPolicy = egressPolicy;
        this.accessControlList = accessControlList;
      }

      public URI getDomainUri() {
        return domainUri;
      }

      public void setDomainUri(URI domainUri) {
        this.domainUri = domainUri;
      }

      public EgressPolicy getEgressPolicy() {
        return egressPolicy;
      }

      public void setEgressPolicy(EgressPolicy egressPolicy) {
        this.egressPolicy = egressPolicy;
      }

      public AccessControlList getAccessControlList() {
        return accessControlList;
      }

      public void setAccessControlList(AccessControlList accessControlList) {
        this.accessControlList = accessControlList;
      }

      public static class EgressPolicy {
        @NotNull private String rule;
        @NotNull private String didRef;

        // Must have no-argument constructor
        public EgressPolicy() {}

        public EgressPolicy(String rule, String didRef) {
          this.setRule(rule);
          this.setDidRef(didRef);
        }

        public String getRule() {
          return rule;
        }

        public void setRule(String rule) {
          this.rule = rule;
        }

        public String getDidRef() {
          return didRef;
        }

        public void setDidRef(String didRef) {
          this.didRef = didRef;
        }
      }

      public static class AccessControlList {
        private List<String> allow;
        private List<String> deny;

        // Must have no-argument constructor
        public AccessControlList() {}

        public List<String> getAllow() {
          return allow;
        }

        public void setAllow(List<String> allow) {
          this.allow = allow;
        }

        public List<String> getDeny() {
          return deny;
        }

        public void setDeny(List<String> deny) {
          this.deny = deny;
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
