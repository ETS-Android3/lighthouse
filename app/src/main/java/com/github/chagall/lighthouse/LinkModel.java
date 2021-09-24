package com.github.chagall.lighthouse;

public class LinkModel {
    public String sender;
    public String link;
    public String originalMessage;

    /**
     * Link object to store extraneous parameters for detected links including sender and original
     * message that the link was found in. Used to populate main activivty list object.
     * @param sender
     * @param link
     * @param originalMessage
     */

    public LinkModel(String sender, String link, String originalMessage) {
        this.sender = sender;
        this.link = link;
        this.originalMessage = originalMessage;
    }
}