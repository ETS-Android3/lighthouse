package com.github.chagall.lighthouse;

public class LinkModel {
    public String sender;
    public String link;
    public String originalMessage;

    public LinkModel(String sender, String link, String originalMessage) {
        this.sender = sender;
        this.link = link;
        this.originalMessage = originalMessage;
    }
}