package org.example.lab1new;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInit {
    public static synchronized List<Player> getLeaderboard() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Player ORDER BY wins DESC", Player.class)
                    .setMaxResults(10)
                    .list();
        } catch (Exception e) {
            System.err.println("Ошибка получения таблицы лидеров: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static synchronized boolean isNameUnique(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(p) FROM Player p WHERE p.name = :name", Long.class)
                    .setParameter("name", name)
                    .uniqueResult();
            return count == 0;
        } catch (Exception e) {
            System.err.println("Ошибка проверки имени: " + e.getMessage());
            return false;
        }
    }

    public static synchronized void savePlayer(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (!isNameUnique(name)) {
                return;
            }
            Transaction tx = session.beginTransaction();
            Player player = new Player();
            player.setName(name);
            player.setWins(0);
            session.save(player);
            tx.commit();
        } catch (Exception e) {
            System.err.println("Ошибка сохранения игрока: " + e.getMessage());
        }
    }

    public static synchronized void incrementWins(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Player player = session.createQuery("FROM Player WHERE name = :name", Player.class)
                    .setParameter("name", name)
                    .uniqueResult();
            if (player != null) {
                player.setWins(player.getWins() + 1);
                session.update(player);
            }
            tx.commit();
        } catch (Exception e) {
            System.err.println("Ошибка обновления побед: " + e.getMessage());
        }
    }
}