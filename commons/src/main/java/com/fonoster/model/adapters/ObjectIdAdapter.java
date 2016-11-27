package com.fonoster.model.adapters;

import org.bson.types.ObjectId;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ObjectIdAdapter extends XmlAdapter<String, ObjectId> {

    @Override
    public String marshal(ObjectId obj) throws Exception {
        return obj.toString();
    }

    @Override
    public ObjectId unmarshal(String obj) throws Exception {
        return new ObjectId(obj);
    }
}