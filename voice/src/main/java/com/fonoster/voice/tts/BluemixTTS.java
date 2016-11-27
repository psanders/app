package com.fonoster.voice.tts;

import com.fonoster.config.CommonsConfig;
import com.fonoster.utils.SLNConverter;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class BluemixTTS extends AbstractTTS implements TTS {
    private static final Logger LOG = LoggerFactory.getLogger(BluemixTTS.class);
    private final static CommonsConfig commonsConfig = CommonsConfig.getInstance();
    TextToSpeech tts = new TextToSpeech();

    public BluemixTTS(String username, String password) {
        tts.setUsernameAndPassword(username, password);
    }

    // TODO: Add parameter "keep" to keep original files
    public String generate(String voice, String text) {
        try {
            String fn = getFilename(voice, text);
            String origin = "/tmp/".concat(fn).concat(".wav");
            String dest = commonsConfig.getTTSStorePath().concat("/").concat(fn).concat(".sln16");

            if (new File(dest).exists()) return commonsConfig.getTTSStorePath().concat("/").concat(fn);
            File f = new File(origin);

            try (OutputStream outputStream = new FileOutputStream(f)) {

                int read;
                byte[] bytes = new byte[1024];

                InputStream inputStream = tts.synthesize(text, getVoice(voice), AudioFormat.WAV).execute ();

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                outputStream.close();

                SLNConverter.convert(origin, dest);
                f.deleteOnExit();
                return commonsConfig.getTTSStorePath().concat("/").concat(fn);
            }
        } catch (FileNotFoundException e) {
            LOG.error("Unable to find file.", e);
        } catch (IOException e) {
            LOG.error("Something happen while converting the file.", e);
        }
        return null;
    }

    private Voice getVoice(String voice) {
        Voice dVoice = Voice.EN_ALLISON;

        if (voice == null || voice.isEmpty()) return dVoice;

        voice = voice.toLowerCase ();

        switch (voice) {
            case ("dieter"):
                return Voice.DE_DIETER;
            case ("girgit"):
                return Voice.DE_GIRGIT;
            case ("allison"):
                return Voice.EN_ALLISON;
            case ("lisa"):
                return Voice.EN_LISA;
            case ("enrique"):
                return Voice.ES_ENRIQUE;
            case ("laura"):
                return Voice.ES_LAURA;
            case ("sofia"):
                return Voice.ES_SOFIA;
            case ("renee"):
                return Voice.FR_RENEE;
            case ("kate"):
                return Voice.GB_KATE;
            case ("francesca"):
                return Voice.IT_FRANCESCA;
            default:
                return dVoice;
        }
    }
}