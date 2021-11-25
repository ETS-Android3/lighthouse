package com.github.raagavi158.lighthouse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageModel {
    public String sender;
    public String link;
    public String originalMessage;

    /**
     * Link object to store extraneous parameters for detected links including sender and original
     * message that the link was found in. Used to populate main activity list object.
     * @param sender
     * @param link
     * @param originalMessage
     */

    public MessageModel(String sender,
                        String link,
                        String originalMessage
                        ) {

        this.sender = sender;
        this.link = link;
        this.originalMessage = originalMessage;
    }
}