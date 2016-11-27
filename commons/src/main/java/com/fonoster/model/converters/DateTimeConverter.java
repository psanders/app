/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.model.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;

import java.util.Map;

public class DateTimeConverter extends TypeConverter implements SimpleValueConverter {

    public static final String MILLIS_PROPERTY_NAME = "t";
    public static final String TIME_ZONE_PROPERTY_NAME = "z";

    public DateTimeConverter() {
        super(DateTime.class);
    }

    @Override
    public final Object encode(Object value, MappedField optionalExtraInfo)
            throws MappingException {
        if (value == null) {
            return null;
        }

        if (!(value instanceof DateTime)) {
            throw new RuntimeException(
                    "Did not expect " + value.getClass().getName());
        }

        DateTime dt = (DateTime) value;
        DBObject obj = new BasicDBObject();

        obj.put(MILLIS_PROPERTY_NAME, new Long(dt.getMillis()));
        obj.put(TIME_ZONE_PROPERTY_NAME, dt.getZone().getID());
        return obj;
    }

    @Override
    public DateTime decode(Class<?> targetClass, Object fromDBObject,
                           MappedField optionalExtraInfo) {
        if (fromDBObject == null) {
            return null;
        }

        if (fromDBObject instanceof Map) {
            Map<String, Object> map = (Map) fromDBObject;

            Long millis = new Long(map.get(MILLIS_PROPERTY_NAME).toString());

            return new DateTime(millis, DateTimeZone.forID(map.get(
                    TIME_ZONE_PROPERTY_NAME).toString()));
        }

        throw new RuntimeException(
                "Did not expect " + fromDBObject.getClass().getName());
    }
}