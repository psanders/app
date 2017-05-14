package com.fonoster.rest;

import com.fonoster.annotations.Since;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import javax.xml.bind.annotation.XmlElement;

@Since("1.0")
public class Response {
    private int status;
    private String message;

    public Response() {
    }

    public Response(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @XmlElement
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @XmlElement
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Creates toString using reflection
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
