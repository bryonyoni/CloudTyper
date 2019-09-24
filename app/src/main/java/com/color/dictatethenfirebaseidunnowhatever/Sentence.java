package com.color.dictatethenfirebaseidunnowhatever;

import java.util.List;

public class Sentence {
    private String id;
    private List<Word> words;
    private String sentence;

    public Sentence(){}

    public Sentence(String id){
        this.id = id;
    }

    public Sentence (String id, String sentence){
        this.id = id;
        this.sentence = sentence;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
