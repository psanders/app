/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.model;

import com.fonoster.annotations.Since;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Since("1.0")
@XmlRootElement
public class Response {
    private int code;
    private String message;

    public Response() {
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @XmlElement
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
