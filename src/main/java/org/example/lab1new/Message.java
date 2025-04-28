package org.example.lab1new;

public class Message {

    private final MessageType type; // "ACTION" или "GAME_INFO" или "DB_QUERY"
    private final String data; // JSON-строка с содержимым

    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

}
