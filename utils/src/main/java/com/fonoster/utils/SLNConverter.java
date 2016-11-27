package com.fonoster.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SLNConverter {
    private static final Logger LOG = LoggerFactory.getLogger(SLNConverter.class);

    public static boolean convert(String origin, String dest) {
        String originFormat = null;
        if(origin.endsWith (".wav")) {
            originFormat = "wav";
        } else if(origin.endsWith (".mp3")) {
            originFormat = "mp3";
        }

        CommandLine cmd = CommandLine.parse("avconv -i " + origin + " -ar 16000 -acodec pcm_s16le -f s16le " + dest);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        int exitValue = 0;

        try {
            exitValue = executor.execute(cmd);
            return exitValue == 0;
        } catch (IOException e) {
            LOG.error("Unable to convert this file. Perhaps 'avconv' is not available.");
        }

        cmd = CommandLine.parse("ffmpeg -i " + origin + " -ar 16000 -acodec pcm_s16le -f s16le " + dest);

        try {
            exitValue = executor.execute(cmd);
        } catch (IOException e) {
            LOG.error("Unable to convert this file. Perhaps 'ffmpeg' is not available.");
        }

        return exitValue == 0;
    }
}
