package org.example.lab1new;

import java.sql.*;

public class DatabaseInit {
    private static final String DB_URL = "jdbc:sqlite:D:\\games\\jetBrains\\IntelliJ IDEA Community Edition\\projects\\lab1-new\\game.db";

    // Метод для получения подключения к базе данных
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Инициализация базы данных и создание таблицы, если она ещё не существует
    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS players (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL UNIQUE," +
                    "wins INTEGER DEFAULT 0)";
            stmt.execute(sql);
            System.out.println("База данных и таблица успешно созданы или уже существуют.");
        } catch (SQLException e) {
            System.err.println("Ошибка инициализации базы данных: " + e.getMessage());
        }
    }
    public synchronized void savePlayer(String name) {
        try (Connection conn = DatabaseInit.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO players (name) VALUES (?)")) {
                pstmt.setString(1, name);
                pstmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Ошибка сохранения игрока: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
    }
}
