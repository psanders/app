/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com>
 * https://fonoster.com
 *
 * This file is part of Fonoster
 *
 * Fonoster can not be copied and/or distributed without the express
 * permission of Fonoster's copyright owners.
 */
package com.fonoster.voice.config;

import com.fonoster.annotations.Since;

import java.net.URL;

@Since("1.0")
public class VoiceConfig {
    private static final VoiceConfig INSTANCE = new VoiceConfig();
    private URL coreLib =  getClass().getClassLoader().getResource("core.js");

    private VoiceConfig() {
    }

    public URL getCoreLib() {
        return coreLib;
    }

    public static VoiceConfig getInstance() {
        return INSTANCE;
    }
}
