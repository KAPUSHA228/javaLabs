package org.example.lab1new;

public class Message {
    private final String type; // "ACTION" или "GAME_INFO"
    private final String data; // JSON-строка с содержимым

    public Message(String type, String data) {
        this.type = type;
        this.data = data;
    }

    // Геттеры и сеттеры
    public String getType() { return type; }
    public String getData() { return data; }
}
