package com.fonoster.voice.asr;

public interface ASR {

    void transcribe(String file, BluemixASR.JSFunc callback);
}
