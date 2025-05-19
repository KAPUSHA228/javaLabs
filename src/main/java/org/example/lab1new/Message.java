package org.example.lab1new;

/**
 * @param type "ACTION" или "GAME_INFO" или "DB_QUERY"
 * @param data JSON-строка с содержимым
 */
public record Message(MessageType type, String data) {

}
