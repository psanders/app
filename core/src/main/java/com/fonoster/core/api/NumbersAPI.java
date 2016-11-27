/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.core.api;

import com.fonoster.exception.ApiException;
import com.fonoster.exception.InvalidParameterException;
import com.fonoster.exception.UnauthorizedAccessException;
import com.fonoster.model.PhoneNumber;
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NumbersAPI {
    private static final Logger LOG = LoggerFactory.getLogger(NumbersAPI.class);
    private static final NumbersAPI INSTANCE = new NumbersAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private NumbersAPI() {
    }

    public static NumbersAPI getInstance() {
        return INSTANCE;
    }

    public PhoneNumber createPhoneNumber(
        User user,
        ServiceProvider provider,
        String number,
        String countryISOCode) throws ApiException {

        if(getPhoneNumber(user, number) != null) throw new ApiException("This number has been assigned.");

        PhoneNumber phoneNumber = new PhoneNumber(user, provider, number, countryISOCode);

        // JavaBean validation
        if (!validator.validate(phoneNumber).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<PhoneNumber>> validate = validator.validate(phoneNumber);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }
        ds.save(phoneNumber);
        return phoneNumber;
    }

    // Keep in mind that using curl the encoding of the plus(+) symbol does not happens
    // Therefore user must replace '+' by %2B
    // TODO: Validate/reformat number if necessary
    // WARNING: This is for internal use only
    public PhoneNumber getPhoneNumber(String number) throws ApiException {
        LOG.debug("Getting obj PhoneNumber for: " + number);
        PhoneNumber pn = ds.createQuery(PhoneNumber.class)
            .field("number").equal(number)
            .field("status").equal(PhoneNumber.Status.ACTIVE)
            .get();

        if (pn == null) throw new ApiException("Unable to find number " + number);

        return pn;
    }

    // Keep in mind that using curl the encoding of the plus(+) symbol does not happens
    // Therefore user must replace '+' by %2B
    // TODO: Validate/reformat number if necessary
    public PhoneNumber getPhoneNumber(User user, String number) throws ApiException {
        PhoneNumber pn = ds.createQuery(PhoneNumber.class)
            .field("user").equal(user)
            .field("number").equal(number)
            .field("status").equal(PhoneNumber.Status.ACTIVE)
            .get();

        //if (pn == null) throw new ApiException("Unable to find number " + number);

        return pn;
    }

    // WARNING: Should I limit this to 1000?
    public List<PhoneNumber> getPhoneNumbersFor(User user, PhoneNumber.Status status) throws ApiException {

        if (user == null) throw new ApiException("Invalid user.");

        Query<PhoneNumber> q = ds.createQuery(PhoneNumber.class)
                .field("user").equal(user);

        if (status != null) {
            q.field("status").equal(status);
        }

        return q.limit(1000).asList();
    }

    public List<PhoneNumber> getPhoneNumbersFor(User user, PhoneNumber.Status status, int maxResults, int firstResult) throws ApiException {

        if (user == null) throw new ApiException("Invalid user.");

        if (maxResults < 0) maxResults = 0;
        if (maxResults > 1000) maxResults = 1000;

        if (firstResult < 0) firstResult = 0;
        if (firstResult > 1000) firstResult = 1000;

        Query<PhoneNumber> q = ds.createQuery(PhoneNumber.class)
                .field("user").equal(user);

        if (status != null) {
            q.field("status").equal(status);
        }

        return q.limit(maxResults).offset(firstResult).asList();
    }

    public void setDefault(User user, PhoneNumber phone) throws ApiException {
        Iterator<PhoneNumber> phones = getPhoneNumbersFor(user, PhoneNumber.Status.ACTIVE).iterator();

        while(phones.hasNext()) {
            PhoneNumber pn = phones.next();
            if (phone.getId().equals(pn.getId())) {
                pn.setPreferred(true);
            } else {
                pn.setPreferred(false);
            }
            // Notice: I'm saving pn not phone, because pn comes from the db and is complete
            ds.save(pn);
        }
    }

    public PhoneNumber getDefault(User user) throws ApiException {
        Iterator<PhoneNumber> phones = getPhoneNumbersFor(user, PhoneNumber.Status.ACTIVE).iterator();

        while(phones.hasNext()) {
            PhoneNumber pn = phones.next();
            if (pn.isPreferred()) return pn;
        }

        if (getPhoneNumbersFor(user, PhoneNumber.Status.ACTIVE).size() > 0) {
            return getPhoneNumbersFor(user, PhoneNumber.Status.ACTIVE).get(0);
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

    public void updatePhoneNumber(User user, PhoneNumber phoneNumber) throws ApiException {

        if (!user.getEmail().equals(phoneNumber.getUser().getEmail())) {
            throw new UnauthorizedAccessException();
        }

        // TODO: Do this everywhere !!!
        if (!validator.validate(phoneNumber).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<PhoneNumber>> validate = validator.validate(phoneNumber);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }
        ds.save(phoneNumber);
    }
}
