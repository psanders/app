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
import com.fonoster.exception.ApiException;
import com.fonoster.exception.InvalidParameterException;
import com.fonoster.exception.ResourceNotFoundException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Agent;
import com.fonoster.model.DIDNumber;
import com.fonoster.model.Domain;
import com.fonoster.model.User;
import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Since("1.0")
public class DomainsAPI {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static DomainsAPI instance = new DomainsAPI();
    private static Datastore ds;

    private DomainsAPI() {
    }

    public static DomainsAPI getInstance() throws ApiException {
        if (instance == null || ds == null) {
            try {
                ds = DBManager.getInstance().getDS();
                instance = new DomainsAPI();
            } catch (UnknownHostException e) {
                throw new ApiException();
            }
        }
        return instance;
    }

    public Domain createDomain(User user, URI domainUri, String name, String egressRule, String egressDIDRef) throws ApiException {
        if (domainExist(domainUri)) throw new ApiException("This domain already exist.");

        Domain.Spec.Context context = new Domain.Spec.Context(domainUri);
        Domain domain = new Domain(user, name, context);

        if (Strings.isNullOrEmpty(egressRule) != Strings.isNullOrEmpty(egressDIDRef)) {
            throw new InvalidParameterException("EgressRule and EgressDIDRef parameters must both be present to enable the EgressPolicy");
        }

        if (!Strings.isNullOrEmpty(egressRule) && !Strings.isNullOrEmpty(egressDIDRef)) {
            DIDNumber did = DIDNumbersAPI.getInstance().getDIDNumberByRef(user, egressDIDRef);
            if (did == null || !Objects.equals(did.getUser().getEmail(), user.getEmail())) throw new UnauthorizedAccessException("This DID is not assigned to you");

            Domain.Spec.Context.EgressPolicy ep = new Domain.Spec.Context.EgressPolicy(egressRule, egressDIDRef);
            domain.getSpec().getContext().setEgressPolicy(ep);
        }

        // JavaBean validation
        if (!validator.validate(domain).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<Domain>> validate = validator.validate(domain);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }

        ds.save(domain);
        return domain;
    }

    public Domain updateDomain(Domain domain) {
        ds.save(domain);
        return domain;
    }

    public List<Domain> getDomains(User user, DateTime start, DateTime end, int maxResults, int firstResult) throws ApiException {

        if (user == null) throw new InvalidParameterException("Invalid user.");

        if (maxResults < 0) maxResults = 0;
        if (maxResults > 1000) maxResults = 1000;

        if (firstResult < 0) firstResult = 0;
        if (firstResult > 1000) firstResult = 1000;

        Query<Domain> q = ds.createQuery(Domain.class)
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

    public Domain getDomain(User user, URI domainUri, boolean ignoreDeleted) throws ResourceNotFoundException, InvalidParameterException {

        if (domainUri == null || user == null) throw  new InvalidParameterException();

        Query<Domain> q = ds.createQuery(Domain.class)
            .field("user").equal(user)
                .field("spec.context.domainUri").equal(domainUri);

        if (!ignoreDeleted) {
            q.field("deleted").notEqual(true);
        }

        Domain result = q.get();

        if (result == null) throw new ResourceNotFoundException();
        return result;
    }

    public boolean domainExist(URI domainUri) throws InvalidParameterException {
        if (domainUri == null) throw new InvalidParameterException("domainUri");
        Domain result = ds.createQuery(Domain.class)
            .field("spec.context.domainUri").equal(domainUri)
                .field("deleted").equal(false).get();
        return  result != null;
    }

    public boolean isDomainOwner(User user, URI domainUri) throws ResourceNotFoundException, InvalidParameterException {
        if (domainUri == null || user == null) throw  new InvalidParameterException();

        Domain result = ds.createQuery(Domain.class)
            .field("spec.context.domainUri").equal(domainUri)
                .field("deleted").equal(false)
                    .field("user").equal(user).get();
        return result != null;
    }

    public boolean domainHasAgents(User user, URI domainUri) throws InvalidParameterException {
        if (domainUri == null || user == null) throw  new InvalidParameterException();

        return ds.createQuery(Agent.class)
            .field("user").equal(user)
                .field("spec.domains").hasThisOne(domainUri)
                    .field("deleted").equal(false).count() > 0;
    }
}
