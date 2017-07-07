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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

@Since("1.0")
public class MailManager {
  private static final MailManager INSTANCE = new MailManager();
  private static final CommonsConfig config = CommonsConfig.getInstance();

  public static MailManager getInstance() {
    return INSTANCE;
  }

  public Response sendMsg(String from, String to, String subject, String message) {
    ClientConfig clientConfigMail = new ClientConfig();
    Client clientMail = ClientBuilder.newClient(clientConfigMail);
    clientMail.register(HttpAuthenticationFeature.basic("api", config.getMailgunApiKey()));
    WebTarget targetMail = clientMail.target(config.getMailgunResource());
    Form formData = new Form();
    formData.param("from", from);
    formData.param("to", to);
    formData.param("subject", subject);
    formData.param("text", message);
    Response response =
        targetMail
            .request()
            .post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    return response;
  }
}
