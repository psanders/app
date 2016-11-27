package com.fonoster.voice.tts;

import com.amazonaws.auth.BasicAWSCredentials;
import com.fonoster.config.CommonsConfig;
import com.fonoster.utils.SLNConverter;
import com.ivona.services.tts.IvonaSpeechCloudClient;
import com.ivona.services.tts.model.CreateSpeechRequest;
import com.ivona.services.tts.model.CreateSpeechResult;
import com.ivona.services.tts.model.Input;
import com.ivona.services.tts.model.Voice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class IvonaTTS extends AbstractTTS implements TTS {
    private static final Logger LOG = LoggerFactory.getLogger(IvonaTTS.class);
    private final static CommonsConfig commonsConfig = CommonsConfig.getInstance();
    private IvonaSpeechCloudClient tts;

    public IvonaTTS(String secretKey, String accessKey) {
        tts = new IvonaSpeechCloudClient(new BasicAWSCredentials (secretKey, accessKey));
        tts.setEndpoint("https://tts.eu-west-1.ivonacloud.com");
    }

    public String generate(String voice, String text) {

        String fn = getFilename(voice, text);
        String origin = "/tmp/".concat(fn).concat(".mp3");
        String dest = commonsConfig.getTTSStorePath().concat("/").concat(fn).concat(".sln16");

        if (new File(dest).exists()) return commonsConfig.getTTSStorePath().concat("/").concat(fn);
        File f = new File(origin);

        CreateSpeechRequest createSpeechRequest = new CreateSpeechRequest();
        Input input = new Input();
        Voice v = new Voice();

        v.setName(getVoice(voice));
        input.setData(text);

        createSpeechRequest.setInput(input);
        createSpeechRequest.setVoice(v);
        InputStream in;
        FileOutputStream outputStream;

        try {

            CreateSpeechResult createSpeechResult = tts.createSpeech(createSpeechRequest);

            in = createSpeechResult.getBody();
            outputStream = new FileOutputStream(f);

            byte[] buffer = new byte[2 * 1024];
            int readBytes;

            while ((readBytes = in.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }
            outputStream.close();

            SLNConverter.convert(origin, dest);
            f.deleteOnExit();
            return commonsConfig.getTTSStorePath().concat("/").concat(fn);
        } catch (IOException e) {
            LOG.error(e.getMessage ());
        }

        return null;
    }

    private String getVoice(String voice) {
        // Ensure is in voice list
        ArrayList<String> voices = new ArrayList<> ();
        voices.add("Nicole");
        voices.add("Enrique");
        voices.add("Agnieszka");
        voices.add("Tatyana");
        voices.add("Russell");
        voices.add("Lotte");
        voices.add("Geraint");
        voices.add("Carmen");
        voices.add("Mads");
        voices.add("Penelope");
        voices.add("Jennifer");
        voices.add("Brian");
        voices.add("Eric");
        voices.add("Ruben");
        voices.add("Ricardo");
        voices.add("Maxim");
        voices.add("Giorgio");
        voices.add("Carla");
        voices.add("Naja");
        voices.add("Maja");
        voices.add("Astrid");
        voices.add("Ivy");
        voices.add("Kimberly");
        voices.add("Chantal");
        voices.add("Amy");
        voices.add("Marlene");
        voices.add("Ewa");
        voices.add("Conchita");
        voices.add("Karl");
        voices.add("Miguel");
        voices.add("Mathieu");
        voices.add("Justin");
        voices.add("Chipmunk");
        voices.add("Jacek");
        voices.add("Ines");
        voices.add("Gwyneth");
        voices.add("Cristiano");
        voices.add("Celine");
        voices.add("Jan");
        voices.add("Liv");
        voices.add("Joey");
        voices.add("Raveena");
        voices.add("Filiz");
        voices.add("Dora");
        voices.add("Salli");
        voices.add("Vitoria");
        voices.add("Emma");
        voices.add("Hans");
        voices.add("Kendra");

        for (String v: voices) {
            if (v.equalsIgnoreCase(voice)) {
                return v;
            }
        }

        // XXX: Not sure about this behavior. Perhaps we should return an error if voice does not exist
        return "Salli";
    }
}