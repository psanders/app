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
import com.fonoster.model.DIDNumber;
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
public class DIDNumbersAPI {
    private static final Logger LOG = LoggerFactory.getLogger(DIDNumbersAPI.class);
    private static final DIDNumbersAPI INSTANCE = new DIDNumbersAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private DIDNumbersAPI() {
    }

    public static DIDNumbersAPI getInstance() {
        return INSTANCE;
    }

    public DIDNumber createDIDNumber(Gateway gateway, String number, Map<String, String> geoInfo,
                                     Map<String, Boolean> tech) throws ApiException {

        if (existDIDNumber(number)) throw new ApiException("This number already exist.");

        DIDNumber.Spec.Location location = new DIDNumber.Spec.Location("tel:" + number, "ast@fnast");

        DIDNumber didNumber = new DIDNumber(gateway, location, geoInfo, tech);

        // JavaBean validation
        if (!validator.validate(didNumber).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<DIDNumber>> validate = validator.validate(didNumber);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }

        ds.save(didNumber);
        return didNumber;
    }

    public void rentDIDNumber(DIDNumber didNumber, User user) throws ApiException {

        // TODO: Verify balance...

        didNumber.setUser(user);

        if (!validator.validate(didNumber).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<DIDNumber>> validate = validator.validate(didNumber);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }
        ds.save(didNumber);
    }

    public DIDNumber getDIDNumber(String number) throws ApiException {
        LOG.debug("Getting obj DIDNumber for: " + number);

        number = number.replace("+", "");

        DIDNumber didNumber = ds.createQuery(DIDNumber.class)
                .field("spec.location.telUrl").equal("tel:" + number)
                .field("status").equal(DIDNumber.Status.ACTIVE).get();

        if (didNumber == null) throw new ApiException("Unable to find DIDNumber for number '" + number + "'");

        return didNumber;
    }

    public DIDNumber getDIDNumber(User user, String number) throws ApiException {
        LOG.debug("Getting obj DIDNumber for: " + number + " and user => " + user.getEmail());

        number = number.replace("+", "");

        DIDNumber didNumber = ds.createQuery(DIDNumber.class).field("user").equal(user)
                .field("spec.location.telUrl").equal("tel:" + number)
                .field("status").equal(DIDNumber.Status.ACTIVE).get();

        if (didNumber == null) throw new ApiException("Unable to find number " + number);

        return didNumber;
    }

    private boolean existDIDNumber(String number) throws ApiException {
        LOG.debug("Getting obj DIDNumber for: " + number);
        DIDNumber didNumber = ds.createQuery(DIDNumber.class)
                .field("spec.location.telUrl").equal("tel:" + number)
                .field("deleted").equal(false).get();

        if (didNumber == null) return false;

        return true;
    }

    public List<DIDNumber> getDIDNumbersFor(User user, DIDNumber.Status status) throws ApiException {

        if (user == null) throw new ApiException("Invalid user.");

        Query<DIDNumber> q = ds.createQuery(DIDNumber.class).field("user").equal(user);

        if (status != null) {
            q.field("status").equal(status);
        }

        return q.limit(1000).asList();
    }

    public List<DIDNumber> getDIDNumbersFor(User user, DIDNumber.Status status, int maxResults, int firstResult) throws ApiException {
        if (user == null) throw new ApiException("Invalid user.");

        if (maxResults < 0) maxResults = 0;
        if (maxResults > 1000) maxResults = 1000;

        if (firstResult < 0) firstResult = 0;
        if (firstResult > 1000) firstResult = 1000;

        Query<DIDNumber> q = ds.createQuery(DIDNumber.class).field("user").equal(user);

        if (status != null) {
            q.field("status").equal(status);
        }

        return q.limit(maxResults).offset(firstResult).asList();
    }

    @Deprecated
    public void setDefault(User user, DIDNumber did) throws ApiException {
        for (DIDNumber d : getDIDNumbersFor(user, DIDNumber.Status.ACTIVE)) {
            if (did.getId().equals(d.getId())) {
                d.setPreferred(true);
            } else {
                d.setPreferred(false);
            }
            // Notice: I'm saving 'd' not did, because pn comes from the db and is complete
            ds.save(d);
        }
    }

    public DIDNumber getDefault(User user) throws ApiException {
        for (DIDNumber didNumber : getDIDNumbersFor(user, DIDNumber.Status.ACTIVE)) {
            if (didNumber.isPreferred()) return didNumber;
        }

        if (getDIDNumbersFor(user, DIDNumber.Status.ACTIVE).size() > 0) {
            return getDIDNumbersFor(user, DIDNumber.Status.ACTIVE).get(0);
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

    public void updateDIDNumber(User user, DIDNumber didNumber) throws ApiException {

        if (!user.getEmail().equals(didNumber.getUser().getEmail())) {
            throw new UnauthorizedAccessException();
        }

        if (!validator.validate(didNumber).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<DIDNumber>> validate = validator.validate(didNumber);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }
        ds.save(didNumber);

        if (didNumber.isPreferred()) {
            for (DIDNumber d : getDIDNumbersFor(user, DIDNumber.Status.ACTIVE)) {
                if (!didNumber.getId().equals(d.getId())) {
                    d.setPreferred(false);
                    ds.save(d);
                }
            }
        }
    }
}
