/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.validation.constraints.NotNull;

@Since("1.0")
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Broadcast {
    @Id
    private ObjectId id;
    private String message;
    private DateTime created;
    @NotNull
    private String apiVersion;

    public Broadcast() {
        created = new DateTime();
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    // Creates toString using reflection
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
