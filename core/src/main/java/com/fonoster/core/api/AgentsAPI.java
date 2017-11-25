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

import com.fonoster.annotations.Since;
import com.fonoster.exception.*;
import com.fonoster.model.Agent;
import com.fonoster.model.User;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Since("1.0")
public class AgentsAPI {
    private static final AgentsAPI INSTANCE = new AgentsAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private AgentsAPI() {
    }

    public static AgentsAPI getInstance() {
        return INSTANCE;
    }

    public List<Agent> getAgents(User user, DateTime start, DateTime end, int maxResults, int firstResult) throws ApiException {

        if (user == null) throw new InvalidParameterException("Invalid user.");

        if (maxResults < 0) maxResults = 0;
        if (maxResults > 1000) maxResults = 1000;

        if (firstResult < 0) firstResult = 0;
        if (firstResult > 1000) firstResult = 1000;

        Query<Agent> q = ds.createQuery(Agent.class)
            .field("user").equal(user)
                .field("deleted").equal(false);

        // All recordings from start date
        if (start != null) {
            q.filter("created >=", start);
        }

        // All recordings until end date
        if (end != null) {
            q.filter("created <=", end);
        }

        return q.limit(maxResults).offset(firstResult).asList();
    }

    public Agent createAgent(User user, URI domainUri, String name, String username, String secret) throws ApiException {

        if (!DomainsAPI.getInstance().isDomainOwner(user, domainUri)) throw new UnauthorizedAccessException();

        if(agentExist(username, domainUri)) throw new AgentAlreadyExistException();

        Agent.Spec spec = new Agent.Spec();
        Agent.Spec.Credentials credentials = new Agent.Spec.Credentials(username, secret);
        spec.setCredentials(credentials);
        spec.setDomains(Arrays.asList(domainUri));

        Agent agent = new Agent(user, name, spec);

        // JavaBean validation
        if (!validator.validate(agent).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<Agent>> validate = validator.validate(agent);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }

        ds.save(agent);
        return agent;
    }

    public Agent getAgentById(User user, ObjectId agentId, boolean ignoreDeleted) throws ResourceNotFoundException {
        Query<Agent> q = ds.createQuery(Agent.class)
            .field("user").equal(user)
                .field("id").equal(agentId);

        if (!ignoreDeleted) {
            q.field("deleted").notEqual(true);
        }

        Agent agent = q.get();

        if (agent == null) throw new ResourceNotFoundException();

        return agent;
    }

    private boolean agentExist(String username, URI domain) {
        return ds.createQuery(Agent.class)
        .field("spec.credentials.username").equal(username)
            .field("spec.domains").hasThisOne(domain)
                .field("deleted").equal(false)
                    .count() > 0;
    }
}
