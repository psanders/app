package com.fonoster.model.services;

import com.fonoster.annotations.Since;
import com.sun.xml.txw2.annotation.XmlElement;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;

@Since("1.0")
@XmlElement
@Entity
@Embedded
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {
    @NotNull
    private String name;
    @NotNull
    private Type type;

    public Service() {}

    public Service(String name, Type type) {
        setName(name);
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        TTS,
        SST,
        TRANSCRIPT
    }
}
