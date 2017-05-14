/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.services;

import com.fonoster.annotations.Since;
import com.fonoster.config.CommonsConfig;

@Since("1.0")
public class MailManager {
  private static final MailManager INSTANCE = new MailManager();
  private static final CommonsConfig config = CommonsConfig.getInstance();

  public static MailManager getInstance() {
    return INSTANCE;
  }

  /* public ClientResponse sendMsg(String from, String to, String subject, String msg) {
      Client client = Client.create();
      client.addFilter(new HTTPBasicAuthFilter("api", config.getMailgunApiKey()));
      WebResource webResource = client.resource(config.getMailgunResource());
      MultivaluedMapImpl formData = new MultivaluedMapImpl();
      formData.add("from", from);
      formData.add("to", to);
      formData.add("subject", subject);
      formData.add("text", msg);
      return webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
              post(ClientResponse.class, formData);
  }*/
}
