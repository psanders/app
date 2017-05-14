/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fonoster.annotations.Since;
import javax.validation.constraints.NotNull;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

@Since("1.0")
@Entity
@Embedded
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {
  @NotNull private String name;
  @NotNull private Type type;

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
