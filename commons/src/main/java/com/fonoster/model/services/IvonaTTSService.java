/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.model.services;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;
import javax.validation.constraints.NotNull;

@Since("1.0")
public class IvonaTTSService extends Service {

  @NotNull private String secretKey;
  @NotNull private String accessKey;
  @NotNull private String apiVersion;

  public IvonaTTSService() {}

  public IvonaTTSService(String name) {
    super(name, Type.TTS);
    this.setApiVersion(CommonsConfig.getInstance().getCurrentVersion());
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }
}
