package com.fonoster.core;

import com.fonoster.core.api.UsersAPI;
import com.fonoster.model.User;
import com.fonoster.model.services.BluemixTTSService;
import com.fonoster.model.services.IvonaTTSService;
import com.fonoster.voice.tts.BluemixTTS;
import com.fonoster.voice.tts.IvonaTTS;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class ITTTS {

    @Test
    public void testBluemixTTS() throws Exception {
        User john = UsersAPI.getInstance().getUserByEmail("john@doe.com");

        BluemixTTSService service = (BluemixTTSService) UsersAPI.getInstance().getService(john, "bluemix-tts");
        BluemixTTS tts = new BluemixTTS (service.getUsername(), service.getPassword());

        String result = tts.generate("enrique", "En hora buena!");
        assertTrue("Could not find file " + result.concat(".sln16"), new File(result.concat(".sln16")).exists());
    }

    @Test
    public void testIvonaTTS() throws Exception {
        User john = UsersAPI.getInstance().getUserByEmail("john@doe.com");

        IvonaTTSService service = (IvonaTTSService) UsersAPI.getInstance().getService(john, "ivona-tts");
        IvonaTTS tts = new IvonaTTS (service.getAccessKey(), service.getSecretKey());

        String result = tts.generate("salli", "Hello, I'm sally!");
        assertTrue("Could not find file " + result.concat(".sln16"), new File(result.concat(".sln16")).exists());
    }
}
