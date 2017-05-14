package com.fonoster.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Since("1.0")
@Entity
public class Agent {
    @Id
    private ObjectId id;
    @NotNull
    private DateTime created;
    @NotNull
    private DateTime modified;
    @NotNull
    @Reference
    @Valid
    private User user;
    @AssertFalse
    private boolean deleted;
    @NotNull
    private String apiVersion;
    @NotNull
    private String kind = getClass().getSimpleName();
    @NotNull
    private Map<String, String> metadata;
    @NotNull
    private Spec spec;

    public Agent() {
    }

    public Agent(User user, String name, Spec.Credentials credentials) {
        this.id = new ObjectId();
        this.modified = new DateTime();
        this.created = new DateTime();
        this.user = user;
        this.deleted = false;
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
        this.metadata = new HashMap();
        metadata.put("name", name);
        this.spec = new Spec();
        this.spec.setCredentials(credentials);
    }

    @JsonIgnore
    @XmlTransient
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
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        @NotNull
        private Credentials credentials;
        private List<String> domains;

        public Credentials getCredentials() {
            return credentials;
        }

        public void setCredentials(Credentials credentials) {
            this.credentials = credentials;
        }

        public List<String> getDomains() {
            return domains;
        }

        public void setDomains(List<String> domains) {
            this.domains = domains;
        }

        public static class Credentials {
            @NotNull
            private String username;
            private String secret;

            public Credentials() {
            }

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
