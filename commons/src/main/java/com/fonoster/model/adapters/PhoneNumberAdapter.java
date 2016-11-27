package com.fonoster.model.adapters;

import com.fonoster.model.PhoneNumber;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PhoneNumberAdapter extends XmlAdapter<String, PhoneNumber> {

    @Override
    public String marshal(PhoneNumber phoneNumber) throws Exception {
        return phoneNumber.getNumber();
    }

    @Override
    // We don't need to unmarshal for now
    public PhoneNumber unmarshal(String phoneNumber) throws Exception {
        return null;
    }
}