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
import com.fonoster.model.Gateway;
import com.fonoster.model.ServiceProvider;
import org.mongodb.morphia.Datastore;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Since("1.0")
public class GatewaysAPI {
    private static final GatewaysAPI INSTANCE = new GatewaysAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private GatewaysAPI() {

    }

    public static GatewaysAPI getInstance() {
        return INSTANCE;
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

}
