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
import com.fonoster.model.Domain;
import com.fonoster.model.User;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.mongodb.morphia.Datastore;
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

    public Domain createDomain(User user, URI uri, String desc) throws ApiException {
        if(getDomainByUri(uri) != null) throw new ApiException("This domain already exist.");

        Domain.Spec.Context context = new Domain.Spec.Context(uri);
        Domain domain = new Domain(user, desc, context);

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
             jsonInString= mapper.writeValueAsString(domains);
             result = JsonPath.parse(jsonInString).read(filter);
        } catch (JsonProcessingException e) {
            throw new ApiException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }

        return result;
    }

    public List<Domain> getDomainsFor(User user) {
        if (user == null) return new ArrayList<>();
        return ds.createQuery(Domain.class).field("user").equal(user).field("deleted").equal(false).asList();
    }

    public Domain getDomainByUri(URI uri) {
        return ds.createQuery(Domain.class).field("id").equal(uri).field("deleted").equal(false).get();
    }

    public boolean domainExist(URI uri) {
        return getDomainByUri(uri) != null;
    }

    public boolean domainHasUser(URI uri, String username) {
        return aAPI.getAgent(uri, username) != null;
    }
}
