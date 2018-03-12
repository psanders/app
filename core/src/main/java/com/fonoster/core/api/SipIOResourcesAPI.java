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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fonoster.annotations.Since;
import com.fonoster.exception.*;
import com.fonoster.model.*;
import com.google.common.base.Strings;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * Only for admin account and Sip I/O integration
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

    public String insert(JSONObject jsonObj) throws ApiException {
        String kind = jsonObj.get("kind").toString();
        ObjectId id;

        try {
            switch (kind) {
                case "Gateway":
                    Gateway gateway = mapper.readValue(jsonObj.toJSONString(), Gateway.class);
                    String spIdStr = gateway.getMetadata().get("spId");

                    if(spIdStr == null || !ObjectId.isValid(spIdStr)) {
                        throw new InvalidParameterException("metadata.spId must be a valid ObjectId");
                    }

                    ObjectId spId = new ObjectId(spIdStr);
                    ServiceProvider sp = DIDNumbersAPI.getInstance().getServiceProviderById(spId);

                    if(sp == null) {
                        throw new MissingDepencyException();
                    }

                    System.out.println("username: " + gateway.getSpec().getRegService().getCredentials().getUsername());
                    System.out.println("host: " + gateway.getSpec().getRegService().getHost());

                    // Look for GW using this reference
                    List l = find(Gateway.class, "@.spec.regService.credentials.username=='"
                            + gateway.getSpec().getRegService().getCredentials().getUsername()
                            + "' && @.spec.regService.host=='" + gateway.getSpec().getRegService().getHost() + "'");

                    if(l.size() > 0) {
                        throw new DuplicateResourceException();
                    }

                    return GatewaysAPI.getInstance().createGateway(sp, gateway.getMetadata().get("name")
                            , gateway.getSpec().getRegService()).getId().toString();
                case "DID":
                    DIDNumber d = mapper.readValue(jsonObj.toJSONString(), DIDNumber.class);

                    Gateway gw = GatewaysAPI.getInstance()
                            .getGatewayById(new ObjectId(d.getMetadata().get("gwRef").toString()));

                    if(gw == null) {
                        throw new MissingDepencyException();
                    }

                    DIDNumber didNumber = new DIDNumber(gw, d.getSpec().getLocation(),
                        (Map<String, String>)d.getMetadata().get("geoInfo"),
                        (Map<String, Boolean>)d.getMetadata().get("tech"));

                    ds.save(didNumber);

                    return didNumber.getId().toString();
                case "Agent":
                    Agent a = mapper.readValue(jsonObj.toJSONString(), Agent.class);

                    if(a.getMetadata().get("userId") == null) {
                        throw new InvalidParameterException("metadata.userId must be a valid email");
                    }

                    User user = UsersAPI.getInstance().getUserByEmail(a.getMetadata().get("userId"));
                    Agent agent = new Agent(user, a.getMetadata().get("name"), a.getSpec());
                    ds.save(agent);

                    return agent.getId().toString();
                case "Domain":
                    Domain dm = mapper.readValue(jsonObj.toJSONString(), Domain.class);

                    if(dm.getMetadata().get("userId") == null) {
                        throw new InvalidParameterException("metadata.userId must be a valid email");
                    }

                    User u = UsersAPI.getInstance().getUserByEmail(dm.getMetadata().get("userId"));
                    Domain domain = new Domain(u, dm.getMetadata().get("name"), dm.getSpec().getContext());
                    ds.save(domain);
                    return domain.getId().toString();
            }
        } catch(IOException e) {
            throw new ApiException(e.getMessage());
        }
        return null;
    }

    public String update(JSONObject jsonObj) throws ApiException {
        String kind = jsonObj.get("kind").toString();
        ObjectId id;

        try {
            switch (kind) {
                case "Gateway":
                    Gateway gateway = mapper.readValue(jsonObj.toJSONString(), Gateway.class);
                    String spIdStr = gateway.getMetadata().get("spId");

                    if(spIdStr == null || !ObjectId.isValid(spIdStr)) {
                        throw new InvalidParameterException("metadata.spId must be a valid ObjectId");
                    }

                    ObjectId spId = new ObjectId(spIdStr);
                    ServiceProvider sp = DIDNumbersAPI.getInstance().getServiceProviderById(spId);

                    if(sp == null) {
                        throw new MissingDepencyException();
                    }

                    // Look for GW using this reference
                    List l = find(Gateway.class, "@.spec.regService.credentials.username=='"
                            + gateway.getSpec().getRegService().getCredentials().getUsername()
                            + "' && @.spec.regService.host=='" + gateway.getSpec().getRegService().getHost() + "'");

                    if(l.size() == 0) {
                        throw new ResourceNotFoundException();
                    }

                    ObjectId gatewayId = new ObjectId(gateway.getMetadata().get("ref"));
                    Gateway gwFromDb = GatewaysAPI.getInstance().getGatewayById(gatewayId);
                    gwFromDb.setSpec(gateway.getSpec());
                    gwFromDb.setModified(new DateTime());
                    gwFromDb.setMetadata(gateway.getMetadata());
                    gwFromDb.setProvider(gateway.getProvider());

                    ds.save(gwFromDb);
                    return gwFromDb.getId().toString();
                case "DID":
                    DIDNumber d = mapper.readValue(jsonObj.toJSONString(), DIDNumber.class);
                    String didId = d.getMetadata().get("ref").toString();

                    Gateway gw = GatewaysAPI.getInstance()
                            .getGatewayById(new ObjectId(d.getMetadata().get("gwRef").toString()));

                    if(gw == null) {
                        throw new MissingDepencyException();
                    }

                    DIDNumber didFromDb = DIDNumbersAPI.getInstance().getDIDNumberByRef(didId);

                    didFromDb.setModified(new DateTime());
                    didFromDb.setSpec(d.getSpec());
                    didFromDb.setGateway(gw);

                    ds.save(didFromDb);

                    return didFromDb.getId().toString();
                case "Agent":
                    Agent a = mapper.readValue(jsonObj.toJSONString(), Agent.class);
                    ObjectId agentId = new ObjectId(a.getMetadata().get("ref"));

                    if(a.getMetadata().get("userId") == null) {
                        throw new InvalidParameterException("metadata.userId must be a valid email");
                    }

                    User user = UsersAPI.getInstance().getUserByEmail(a.getMetadata().get("userId"));

                    Agent agentFromDb = AgentsAPI.getInstance().getAgentById(user, agentId, false);
                    agentFromDb.setModified(new DateTime());
                    agentFromDb.setSpec(a.getSpec());
                    agentFromDb.setMetadata(a.getMetadata());

                    ds.save(agentFromDb);

                    return agentFromDb.getId().toString();
                case "Domain":
                    Domain dm = mapper.readValue(jsonObj.toJSONString(), Domain.class);
                    URI domainId;

                    try {
                        domainId = new URI(dm.getMetadata().get("ref"));
                    } catch (URISyntaxException e) {
                        throw new InvalidParameterException();
                    }

                    if(dm.getMetadata().get("userId") == null) {
                        throw new InvalidParameterException("metadata.userId must be a valid email");
                    }

                    User u = UsersAPI.getInstance().getUserByEmail(dm.getMetadata().get("userId"));

                    Domain domainFromDb = DomainsAPI.getInstance().getDomain(u, domainId, false);
                    domainFromDb.setModified(new DateTime());
                    domainFromDb.setSpec(dm.getSpec());
                    domainFromDb.setMetadata(dm.getMetadata());

                    ds.save(domainFromDb);
                    return domainFromDb.getId().toString();
            }
        } catch(IOException e) {
            throw new ApiException(e.getMessage());
        }
        return null;
    }

    public Object get(Class<?> clazz, String ref) throws ApiException {

        Object obj = null;

        try {
            String kind = clazz.newInstance().getClass().getSimpleName();

            switch (kind) {
                case "Gateway":
                    obj = ds.createQuery(Gateway.class)
                        .field("deleted").equal(false)
                            .field("_id").equal(ref)
                                .get();
                    break;
                case "DIDNumber":
                    obj = ds.createQuery(DIDNumber.class)
                        .field("deleted").equal(false)
                            .field("_id").equal(ref)
                                .get();
                    break;
                case "Agent":
                    obj = ds.createQuery(Agent.class)
                        .field("deleted").equal(false)
                            .field("_id").equal(ref)
                                .get();
                    break;
                case "Domain":
                    obj = ds.createQuery(Domain.class)
                        .field("deleted").equal(false)
                            .field("_id").equal(ref)
                                .get();
                    break;
            }
        }  catch(InstantiationException e) {
            throw new ApiException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ApiException(e.getMessage());
        }

        return obj;
    }

    public List find(Class<?> clazz, String f) throws ApiException {
        String filter = "*";
        String jsonInString;

        if (!Strings.isNullOrEmpty(f) && !f.equals("*")) {
            filter = "*.[?(" + f + ")]";
        }

        List objs = null;
        List result;

        try {
            switch (clazz.newInstance().getClass().getSimpleName()) {
                case "Gateway":
                    objs = ds.createQuery(Gateway.class).field("deleted").equal(false).asList();
                    break;
                case "DIDNumber":
                    objs = ds.createQuery(DIDNumber.class).field("deleted").equal(false).asList();
                    break;
                case "Agent":
                    objs = ds.createQuery(Agent.class).field("deleted").equal(false).asList();
                    break;
                case "Domain":
                    objs = ds.createQuery(Domain.class).field("deleted").equal(false).asList();
                    break;
            }

            jsonInString = mapper.writeValueAsString(objs);
            result = JsonPath.parse(jsonInString).read(filter);
        } catch (JsonProcessingException | IllegalAccessException | InstantiationException e) {
            throw new ApiException(e.getMessage());
        }

        return result;
    }

    public void remove(Class<?> clazz, String ref) throws ApiException {
        try {
            switch (clazz.newInstance().getClass().getSimpleName()) {
                case "Gateway":
                    Gateway gw = ds.createQuery(Gateway.class).field("metadata.ref").equal(ref).get();
                    gw.setDeleted(true);
                    ds.save(gw);
                    break;
                case "DIDNumber":
                    DIDNumber did = ds.createQuery(DIDNumber.class).field("metadata.ref").equal(ref).get();
                    did.setDeleted(true);
                    ds.save(did);
                    break;
                case "Agent":
                    Agent ag = ds.createQuery(Agent.class).field("metadata.ref").equal(ref).get();
                    ag.setDeleted(true);
                    ds.save(ag);
                    break;
                case "Domain":
                    Domain dm = ds.createQuery(Domain.class).field("metadata.ref").equal(ref).get();
                    dm.setDeleted(true);
                    ds.save(dm);
                    break;
            }

        } catch(InstantiationException | IllegalAccessException e) {
            throw new ApiException(e.getMessage());
        }
    }
}
