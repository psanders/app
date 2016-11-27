package com.fonoster.services;

import com.fonoster.config.CommonsConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;

public class MailManager {
    private static final MailManager INSTANCE = new MailManager();
    private static final CommonsConfig config = CommonsConfig.getInstance();

    public static MailManager getInstance() {
        return INSTANCE;
    }

    public ClientResponse sendMsg(String from, String to, String subject, String msg) {
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
    }
}
