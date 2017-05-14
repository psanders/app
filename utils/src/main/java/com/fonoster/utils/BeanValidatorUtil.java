/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
package com.fonoster.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

//@Since("1.0")
public class BeanValidatorUtil {
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static boolean isValidBean(Object obj) {
        return validator.validate(obj).size() == 0;
    }

    public static String getValidationError(Object obj) {
        // JavaBean validation
        if (validator.validate(obj).size() > 0) {
            StringBuilder sb = new StringBuilder(75);
            Set<ConstraintViolation<Object>> validate = validator.validate(obj);
            for (ConstraintViolation<?> cv : validate) {
                sb.append("[param '");
                sb.append(cv.getPropertyPath().toString());
                sb.append("' ");
                sb.append(cv.getMessage());
                sb.append("]");
            }
            return sb.toString();
        }
        return ""; // No errors
    }
}
