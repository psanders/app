/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
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