/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.core.api;

import com.fonoster.model.Broadcast;
import com.fonoster.model.User;
import org.mongodb.morphia.Datastore;

import java.util.List;

public class MiscAPI {
    private static final MiscAPI INSTANCE = new MiscAPI();
    private static final Datastore ds = DBManager.getInstance().getDS();

    private MiscAPI() {
    }

    public static MiscAPI getInstance() {
        return INSTANCE;
    }

    public void sendBroadcast(String message) {
        Broadcast g = new Broadcast();
        g.setMessage(message);

        List<User> users = ds.createQuery(User.class).asList();
        for (User user : users) {
            user.setCheckedGlobalMessage(false);
            ds.save(user);
        }

        ds.save(g);
    }

    public Broadcast getBroadcast() {
        return ds.find(Broadcast.class).order("-created").get();
    }

}
