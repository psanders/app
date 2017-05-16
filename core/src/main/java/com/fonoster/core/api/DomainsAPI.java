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
import com.fonoster.exception.ApiException;
import com.fonoster.exception.InvalidParameterException;
import com.fonoster.exception.ResourceNotFoundException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.Domain;
import com.fonoster.model.PhoneNumber;
import com.fonoster.model.User;
import com.google.common.base.Strings;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Since("1.0")
public class DomainsAPI {
    private static final Logger LOG = LoggerFactory.getLogger(DomainsAPI.class);
    private static final DomainsAPI INSTANCE = new DomainsAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private AgentsAPI aAPI = AgentsAPI.getInstance();
    private ObjectMapper mapper = new ObjectMapper();
    private Configuration conf;

    private DomainsAPI() {
        conf = Configuration.defaultConfiguration();
        conf.addOptions(Option.ALWAYS_RETURN_LIST);
    }

    public static DomainsAPI getInstance() {
        return INSTANCE;
    }

    public Domain createDomain(User user, URI domainUri, String name, String egressRule, String egressDidRef) throws ApiException {
        if (domainExist(domainUri)) throw new ApiException("This domain already exist.");

        Domain.Spec.Context context = new Domain.Spec.Context(domainUri);
        Domain domain = new Domain(user, name, context);

        if (Strings.isNullOrEmpty(egressRule) != Strings.isNullOrEmpty(egressDidRef)) {
            throw new InvalidParameterException("EgressRule and EgressDidRef parameters must both be present to enable the EgressPolicy");
        }

        if (!Strings.isNullOrEmpty(egressRule) && !Strings.isNullOrEmpty(egressDidRef)) {

            PhoneNumber pn = NumbersAPI.getInstance().getPhoneNumber(egressDidRef);
            if (pn == null || pn.getUser().getEmail() != user.getEmail()) throw new UnauthorizedAccessException("This DID is not assigned to you");

            Domain.Spec.Context.EgressPolicy ep = new Domain.Spec.Context.EgressPolicy(egressRule, egressDidRef);
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

    public Domain getDomain(User user, String uri) throws ResourceNotFoundException {
        Domain result = ds.createQuery(Domain.class)
            .field("id")
                .equal(uri).field("deleted").equal(false)
                    .field("user").equal(user).get();

        if (result == null) throw new ResourceNotFoundException();
        return result;
    }

    // Only for admin account (Including Sip I/O integration)
    public List<Domain> getDomains(String f) throws ApiException {
        String filter;
        String jsonInString;

        if (f == null || f.isEmpty()) {
            filter = "*";
        } else {
            filter = "*.[?(@." + f + ")]";
        }

        List<Domain> domains = ds.createQuery(Domain.class).field("deleted").equal(false).asList();
        List<Domain> result;

        try {
            jsonInString = mapper.writeValueAsString(domains);
            result = JsonPath.parse(jsonInString).read(filter);
        } catch (JsonProcessingException e) {
            throw new ApiException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

        if(result.isEmpty()) throw new ResourceNotFoundException();

        return result;
    }

    public List<Domain> getDomains(User user, DateTime start, DateTime end, int maxResults, int firstResult, boolean starred, Domain.Status status) throws ApiException {

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

        if (starred) {
            q.field("starred").equal(true);
            q.field("status").equal(Domain.Status.NORMAL);
        } else {
            q.field("status").equal(status);
        }

        return q.limit(maxResults).offset(firstResult).asList();
    }

    public List<Domain> getDomainsFor(User user) {
        if (user == null) return new ArrayList<>();
        return ds.createQuery(Domain.class).field("user").equal(user).field("deleted").equal(false).asList();
    }

    public Domain getDomainByUri(URI domainUri) throws ResourceNotFoundException {
        Domain result = ds.createQuery(Domain.class).field("id").equal(domainUri).field("deleted").equal(false).get();
        if (result == null) throw new ResourceNotFoundException();
        return result;
    }

    public boolean domainExist(URI domainUri) throws ResourceNotFoundException {
        return getDomainByUri(domainUri) != null;
    }

    public boolean domainHasUser(URI uri, String username) {
        return aAPI.getAgent(uri, username) != null;
    }
}
