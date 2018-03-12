/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
package com.fonoster.core.api;

import com.fonoster.annotations.Since;
import com.fonoster.exception.ApiException;
import com.fonoster.exception.ResourceNotFoundException;
import com.fonoster.model.Peer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.validation.Validation;
import javax.validation.Validator;
import java.net.UnknownHostException;

@Since("1.0")
public class PeersAPI {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static PeersAPI instance;
    private static Datastore ds;

    private PeersAPI() {
    }

    public static PeersAPI getInstance() throws ApiException {
        if (instance == null || ds == null) {
            try {
                ds = DBManager.getInstance().getDS();
                instance = new PeersAPI();
            } catch (UnknownHostException e) {
                throw new ApiException();
            }
        }
        return instance;
    }

    public Peer getPeerById(ObjectId peerId, boolean ignoreDeleted) throws ResourceNotFoundException {
        Query<Peer> q = ds.createQuery(Peer.class)
            .field("id").equal(peerId);

        if (!ignoreDeleted) {
            q.field("deleted").notEqual(true);
        }

        Peer peer = q.get();

        if (peer == null) throw new ResourceNotFoundException();

        return peer;
    }

}
