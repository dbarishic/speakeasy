package com.dbarihic.speakeasyespeak.helper;

import com.dbarihic.speakeasyespeak.domain.Voice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

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

    public String[] listVoices() throws InterruptedException, IOException {
        String[] cmd = {
                "/bin/sh",
                "-c",
                "espeak --voices | awk '{print $2}'"
        };
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);

        Process p = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader((p.getInputStream())));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while ( (line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(System.getProperty("line.separator"));
        }

        String espeakResult = stringBuilder.toString();
        String[] tokenized = espeakResult.split("\n");
        String[] result = Arrays.copyOfRange(tokenized, 1, tokenized.length);

        p.waitFor();

        return result;
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
