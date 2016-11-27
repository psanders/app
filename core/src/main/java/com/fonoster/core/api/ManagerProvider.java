/*
* Copyright (C) 2014 PhonyTive LLC
* http://fonoster.com
*
* This file is part of Fonoster
*/
package com.fonoster.core.api;

import com.fonoster.core.config.CoreConfig;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;

public class ManagerProvider {
    private final static ManagerProvider INSTANCE = new ManagerProvider();
    private static AsteriskServer asteriskServer;
    protected static CoreConfig config;

    private ManagerProvider() {
        config = CoreConfig.getInstance();
        asteriskServer = new DefaultAsteriskServer(
            config.getManagerHost(),
            config.getManagerUsername(),
            config.getManagerSecret());
    }

    public static ManagerProvider getInstance() {
        return INSTANCE;
    }

    public AsteriskServer getAsteriskServer() {
        return asteriskServer;
    }

}
