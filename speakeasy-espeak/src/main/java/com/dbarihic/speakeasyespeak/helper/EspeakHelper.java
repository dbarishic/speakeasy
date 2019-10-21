package com.dbarihic.speakeasyespeak.helper;

import com.dbarihic.speakeasyespeak.domain.Voice;

import java.io.IOException;

/**
 * Reference: http://espeak.sourceforge.net/commands.html
 */

public class EspeakHelper {

    private static final String COMMAND_ESPEAK = "espeak";
    private Voice voice;

    public EspeakHelper() {
        this(new Voice());
    }

    public EspeakHelper(Voice voice) {
        this.voice = voice;
    }

    /**
     * Create a new espeak process
     *
     * @param text - the text to speak
     */
    public void speak(String text) {
        execute(COMMAND_ESPEAK,
                "-v", voice.getName() + voice.getVariant(),
                "-p", Integer.toString(voice.getPitch()),
                "-a", Integer.toString(voice.getAmplitude()),
                "-s", Integer.toString(voice.getSpeed()),
                "-g", Integer.toString(voice.getGap()),
                text);
    }

    /**
     * Create a new espeak process, save output to wave file
     *
     * @param text     - the text to speak
     * @param fileName - name of output file
     */
    public void speak(String text, String fileName) {
        execute(COMMAND_ESPEAK,
                "-v", voice.getName() + voice.getVariant(),
                "-p", Integer.toString(voice.getPitch()),
                "-a", Integer.toString(voice.getAmplitude()),
                "-s", Integer.toString(voice.getSpeed()),
                "-g", Integer.toString(voice.getGap()),
                "-w", fileName + ".wav",
                text);
    }

    private static void execute(final String... command) {
        String threadName = "espeak";

        new Thread(() -> {
            ProcessBuilder b = new ProcessBuilder(command);
            try {
                Process process = b.start();
                process.waitFor();
                process.destroy();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, threadName).start();
    }
}
