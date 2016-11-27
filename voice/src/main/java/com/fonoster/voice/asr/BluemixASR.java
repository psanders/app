package com.fonoster.voice.asr;

import com.fonoster.config.CommonsConfig;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;

public class BluemixASR implements ASR {
    private static final Logger LOG = LoggerFactory.getLogger(BluemixASR.class);
    CommonsConfig config = CommonsConfig.getInstance ();
    private SpeechToText asr = new SpeechToText();
    private String model;
    private String format;

    public BluemixASR(String username, String password) {
        asr.setUsernameAndPassword(username, password);
        asr.setEndPoint("https://stream.watsonplatform.net/speech-to-text/api");
        this.model = "en-US_NarrowbandModel";
        this.format = HttpMediaType.AUDIO_WAV;
    }

    public void transcribe(String file, JSFunc func) {
        CountDownLatch lock = new CountDownLatch(1);

        FileInputStream audio = null;
        try {
            audio = new FileInputStream (file);
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        }

        RecognizeOptions options = new RecognizeOptions.Builder()
            .maxAlternatives(0)
            .interimResults(false)
            .continuous(true)
            .model(getModel())
            .contentType(getFormat())
            .build();

        assert audio != null;

        asr.recognizeUsingWebSocket(audio, options, new BaseRecognizeCallback () {
            @Override
            public void onTranscription(SpeechResults speechResults) {

                if (speechResults.getResults().size() > 0
                        && speechResults.getResults().get(0).isFinal()) {
                    SpeechAlternative sa = speechResults.getResults().get(0).getAlternatives().get(0);
                    func.alternative(sa);
                    lock.countDown();
                }
            }

            @Override
            public void onError(Exception e) {
                LOG.error(e.getMessage());
            }
        });

        try {
            lock.await();
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public interface JSFunc {
        void alternative(SpeechAlternative sa);
    }
}
