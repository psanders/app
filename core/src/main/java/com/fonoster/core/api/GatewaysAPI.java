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
import com.fonoster.model.Gateway;
import com.fonoster.model.ServiceProvider;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.net.UnknownHostException;
import java.util.Set;

@Since("1.0")
public class GatewaysAPI {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static GatewaysAPI instance;
    private static Datastore ds;

    private GatewaysAPI() {

    }

    public static GatewaysAPI getInstance() throws ApiException {
        if (instance == null || ds == null) {
            try {
                ds = DBManager.getInstance().getDS();
                instance = new GatewaysAPI();
            } catch (UnknownHostException e) {
                throw new ApiException();
            }
        }
        return instance;
    }

    public Gateway createGateway(ServiceProvider provider, String name, Gateway.Spec.RegService regService) throws ApiException {
        Gateway gateway = new Gateway(provider, name, regService);
        Gateway.Spec spec = new Gateway.Spec();
        spec.setRegService(regService);
        gateway.setSpec(spec);

        // JavaBean validation
        if (!validator.validate(gateway).isEmpty()) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<Gateway>> validate = validator.validate(gateway);
            for (ConstraintViolation<?> cv : validate) {
                sb.append(cv.getMessage());
                sb.append("\n");
            }
            throw new ApiException("Invalid parameter. [" + sb.toString() + "]");
        }

        ds.save(gateway);
        return gateway;
    }

    public Gateway getGatewayById(ObjectId id) throws ResourceNotFoundException, InvalidParameterException {

        if (id == null) throw  new InvalidParameterException();

        Query q = ds.createQuery(Gateway.class).field("_id").equal(id);
        Gateway result = (Gateway) q.get();

        if (result == null) throw new ResourceNotFoundException();
        return result;
    }


}
