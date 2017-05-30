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
import com.fonoster.core.config.CoreConfig;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;

@Since("1.0")
public class ManagerProvider {
    private final static ManagerProvider INSTANCE = new ManagerProvider();
    protected static CoreConfig config;
    private static AsteriskServer asteriskServer;

    private ManagerProvider() {
        config = CoreConfig.getInstance();
        asteriskServer = new DefaultAsteriskServer(config.getManagerHost(), config.getManagerUsername(), config.getManagerSecret());
    }

    public static ManagerProvider getInstance() {
        return INSTANCE;
    }

    public AsteriskServer getAsteriskServer() {
        return asteriskServer;
    }
}
