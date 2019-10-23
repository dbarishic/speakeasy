package com.dbarihic.speakeasyespeak.helper;

import com.dbarihic.speakeasyespeak.domain.Voice;

import java.io.File;
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
     *  @param text     - the text to speak
     * @param fileName - name of output file
     * @return
     */
    public File speakToFile(String text, String fileName) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder(COMMAND_ESPEAK,
                "-v", voice.getName() + voice.getVariant(),
                "-p", Integer.toString(voice.getPitch()),
                "-a", Integer.toString(voice.getAmplitude()),
                "-s", Integer.toString(voice.getSpeed()),
                "-g", Integer.toString(voice.getGap()),
                "-w", fileName + ".wav",
                text);

        Process p = processBuilder.start();
        p.waitFor();

        return new File(fileName + ".wav");
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

    public void setVoice(Voice voice) {
        this.voice = voice;
    }
}
