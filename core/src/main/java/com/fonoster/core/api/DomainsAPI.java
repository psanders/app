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
import com.fonoster.model.DID;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Since("1.0")
public class DomainsAPI {
    private static final DomainsAPI INSTANCE = new DomainsAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private DomainsAPI() {
    }

    public static DomainsAPI getInstance() {
        return INSTANCE;
    }

    public Domain createDomain(User user, URI domainUri, String name, String egressRule, String egressDIDRef) throws ApiException {
        if (domainExist(domainUri)) throw new ApiException("This domain already exist.");

        Domain.Spec.Context context = new Domain.Spec.Context(domainUri);
        Domain domain = new Domain(user, name, context);

        if (Strings.isNullOrEmpty(egressRule) != Strings.isNullOrEmpty(egressDIDRef)) {
            throw new InvalidParameterException("EgressRule and EgressDIDRef parameters must both be present to enable the EgressPolicy");
        }

        if (!Strings.isNullOrEmpty(egressRule) && !Strings.isNullOrEmpty(egressDIDRef)) {
            DID did = DIDsAPI.getInstance().getDID(egressDIDRef);
            if (did == null || !Objects.equals(did.getRenter().getEmail(), user.getEmail())) throw new UnauthorizedAccessException("This DID is not assigned to you");

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

    public Domain getDomain(User user, URI domainUri) throws ResourceNotFoundException, InvalidParameterException {

        if (domainUri == null || user == null) throw  new InvalidParameterException();

        Domain result = ds.createQuery(Domain.class)
            .field("id")
                .equal(domainUri).field("deleted").equal(false)
                    .field("user").equal(user).get();

        if (result == null) throw new ResourceNotFoundException();
        return result;
    }

    public List<Domain> getDomains(User user, DateTime start, DateTime end, int maxResults, int firstResult) throws ApiException {

        if (user == null) throw new InvalidParameterException("Invalid user.");

        if (maxResults < 0) maxResults = 0;
        if (maxResults > 1000) maxResults = 1000;

        if (firstResult < 0) firstResult = 0;
        if (firstResult > 1000) firstResult = 1000;

        Query<Domain> q = ds.createQuery(Domain.class).field("user").equal(user);

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

    public Domain getDomainByUri(URI domainUri) throws ResourceNotFoundException, InvalidParameterException {
        if (domainUri == null) throw  new InvalidParameterException("domainUri");
        Domain result = ds.createQuery(Domain.class).field("id").equal(domainUri).field("deleted").equal(false).get();
        if (result == null) throw new ResourceNotFoundException();
        return result;
    }

    public boolean domainExist(URI domainUri) throws ResourceNotFoundException, InvalidParameterException {
        if (domainUri == null) throw  new InvalidParameterException();
        return getDomainByUri(domainUri) != null;
    }

    public boolean isDomainOwner(User user, URI domainUri) throws ResourceNotFoundException, InvalidParameterException {
        if (domainUri == null || user == null) throw  new InvalidParameterException();

        Domain result = ds.createQuery(Domain.class)
                .field("id")
                .equal(domainUri).field("deleted").equal(false)
                .field("user").equal(user).get();
        return result != null;
    }
}
