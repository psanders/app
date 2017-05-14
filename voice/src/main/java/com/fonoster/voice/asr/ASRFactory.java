/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
package com.fonoster.voice.asr;

import com.fonoster.annotations.Since;
import com.fonoster.core.api.UsersAPI;
import com.fonoster.exception.ApiException;
import com.fonoster.model.User;
import com.fonoster.model.services.BluemixSTTService;
import com.fonoster.model.services.Service;

@Since("1.0")
public class ASRFactory {
    private User user;

    public ASRFactory(User user) {
        this.user = user;
    }

    public ASR getASREngine(String engine) throws ApiException {

        if (engine.equals("default")) return getDefaultASR ();

        Service service = UsersAPI.getInstance().getService(user, engine);

        if (service instanceof BluemixSTTService) {
            BluemixSTTService bservice = (BluemixSTTService)service;
            return new BluemixASR (bservice.getUsername(), bservice.getPassword());
        }

        throw new ApiException ("Unable to find " + engine + " in your catalog.");
    }

    public BluemixASR getDefaultASR() throws ApiException {
        return new BluemixASR("046e9fbb-b0bb-42b6-aad1-37a55d37e0a1", "Z0acystUPg2P");
    }
}
