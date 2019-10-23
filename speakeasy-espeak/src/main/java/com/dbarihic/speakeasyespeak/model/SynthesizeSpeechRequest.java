package com.dbarihic.speakeasyespeak.model;

public class SynthesizeSpeechRequest {
    private String text;
    private String languageCode;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
