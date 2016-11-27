package com.fonoster.voice.config;

import java.net.URL;

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
