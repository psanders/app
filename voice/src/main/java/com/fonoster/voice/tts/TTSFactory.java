package com.fonoster.voice.tts;

import com.fonoster.core.api.UsersAPI;
import com.fonoster.core.config.CoreConfig;
import com.fonoster.exception.ApiException;
import com.fonoster.model.User;
import com.fonoster.model.services.BluemixTTSService;
import com.fonoster.model.services.IvonaTTSService;
import com.fonoster.model.services.Service;

public class TTSFactory {
    CoreConfig config = CoreConfig.getInstance ();
    private User user;

    public TTSFactory(User user) {
        this.user = user;
    }

    public TTS getTTSEngine(String engine) throws ApiException {

        if (engine.equals("default")) return getDefaultTTS ();

        Service service = UsersAPI.getInstance().getService(user, engine);

        if (service instanceof BluemixTTSService) {
            BluemixTTSService bservice = (BluemixTTSService)service;
            return new BluemixTTS (bservice.getUsername(), bservice.getPassword());
        }

        if (service instanceof IvonaTTSService) {
            IvonaTTSService iservice = (IvonaTTSService)service;
            return new IvonaTTS (iservice.getAccessKey (), iservice.getSecretKey ());
        }

        throw new ApiException ("Unable to find " + engine + " in your catalog.");
    }

    public BluemixTTS getDefaultTTS() throws ApiException {
        return new BluemixTTS(config.getBluemixUsername(), config.getBluemixPassword());
    }
}
