package com.fonoster.model.adapters;

import com.fonoster.model.CallDetailRecord;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CDRAdapter extends XmlAdapter<String, CallDetailRecord> {

    @Override
    public String marshal(CallDetailRecord cdr) throws Exception {
        return cdr.getId().toString();
    }

    @Override
    // We don't need to unmarshal for now
    public CallDetailRecord unmarshal(String cdr) throws Exception {
        return null;
    }
}