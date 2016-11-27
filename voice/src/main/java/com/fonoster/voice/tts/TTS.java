package com.fonoster.voice.tts;

public interface TTS {

    String generate(String voice, String text);

    String getFilename(String voice, String text) ;
}
