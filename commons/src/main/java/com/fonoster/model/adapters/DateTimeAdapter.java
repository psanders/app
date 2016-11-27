package com.fonoster.model.adapters;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateTimeAdapter extends XmlAdapter<String, DateTime> {

    @Override
    public String marshal(DateTime dt) throws Exception {
        return dt.toString();
    }

    @Override
    public DateTime unmarshal(String dt) throws Exception {
        return new DateTime(dt);
    }
}