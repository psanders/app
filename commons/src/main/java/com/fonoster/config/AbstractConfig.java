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
import java.io.File;
import java.io.InputStream;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Since("1.0")
public abstract class AbstractConfig {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractConfig.class);
  private PropertiesConfiguration config;

  public AbstractConfig(String file) {
    init(file);
  }

  public void init(String file) {
    if (new File("/etc/fonoster.conf").exists()) {
      String filePath = "/etc/fonoster.conf";

      try {
        config = new PropertiesConfiguration(new File(filePath));
      } catch (ConfigurationException e) {
        LOG.error("Unable to load file: " + filePath);
      }
    } else {
      LOG.warn("Using " + file + " from classpath should only happen in development env.");
      ClassLoader classLoader = getClass().getClassLoader();
      InputStream is = classLoader.getResourceAsStream(file);
      try {
        config = new PropertiesConfiguration();
        config.load(is);
      } catch (ConfigurationException e) {
        LOG.error(
            "Unable to load config file: " + file + " exception message -> " + e.getMessage());
      }
    }
  }

  public PropertiesConfiguration getConfig() {
    return config;
  }
}
