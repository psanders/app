package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Entity
@Since("1.0")
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity {
    @Id
    private ObjectId id;
    private DateTime created;
    @Reference
    @NotNull
    private User user;
    @NotNull
    private String description;
    @NotNull
    private Type type;
    @NotNull
    private String apiVersion;

    public Activity() {
    }

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

    @XmlTransient
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

        static public Type getByValue(String value) {
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
