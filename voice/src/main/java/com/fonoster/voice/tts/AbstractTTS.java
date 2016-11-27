package com.fonoster.voice.tts;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AbstractTTS {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTTS.class);

    // This app is use by FonosterJS to re-gen the audio's id.
    public String getFilename(String voice, String text) {
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update((voice.concat(text)).getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = messageDigest.digest();
            final String r = new String(Hex.encodeHex(resultByte));
            return r;
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Unable to find algorithm MD5.");
        }
        return null;
    }

    protected AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
