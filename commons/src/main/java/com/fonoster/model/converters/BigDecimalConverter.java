/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
/*
 *Copyright (C) 2014 PhonyTive LLC
 *http://fonoster.com
 *
 *This file is part of Fonoster
 */
package com.fonoster.model.converters;

import com.fonoster.annotations.Since;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;

@Since("1.0")
public class BigDecimalConverter extends TypeConverter implements SimpleValueConverter {

  public BigDecimalConverter() {
    super(BigDecimal.class);
  }

  @Override
  public Object encode(Object value, MappedField optionalExtraInfo) {
    if (value == null) {
      return null;
    }
    BigDecimal bigDecimalValue = (BigDecimal) value;

    if (bigDecimalValue.scale() > 18) {
      bigDecimalValue = bigDecimalValue.setScale(18, BigDecimal.ROUND_DOWN);
    }

    DBObject dbo = new BasicDBObject();

    dbo.put("unscaled", bigDecimalValue.unscaledValue().longValue());
    dbo.put("scale", bigDecimalValue.scale());

    return dbo;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Object decode(Class targetClass, Object fromDBObject, MappedField field)
      throws MappingException {
    DBObject dbo = (DBObject) fromDBObject;
    if (dbo == null) {
      return null;
    }

    BigDecimal bigDecimal = null;

    Long unscaled = (Long) dbo.get("unscaled");
    Integer scale = (Integer) dbo.get("scale");

    if (unscaled != null && scale != null) {
      bigDecimal = new BigDecimal(new BigInteger(unscaled.toString()), scale);
    }

    return bigDecimal;
  }
}
