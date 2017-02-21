package com.cbtsoft.pokercenter.core.pojo;

public class Message {

    private String from;
    private int type;
    private Object messageBody;

    public Message(Object body) {
        this.type = 0;
        messageBody = body;
    }

    public Message(int type, Object body) {
        this.type = type;
        messageBody = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(Object messageBody) {
        this.messageBody = messageBody;
    }
}
