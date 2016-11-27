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
import org.mongodb.morphia.annotations.*;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@Since("1.0")
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@Indexes(
    @Index(fields = {
            @Field("prefix"), @Field("provider")
        },
        options=@IndexOptions(unique=true, dropDups=true)
    )
)
public class Rate {
    @Id
    private ObjectId id;
    @Reference
    @NotNull
    private ServiceProvider provider;
    @NotNull
    private String prefix;
    private String description;
    @NotNull
    private BigDecimal buying;
    @NotNull
    private BigDecimal selling;
    @NotNull
    private DateTime created;
    @NotNull
    private DateTime modified;
    @NotNull
    private String apiVersion;

    public Rate() {}

    public Rate(ServiceProvider provider, String prefix, String description) {
        this.setProvider(provider);
        this.setPrefix(prefix);
        this.setDescription(description);
        this.setCreated(new DateTime());
        this.setModified(new DateTime());
        this.setApiVersion (CommonsConfig.getInstance().getCurrentVersion());
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBuying() {
        return buying;
    }

    public void setBuying(BigDecimal buying) {
        this.buying = buying;
    }

    public BigDecimal getSelling() {
        return selling;
    }

    public void setSelling(BigDecimal selling) {
        this.selling = selling;
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

    public ServiceProvider getProvider() {
        return provider;
    }

    public void setProvider(ServiceProvider provider) {
        this.provider = provider;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
}
