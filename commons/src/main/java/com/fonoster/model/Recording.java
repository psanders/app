/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import com.fonoster.exception.ApiException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Since("1.0")
@Entity
@XmlRootElement
public class Recording {
  @Id private ObjectId id;
  @NotNull @Reference private Account account;
  @NotNull private DateTime created;
  @NotNull private DateTime modified;
  @NotNull private URI uri;
  @Reference @NotNull private CallDetailRecord callDetailRecord;
  private float duration;
  @NotNull private String apiVersion;

  // Must have no-argument constructor
  public Recording() {}

  public Recording(CallDetailRecord callDetailRecord) throws ApiException {
    id = new ObjectId();
    this.created = DateTime.now();
    this.modified = DateTime.now();
    this.callDetailRecord = callDetailRecord;
    this.account = callDetailRecord.getAccount();
    this.apiVersion = CommonsConfig.getInstance().getCurrentVersion();
    try {
      this.uri = CommonsConfig.getInstance().getRecordingURI(this);
    } catch (URISyntaxException e) {
      throw new ApiException(e.getMessage());
    }
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  @XmlElement(name = "accountId")
  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
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

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  @XmlElement(name = "callId")
  public CallDetailRecord getCallDetailRecord() {
    return callDetailRecord;
  }

  public void setCallDetailRecord(CallDetailRecord callDetailRecord) {
    this.callDetailRecord = callDetailRecord;
  }

  public float getDuration() {
    return duration;
  }

  public void setDuration(float duration) {
    this.duration = duration;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public static enum AudioFormat {
    WAV,
    MP3;

    public static AudioFormat getByValue(String value) {
      if (value == null) return null;
      value = value.toUpperCase();
      switch (value) {
        case "WAV":
          return WAV;
        case "MP3":
          return MP3;
      }
      return null;
    }
  }
}
