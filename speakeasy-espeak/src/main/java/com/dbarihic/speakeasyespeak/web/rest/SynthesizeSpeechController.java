package com.dbarihic.speakeasyespeak.web.rest;

import com.dbarihic.speakeasyespeak.domain.Voice;
import com.dbarihic.speakeasyespeak.helper.EspeakHelper;
import com.dbarihic.speakeasyespeak.model.Language;
import com.dbarihic.speakeasyespeak.model.SynthesizeSpeechRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController()
@RequestMapping("/speech")
public class SynthesizeSpeechController {

    private static final EspeakHelper espeakHelper = new EspeakHelper();
    private static final ObjectMapper mapper = new ObjectMapper();

    @CrossOrigin(origins = "*")
    @PostMapping("/synthesize")
    public String synthesizeSpeech(@RequestBody SynthesizeSpeechRequest request) throws IOException, InterruptedException {
        Voice voice = new Voice();
        voice.setName(request.getLanguage());

        espeakHelper.setVoice(voice);
        File file = espeakHelper.speakToFile(request.getText(), "test");

        byte[] fileContent = FileUtils.readFileToByteArray(file);
        String base64 = Base64.getEncoder().encodeToString(fileContent);
        file.delete();

        return base64;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/list-voices")
    public ResponseEntity<Map<String, List<Language>>> listVoices() throws IOException, InterruptedException {

        Map<String, List<Language>> response = new HashMap<>();
        List<Language> languages = new ArrayList<>();

        String[] voices = espeakHelper.listVoices();

        for (String voice : voices) {
            Language language = new Language();
            language.setCode(voice);
            language.setName(voice);
            languages.add(language);
        }

        response.put("languages", languages);
        return ResponseEntity.ok(response);
    }
}
