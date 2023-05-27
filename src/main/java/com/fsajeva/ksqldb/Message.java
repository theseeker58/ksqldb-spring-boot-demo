package com.fsajeva.ksqldb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    private String sender;
    private String sequence;
    private String text;

    /* Queste annotazioni sono superflue se si definisce un default constructor
    quindi hanno senso solo nel caso di immutable */
    @JsonCreator
    public Message(@JsonProperty("sender") String sender, @JsonProperty("sequence") String sequence,
                   @JsonProperty("text") String text) {
        super();
        this.sender = sender;
        this.sequence = sequence;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public String getSequence() {
        return sequence;
    }

    public String getText() {
        return text;
    }
 }
