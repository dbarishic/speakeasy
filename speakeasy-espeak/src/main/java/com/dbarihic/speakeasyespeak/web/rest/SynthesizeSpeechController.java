package com.dbarihic.speakeasyespeak.web.rest;

import com.dbarihic.speakeasyespeak.domain.Voice;
import com.dbarihic.speakeasyespeak.helper.EspeakHelper;
import com.dbarihic.speakeasyespeak.model.SynthesizeSpeechRequest;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@RestController()
@RequestMapping("/speech")
public class SynthesizeSpeechController {

    private static final EspeakHelper espeakHelper = new EspeakHelper();

    @PostMapping("/synthesize")
    public String synthesizeSpeech(@RequestBody SynthesizeSpeechRequest request) throws IOException, InterruptedException {
        Voice voice = new Voice();
        voice.setName(request.getLanguageCode());

        espeakHelper.setVoice(voice);
        File file = espeakHelper.speakToFile(request.getText(), "test");

        byte[] fileContent = FileUtils.readFileToByteArray(file);
        String base64 = Base64.getEncoder().encodeToString(fileContent);
        file.delete();

        return base64;
    }
}
