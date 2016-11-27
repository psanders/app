/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.core.api;

import com.fonoster.exception.ApiException;
import com.fonoster.model.App;
import com.fonoster.model.Script;
import com.fonoster.model.User;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class AppsAPI {
    private static final AppsAPI INSTANCE = new AppsAPI();
    final private Datastore ds = DBManager.getInstance().getDS();

    private AppsAPI() {}

    public static AppsAPI getInstance() {
        return INSTANCE;
    }

    public App createApp(User user, String name, String script) throws ApiException {
        if (user == null) throw new ApiException("Invalid user.");
        if (name == null || name.isEmpty()) throw new ApiException("Must provide name for the app.");

        App app = new App(user, name);

        Script main = new Script ("main.js", Script.Type.JAVASCRIPT);
        main.setSource(script);
        List<Script> scripts = app.getScripts();
        scripts.add(main);
        app.setScripts(scripts);

        ds.save(app);
        return app;
    }

    public App getAppById(User user, ObjectId id, boolean ignoreStatus) throws ApiException {

        if (user == null)
            throw new ApiException("Invalid User.");

        if (id == null)
            throw new ApiException("Invalid Id.");

        Query<App> q = ds.createQuery(App.class)
            .field("_id").equal(id)
            .field("user").equal(user);

        if(!ignoreStatus) {
            q.field("status").notEqual(App.Status.DELETED);
        }

        return q.get();
    }

    public List<App> getApps(User user,
        DateTime start,
        DateTime end,
        int maxResults,
        int firstResult,
        boolean starred,
        App.Status status) throws ApiException {

        if (user == null) throw new ApiException("Invalid user.");

        if (maxResults < 0) maxResults = 0;
        if (maxResults > 1000) maxResults = 1000;

        if (firstResult < 0) firstResult = 0;
        if (firstResult > 1000) firstResult = 1000;

        Query<App> q = ds.createQuery(App.class)
                .field("user").equal(user);

        // All recordings from start date
        if (start != null) {
            q.filter("created >=", start);
        }

        // All recordings until end date
        if (end != null) {
            q.filter("created <=", end);
        }

        if (starred) {
            q.field("starred").equal(true);
            q.field("status").equal(App.Status.NORMAL);
        } else {
            q.field("status").equal(status);
        }

        return q.limit(maxResults).offset(firstResult).asList();
    }
}
