/*
*Copyright (C) 2014 PhonyTive LLC
*http://fonoster.com
*
*This file is part of Fonoster
*/
package com.fonoster.core.api;

import com.fonoster.core.config.CoreConfig;
import com.fonoster.model.Account;
import com.fonoster.model.converters.BigDecimalConverter;
import com.fonoster.model.converters.DateTimeConverter;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.DefaultCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class DBManager {
    private static final Logger LOG = LoggerFactory.getLogger(DBManager.class);
    private static final CoreConfig config = CoreConfig.getInstance();
    private static final DBManager INSTANCE = new DBManager();
    private static Datastore ds;

    private DBManager() {
        ServerAddress addr = new ServerAddress(config.getDbHost(), config.getDbPort());
        List<MongoCredential> credentialsList = new ArrayList<>();
        MongoCredential credential = MongoCredential.createCredential(
                config.getDbUsername(), config.getDbName(), config.getDbSecret().toCharArray());

        LOG.info("Creating datastore for {"
                + " host: " + config.getDbHost ()
                + ", port: " + config.getDbPort ()
                + ", dbname: " + config.getDbName () + " }");

        credentialsList.add(credential);
        MongoClient client = new MongoClient(addr, credentialsList);
        Morphia morphia = new Morphia();

        // This is to ensure that morphia load entity classes while running in a custom ClassLoader.
        // Without this is impossible to run the Voice module inside Astive Server
        morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return Account.class.getClassLoader();
            }
        });

        morphia.map(com.fonoster.model.Account.class);
        morphia.map(com.fonoster.model.Activity.class);
        morphia.map(com.fonoster.model.App.class);
        morphia.map(com.fonoster.model.Broadcast.class);
        morphia.map(com.fonoster.model.CallDetailRecord.class);
        morphia.map(com.fonoster.model.CallStats.class);
        morphia.map(com.fonoster.model.PaymentInfo.class);
        morphia.map(com.fonoster.model.PhoneNumber.class);
        morphia.map(com.fonoster.model.Rate.class);
        morphia.map(com.fonoster.model.Recording.class);
        morphia.map(com.fonoster.model.ServiceProvider.class);
        morphia.map(com.fonoster.model.TrafficInfo.class);
        morphia.map(com.fonoster.model.services.Service.class);
        morphia.map(com.fonoster.model.services.BluemixTTSService.class);
        morphia.map(com.fonoster.model.services.BluemixSTTService.class);
        morphia.getMapper().getConverters().addConverter(BigDecimalConverter.class);
        morphia.getMapper().getConverters().addConverter(DateTimeConverter.class);

        ds = morphia.createDatastore(client, config.getDbName());
        ds.ensureIndexes();
        ds.ensureCaps();
    }

    public static DBManager getInstance() {
        return INSTANCE;
    }

    public Datastore getDS() {
        return ds;
    }
}
