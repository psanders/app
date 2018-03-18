package com.fonoster.model;

import com.fonoster.config.CommonsConfig;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

public class SystemUser {
  @NotNull private DateTime created;
  @NotNull private DateTime modified;
  @NotNull private String apiVersion;
  @NotNull private String kind = "User";
  @NotNull private Map<String, String> metadata;
  @NotNull private Spec spec;

  public SystemUser(String name, Spec spec) {
    this.modified = new DateTime();
    this.created = new DateTime();
    this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    this.metadata = new HashMap();
    metadata.put("name", name);
    metadata.put("ref", new ObjectId().toString());
    this.spec = spec;
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
    @NotNull private Credentials credentials;

    public Credentials getCredentials() {
      return credentials;
    }

    public void setCredentials(Credentials credentials) {
      this.credentials = credentials;
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
}
