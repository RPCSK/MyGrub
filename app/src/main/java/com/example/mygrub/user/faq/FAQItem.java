package com.example.mygrub.user.faq;

public class FAQItem {

    private String key, question, answer;

    public FAQItem() {
    }

    public FAQItem(String key, String question, String answer) {
        this.key = key;
        this.question = question;
        this.answer = answer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
