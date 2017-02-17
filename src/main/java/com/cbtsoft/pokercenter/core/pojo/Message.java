package com.cbtsoft.pokercenter.core.pojo;

public class Message {
    public enum Type {
        GLOBAL,
        ROOM,
        PRIVATE
    }
    private String from;
    private Type type;
    private String messageBody;

    public Message(String body) {
        messageBody = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
