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
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.DID;
import com.fonoster.model.Gateway;
import com.fonoster.model.ServiceProvider;
import com.fonoster.model.User;
import com.fonoster.utils.BeanValidatorUtil;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Since("1.0")
public class DIDsAPI {
    private static final Logger LOG = LoggerFactory.getLogger(DIDsAPI.class);
    private static final DIDsAPI INSTANCE = new DIDsAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private DIDsAPI() {
    }

    public static DIDsAPI getInstance() {
        return INSTANCE;
    }

    public DID createDID(User renter, Gateway gateway, String number, Map<String, Map > geoInfo) throws ApiException {

        if (getDID(renter, number) != null) throw new ApiException("This number has been assigned.");

        // TODO: Get aorLink from config
        DID.Spec.Location location = new DID.Spec.Location("tel:" + number, "");

        DID did = new DID(renter, gateway, location, geoInfo);

        // JavaBean validation
        if (!validator.validate(did).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<DID>> validate = validator.validate(did);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }

        ds.save(did);
        return did;
    }

    public DID getDID(String telUrl) throws ApiException {
        LOG.debug("Getting obj DID for: " + telUrl);
        DID did = ds.createQuery(DID.class).field("number").equal(telUrl).field("status").equal(DID.Status.ACTIVE).get();

        if (did == null) throw new ApiException("Unable to find DID for tel url '" + telUrl + "'");

        return did;
    }

    public DID getDID(User user, String number) throws ApiException {
        DID pn = ds.createQuery(DID.class).field("user").equal(user)
                .field("number").equal(number).field("status").equal(DID.Status.ACTIVE).get();

        if (pn == null) throw new ApiException("Unable to find number " + number);

        return pn;
    }

    public List<DID> getDIDsFor(User user, DID.Status status) throws ApiException {

        if (user == null) throw new ApiException("Invalid user.");

        Query<DID> q = ds.createQuery(DID.class).field("user").equal(user);

        if (status != null) {
            q.field("status").equal(status);
        }

        return q.limit(1000).asList();
    }

    public List<DID> getDIDsFor(User user, DID.Status status, int maxResults, int firstResult) throws ApiException {
        if (user == null) throw new ApiException("Invalid user.");

        if (maxResults < 0) maxResults = 0;
        if (maxResults > 1000) maxResults = 1000;

        if (firstResult < 0) firstResult = 0;
        if (firstResult > 1000) firstResult = 1000;

        Query<DID> q = ds.createQuery(DID.class).field("user").equal(user);

        if (status != null) {
            q.field("status").equal(status);
        }

        return q.limit(maxResults).offset(firstResult).asList();
    }

    public void setDefault(User user, DID did) throws ApiException {

        for (DID d : getDIDsFor(user, DID.Status.ACTIVE)) {
            if (did.getId().equals(d.getId())) {
                d.setPreferred(true);
            } else {
                d.setPreferred(false);
            }
            // Notice: I'm saving 'd' not did, because pn comes from the db and is complete
            ds.save(d);
        }
    }

    public DID getDefault(User user) throws ApiException {

        for (DID did : getDIDsFor(user, DID.Status.ACTIVE)) {
            if (did.isPreferred()) return did;
        }

        if (getDIDsFor(user, DID.Status.ACTIVE).size() > 0) {
            return getDIDsFor(user, DID.Status.ACTIVE).get(0);
        }

        throw new ApiException("Not numbers were found for this user.");
    }

    public ServiceProvider createServiceProvider(String name, String address, String contact) throws InvalidParameterException {
        ServiceProvider sp = new ServiceProvider(name, address, contact);

        if (!BeanValidatorUtil.isValidBean(sp))
            throw new InvalidParameterException(BeanValidatorUtil.getValidationError(sp));

        ds.save(sp);
        return sp;
    }

    // Should also provide a getServiceProviderById and getServiceProviderByCapabilities-> SMS|VOICE...
    public List<ServiceProvider> getServiceProviders() {
        return ds.createQuery(ServiceProvider.class).asList();
    }

    public ServiceProvider getServiceProviderById(ObjectId id) {
        return ds.createQuery(ServiceProvider.class).field("_id").equal(id).get();
    }

    public void updateDID(User user, DID phoneNumber) throws ApiException {

        if (!user.getEmail().equals(phoneNumber.getUser().getEmail())) {
            throw new UnauthorizedAccessException();
        }

        // TODO: Do this everywhere !!!
        if (!validator.validate(phoneNumber).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<DID>> validate = validator.validate(phoneNumber);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }
        ds.save(phoneNumber);
    }
}
