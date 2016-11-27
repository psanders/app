package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.sun.xml.txw2.annotation.XmlElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.validation.constraints.NotNull;

@Since("1.0")
@XmlElement
@Entity
@Embedded
@JsonIgnoreProperties(ignoreUnknown = true)
public class Script {
    @NotNull
    @Id
    private String name;
    @NotNull
    private String source;
    @NotNull
    private Type type;
    @NotNull
    private String apiVersion;

    public Script() {
    }

    public Script(String name) {
        this.setType (Type.JAVASCRIPT);
        this.name = name;
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    public Script(String name, Type type) {
        this.setType (type);
        this.name = name;
        this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        JAVASCRIPT
    }
}
