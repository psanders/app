package com.fonoster.model.adapters;

import com.fonoster.model.App;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AppAdapter extends XmlAdapter<String, App> {

    @Override
    public String marshal(App app) throws Exception {
        return app.getId().toString();
    }

    @Override
    // We don't need to unmarshal for now
    public App unmarshal(String obj) throws Exception {
        return null;
    }
}