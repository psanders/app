package com.fonoster.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Wrapper class for "lame".
 */
public class WavToMp3Util {
    private static final Logger LOG = LoggerFactory.getLogger(WavToMp3Util.class);

    public static boolean convert(String pathToFile) {
        StringBuilder line = new StringBuilder("lame -b 32 --resample 8 -a");
        line.append(" ");
        line.append(pathToFile);
        line.append(" ");
        line.append(pathToFile.replace(".wav", ".mp3"));

        CommandLine commandLine = CommandLine.parse(line.toString());
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        int exitValue = 0;
        try {
            exitValue = executor.execute(commandLine);
        } catch (IOException e) {
            LOG.error("Unable to convert this file.", e);
        }

        return exitValue == 0;
    }

}
