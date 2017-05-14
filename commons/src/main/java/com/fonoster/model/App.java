package com.fonoster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@Entity
@Since("1.0")
@JsonIgnoreProperties(ignoreUnknown = true)
public class App {
    @Id
    private ObjectId id;
    @NotNull
    private DateTime created;
    @NotNull
    private DateTime modified;
    @Reference
    @NotNull
    private User user;
    @NotNull
    private String name;
    private boolean starred;
    @NotNull
    private Status status;
    @NotNull
    private String apiVersion;
    private List<Script> scripts;

    public App() {
        name = "";
        status = Status.NORMAL;
        starred = false;
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
        this.scripts = new ArrayList<>();
    }

    public App(User user, String name) {
        this.id = new ObjectId();
        this.user = user;
        this.name = name;
        this.created = DateTime.now();
        this.modified = DateTime.now();
        this.status = Status.NORMAL;
        this.starred = false;
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
        this.scripts = new ArrayList<>();
    }

    public App(User user, String name, String script) {
        this.id = new ObjectId();
        this.user = user;
        this.name = name;
        this.created = DateTime.now();
        this.modified = DateTime.now();
        this.status = Status.NORMAL;
        this.starred = false;
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
        this.scripts = new ArrayList<>();
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

    // My attempt to fix the serialization issue
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

    public DateTime getModified() {
        return modified;
    }

    public void setModified(DateTime modified) {
        this.modified = modified;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

    @XmlType(name = "appStatus")
    public static enum Status {
        NORMAL,
        ARCHIVED,
        DELETED,
        TRASH;

        static public Status getByValue(String value) {
            if (value == null) return null;
            value = value.toUpperCase();
            switch (value) {
                case "NORMAL":
                    return NORMAL;
                case "ARCHIVED":
                    return ARCHIVED;
                case "DELETED":
                    return DELETED;
                case "TRASH":
                    return TRASH;
            }
            return null;
        }
    }

    // Creates toString using reflection
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
