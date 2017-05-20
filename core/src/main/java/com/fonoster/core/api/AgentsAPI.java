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

import com.fonoster.exception.ApiException;
import com.fonoster.exception.InvalidParameterException;
import com.fonoster.exception.ResourceNotFoundException;
import com.fonoster.exception.UnauthorizedAccessException;
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
import java.util.List;
import java.util.Set;

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

        Query<Agent> q = ds.createQuery(Agent.class).field("user").equal(user);

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

        if (!DomainsAPI.getInstance().ownsDomain(user, domainUri)) throw  new UnauthorizedAccessException();

        Agent.Spec.Credentials credentials = new Agent.Spec.Credentials(username, secret);
        Agent agent = new Agent(user, name, credentials);

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

    public Agent getAgent(User user, ObjectId agentId) throws ResourceNotFoundException {
        Agent agent = ds.createQuery(Agent.class)
            .field("user").equal(user)
                .field("id").equal(agentId)
                        .field("deleted").equal(false).get();

        if (agent == null) throw new ResourceNotFoundException();

        return agent;
    }
}
