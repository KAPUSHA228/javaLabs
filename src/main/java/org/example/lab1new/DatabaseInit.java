package org.example.lab1new;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static synchronized ArrayList<Player> getLeaderboard() {
        ArrayList<Player> leaders = new ArrayList<>();
        try (Connection conn = DatabaseInit.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name, wins FROM players ORDER BY wins DESC LIMIT 10"))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                leaders.add(new Player(rs.getString("name"), rs.getInt("wins")));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения таблицы лидеров: " + e.getMessage());
        }
        System.out.println("DB SZ "+leaders.size());
        return leaders;
    }

    public static synchronized boolean isNameUnique(String name) {
        try (Connection conn = DatabaseInit.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM players WHERE name = ?")){
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            System.err.println("Ошибка проверки имени: " + e.getMessage());
            return false;
        }
    }

    public static synchronized void savePlayer(String name) {
        try (Connection conn = DatabaseInit.getConnection()){
            if (!isNameUnique(name)) {
                //System.err.println("Такой игрок уже существует");
                return;
            }
            System.out.println("MYNAME"+ name);
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO players (name) VALUES (?)")) {
                ps.setString(1, name);
                int affectedRows = ps.executeUpdate();
                conn.commit();
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected");
                }
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Ошибка сохранения игрока: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
    }

    public static synchronized void incrementWins(String name) {
        try(Connection conn = DatabaseInit.getConnection()) {
            conn.setAutoCommit(false);
            try(PreparedStatement ps = conn.prepareStatement("UPDATE players SET wins = wins + 1 WHERE name = ?")) {
                ps.setString(1, name);
                int affectedRows = ps.executeUpdate();
                conn.commit();
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected");
                }
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Ошибка обновления побед: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
    }
}
