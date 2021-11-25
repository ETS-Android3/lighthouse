package com.github.raagavi158.lighthouse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LinkModel {
    public String sender;
    public String link;
    public String originalMessage;
    public String identifier;
    public String topline;
    public String rank;
    public int score;
    public JSONObject writeup;
    public ArrayList<MessageModel> messagesContainingDomain = new ArrayList<>();


    /**
     * Link object to store extraneous parameters for detected links including sender and original
     * message that the link was found in. Used to populate main activity list object.
     * @param sender
     * @param link
     * @param originalMessage
     */

    public LinkModel(String sender,
                     String link,
                     String originalMessage,
                     String identifier,
                     String topline,
                     String rank,
                     int score,
                     JSONObject writeup,
                     ArrayList<MessageModel> messagesContainingDomain
                     ) {

        this.sender = sender;
        this.link = link;
        this.originalMessage = originalMessage;
        this.identifier = identifier;
        this.topline = topline;
        this.rank = rank;
        this.score = score;
        this.writeup = writeup;
        this.messagesContainingDomain = messagesContainingDomain;
    }
}