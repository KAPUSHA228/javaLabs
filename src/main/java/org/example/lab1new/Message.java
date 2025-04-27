package org.example.lab1new;

public class Message {

    private String type; // "ACTION" или "GAME_INFO"
    private String data; // JSON-строка с содержимым

    public Message(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

}
