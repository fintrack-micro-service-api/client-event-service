package com.example.clienteventservice;

public class KafkaMessage {
    private String content;

    public KafkaMessage() {
    }

    public KafkaMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
