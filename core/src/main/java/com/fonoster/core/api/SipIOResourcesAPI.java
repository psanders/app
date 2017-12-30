/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
package com.fonoster.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fonoster.annotations.Since;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.ResourceNotFoundException;
import com.fonoster.model.Agent;
import com.fonoster.model.DIDNumber;
import com.fonoster.model.Domain;
import com.fonoster.model.Gateway;
import com.google.common.base.Strings;
import com.jayway.jsonpath.JsonPath;
import org.mongodb.morphia.Datastore;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Only for admin account (Including Sip I/O integration)
 */
@Since("1.0")
public class SipIOResourcesAPI {
    private ObjectMapper mapper = new ObjectMapper();
    private static SipIOResourcesAPI instance;
    private static Datastore ds;

    private SipIOResourcesAPI() {
    }

    public static SipIOResourcesAPI getInstance() throws ApiException {
        if (instance == null || ds == null) {
            try {
                ds = DBManager.getInstance().getDS();
                instance = new SipIOResourcesAPI();
            } catch (UnknownHostException e) {
                throw new ApiException();
            }
        }
        return instance;
    }

    public List<Domain> getDomains(String f) throws ApiException {
        String filter = "*";
        String jsonInString;

        if (!Strings.isNullOrEmpty(f) && !f.equals("*")) {
            filter = "*.[?(" + f + ")]";
        }

        List<Domain> domains = ds.createQuery(Domain.class).field("deleted").equal(false).asList();
        List<Domain> result;

        try {
            jsonInString = mapper.writeValueAsString(domains);
            result = JsonPath.parse(jsonInString).read(filter);
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

        if(result.isEmpty()) throw new ResourceNotFoundException();

        return result;
    }

    public Domain getDomainByUri(URI domainUri) throws ResourceNotFoundException {
        Domain result = ds.createQuery(Domain.class).field("id").equal(domainUri).field("deleted").equal(false).get();
        if (result == null) throw new ResourceNotFoundException();
        return result;
    }

    // Only for admin account (Including Sip I/O integration)
    public List<Agent> getAgents(URI domainUri, String f) throws ApiException {
        String filter = "*";
        String jsonInString;

        if (!Strings.isNullOrEmpty(f) && !f.equals("*")) {
            filter = "*.[?(" + f + ")]";
        }

        List<Agent> agents = ds.createQuery(Agent.class).field("deleted").equal(false).asList();

        if (domainUri != null) {
            agents = agents.stream().filter(agent -> hasDomain(agent.getSpec().getDomains(), domainUri)).collect(Collectors.toList());
        }

        List<Agent> result;

        try {
            jsonInString = mapper.writeValueAsString(agents);
            result = JsonPath.parse(jsonInString).read(filter);
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

        if (result.isEmpty()) throw new ResourceNotFoundException();

        return result;
    }

    public List<DIDNumber> getDIDNumbers(String f) throws ApiException {
        String filter = "*";
        String jsonInString;

        if (!Strings.isNullOrEmpty(f) && !f.equals("*")) {
            filter = "*.[?(" + f + ")]";
        }

        List<DIDNumber> didNumbers = ds.createQuery(DIDNumber.class).field("deleted").equal(false).asList();
        List<DIDNumber> result;

        try {
            jsonInString = mapper.writeValueAsString(didNumbers);
            result = JsonPath.parse(jsonInString).read(filter);
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

        if(result.isEmpty()) throw new ResourceNotFoundException();

        return result;
    }

    public List<Gateway> getGateways(String f) throws ApiException {
        String filter = "*";
        String jsonInString;

        if (!Strings.isNullOrEmpty(f) && !f.equals("*")) {
            filter = "*.[?(" + f + ")]";
        }

        List<Gateway> gateways = ds.createQuery(Gateway.class).field("deleted").equal(false).asList();
        List<Gateway> result;

        try {
            jsonInString = mapper.writeValueAsString(gateways);
            result = JsonPath.parse(jsonInString).read(filter);
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

        if(result.isEmpty()) throw new ResourceNotFoundException();

        return result;
    }

    private boolean hasDomain(List<URI> domains, URI domainUri) {
        for (URI dUri : domains) {
            if (domainUri.equals(dUri)) {
                return true;
            }
        }
        return false;
    }
}
