/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.config;

import com.fonoster.annotations.Since;
import com.fonoster.model.CallDetailRecord;
import com.fonoster.model.Recording;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.configuration.PropertiesConfiguration;

@Since("1.0")
public class CommonsConfig extends AbstractConfig {
  private static final CommonsConfig INSTANCE = new CommonsConfig();
  private PropertiesConfiguration config;

  private CommonsConfig() {
    super("fonoster.conf");
    config = super.getConfig();
  }

  public static CommonsConfig getInstance() {
    return INSTANCE;
  }

  public String getCurrentVersion() {
    return config.getString("apiVersion");
  }

  public URI getCdrURI(CallDetailRecord callDetailRecord) throws URISyntaxException {
    StringBuilder sb = new StringBuilder("/");
    sb.append(callDetailRecord.getApiVersion());
    sb.append("/accounts/");
    sb.append(callDetailRecord.getAccount().getId());
    sb.append("/calls/");
    sb.append(callDetailRecord.getId());
    return new URI(sb.toString());
  }

  public URI getCallRecordingsURI(CallDetailRecord callDetailRecord) throws URISyntaxException {
    StringBuilder sb = new StringBuilder("/");
    sb.append(callDetailRecord.getApiVersion());
    sb.append("/accounts/");
    sb.append(callDetailRecord.getAccount().getId());
    sb.append("/calls/");
    sb.append(callDetailRecord.getId());
    sb.append("/recordings");
    return new URI(sb.toString());
  }

  public URI getRecordingURI(Recording recording) throws URISyntaxException {
    StringBuilder sb = new StringBuilder("/");
    sb.append(recording.getApiVersion());
    sb.append("/accounts/");
    sb.append(recording.getAccount().getId());
    sb.append("/recordings/");
    sb.append(recording.getId());
    return new URI(sb.toString());
  }

  public String getMailgunApiKey() {
    return config.getString("mailgun.apiKey");
  }

  public String getMailgunResource() {
    return config.getString("mailgun.resource");
  }

  public String getAdminMail() {
    return config.getString("email.admin");
  }

  public String getDevMail() {
    return config.getString("email.dev");
  }

  public String getTeamMail() {
    return config.getString("email.team");
  }

  public String getRecordingsPath() {
    return config.getString("recordingsPath");
  }

  public String getTTSStorePath() {
    return config.getString("ttsStorePath");
  }

  public String getAdminUsername() {
    return config.getString("admin.username");
  }

  public String getAdminSecret() {
    return config.getString("admin.secret");
  }
}
