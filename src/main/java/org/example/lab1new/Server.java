package org.example.lab1new;

import com.google.gson.Gson;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private boolean isRunning1;
    private boolean isRunning2;
    private static final int port = 3124;
    private final Model m = BModel.build();
    private final Gson json = new Gson();
    private final ArrayList<ClientConnect> list = new ArrayList<>();

    Server() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        m.getAllInfo().setGameStarted(false);
        m.getAllInfo().setGameFollow(false);
        isRunning1 = false;
        isRunning2 = false;
        m.getAllInfo().setPaused(false);
        m.getAllInfo().setDirection1((byte) 1);
        m.getAllInfo().setDirection2((byte) 1);
        new Thread(() -> moveCircle1(m.getAllInfo().getDirection1())).start();
        new Thread(() -> moveCircle2(m.getAllInfo().getDirection2())).start();
        //new Thread(this::gameLoop).start();
        try {
            printNetworkInterfaces();
            ServerSocket ss = new ServerSocket(port, 0, InetAddress.getLocalHost());
            HibernateUtil.getSessionFactory();
            System.out.println("Server started on " + InetAddress.getLocalHost() + "\n");
            String msg;
            while (true) {
                Socket cs = ss.accept();
                try {
                    DataInputStream dis = new DataInputStream(cs.getInputStream());
                    DataOutputStream dos = new DataOutputStream(cs.getOutputStream());
                    msg = dis.readUTF();
                    if (msg.equals("OBSERVER")) {
                        System.out.println("Mobile client connected: " + cs.getInetAddress());
                        try {
                            ArrayList<Player> list = getLeaderboard();
                            dos.writeUTF(json.toJson(list));
                            dos.flush();
                        } catch (IOException e) {
                            System.err.println("Не удалось отправить сообщение: " + e.getMessage());
                        } finally {
                            try {
                                cs.close();
                            } catch (IOException e) {
                                System.err.println("Ошибка при закрытии сокета: " + e.getMessage());
                            }
                        }
                    } else {
                        if (list.size() < 4) {
                            m.addServers(() -> {
                            });
                            int clientIndex;
                            synchronized (list) {
                                clientIndex = list.size();
                                ClientConnect cc = new ClientConnect(cs, true, m, this, clientIndex);
                                System.out.println("CID " + cc.getID());
                                System.out.println("SERVER's MODEL " + m.getAllInfo().getScores());
                                System.out.println("Connected to: " + cs.getPort());
                                list.add(cc);
                                if (m.getAllInfo().isGameStarted()) {
                                    m.getAllInfo().setReady(clientIndex, true);
                                }
                            }
                        } else {
                            cs.close();
                            System.out.println("Server is full. Connection rejected.");
                        }
                    }
                } catch (SocketTimeoutException e) {
                    System.err.println("Таймаут при чтении данных от клиента");
                    cs.close();
                } catch (IOException e) {
                    System.err.println("Ошибка ввода-вывода: " + e.getMessage());
                    try { cs.close(); } catch (IOException ignored) {}
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }


    public synchronized void savePlayer(String name) {
        DatabaseInit.savePlayer(name);
    }

    public synchronized void incrementWins(String name) {
        DatabaseInit.incrementWins(name);
    }

    private void printNetworkInterfaces() {
        try {
            System.out.println("Доступные сетевые интерфейсы:");
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;

                System.out.println("\nИнтерфейс: " + iface.getDisplayName());
                Enumeration<InetAddress> addresses = iface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) { // Только IPv4
                        System.out.println("  IP: " + addr.getHostAddress());
                    }
                }
            }

            // Основной IP для подключения
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("\nСервер ожидает подключений на: " +
                    ip.getHostAddress() + ":" + port);
        } catch (SocketException | UnknownHostException e) {
            System.out.println("Ошибка при определении сетевых интерфейсов: " + e.getMessage());
        }
    }

    public synchronized ArrayList<Player> getLeaderboard() {
        return (ArrayList<Player>) DatabaseInit.getLeaderboard();
    }

    void setModel(Model m) {
        this.m.setModel(m);
    }

    Model getModel() {
        return m;
    }

    private void gameLoop() {
        while (true) {
            if (m.getAllInfo().isGameStarted() && !m.getAllInfo().isPaused()) {
                List<Bullet> bullets = m.getAllInfo().getActiveBullets();
                Iterator<Bullet> iterator = bullets.iterator();
                while (iterator.hasNext()) {
                    Bullet bullet = iterator.next();
                    bullet.update();

                    // Проверка столкновений с мишенями
                    if (checkCollision(bullet, m.getAllInfo().getC1())) {
                        m.getAllInfo().IncreaseScoreI(bullet.getOwnerId(), 2);
                        iterator.remove();
                    } else if (checkCollision(bullet, m.getAllInfo().getC2())) {
                        m.getAllInfo().IncreaseScoreI(bullet.getOwnerId(), 1);
                        iterator.remove();
                    }
                    // Удаление пуль, вышедших за границы
                    if (bullet.getX() < 0 || bullet.getX() > 800) { // Предполагаемая ширина окна 800
                        iterator.remove();
                    }
                }
                broadcast(); // Рассылаем обновлённое состояние клиентам
            }
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                System.out.println("Game loop interrupted");
            }
        }
    }

    private boolean checkCollision(Bullet bullet, MyCircle target) {
        double dx = bullet.getX() - target.getCenterX();
        double dy = bullet.getY() - target.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= bullet.getRadius() + target.getRadius();
    }

    public void setFollowing(boolean k) {
        m.getAllInfo().setGameFollow(k);
    }

    public void setStarting(boolean k) {
        m.getAllInfo().setGameStarted(k);
    }

    public void togglePaused() {
        m.getAllInfo().setPaused(!m.getAllInfo().isPaused());
        System.out.println("SERVER PAUSE " + m.getAllInfo().isPaused());
    }

    public boolean getFollowing() {
        return m.getAllInfo().isGameFollow();
    }

    public boolean getStarting() {
        return m.getAllInfo().isGameStarted();
    }

    public boolean getPaused() {
        return m.getAllInfo().isPaused();
    }

    public void moveCircle1(byte direction) {
        byte speed = 5;
        byte direction1 = direction;
        if (!isRunning1) {
            isRunning1 = true;

            while (m.getAllInfo().isGameFollow() && !m.getAllInfo().isPaused()) {
                double newY = m.getAllInfo().getC1().getCenterY() + speed * direction1;
                if (newY <= 0 || newY >= 286.4 - 30) {
                    direction1 *= -1;
                }
                m.getAllInfo().getC1().setCenterY(newY);
                broadcast();
                m.getAllInfo().setDirection1(direction1);
                try {
                    Thread.sleep(16); // ~60 FPS (1000ms / 60 = 16ms)
                } catch (InterruptedException e) {
                    System.out.println("Sleep error2");
                }
            }
            isRunning1 = false;
        }
    }

    public void moveCircle2(byte direction) {
        byte speed = 2;
        byte direction2 = direction;
        if (!isRunning2) {
            isRunning2 = true;
            while (m.getAllInfo().isGameFollow() && !m.getAllInfo().isPaused()) {
                double newY = m.getAllInfo().getC2().getCenterY() + speed * direction2;
                if (newY <= 0 || newY >= 286.4 - 50) {
                    direction2 *= -1;
                }
                m.getAllInfo().getC2().setCenterY(newY);
                broadcast();
                m.getAllInfo().setDirection2(direction2);
                try {
                    Thread.sleep(16); // ~60 FPS (1000ms / 60 = 16ms)
                } catch (InterruptedException e) {
                    System.out.println("Sleep error3");
                }
            }
            isRunning2 = false;
        }
    }

    public void broadcast() {
        System.out.println(m.toString());
        list.forEach(client -> client.sendMessage(new Message(MessageType.GameInfo, json.toJson(m.getAllInfo()))));
    }

    public static void main(String[] args) {
        new Server();
    }

    public boolean checkReady() {
        for (Boolean i : m.getAllInfo().getReady())
            if (!i) return false;
        return true;

    }

    public void end() {
        m.getAllInfo().ResetStatistic();
        m.getAllInfo().setGameStarted(false);
        m.getAllInfo().setGameFollow(false);
        isRunning1 = false;
        isRunning2 = false;
        m.getAllInfo().setPaused(false);
        m.getAllInfo().setDirection1((byte) 1);
        m.getAllInfo().setDirection2((byte) 1);
        for (int i = 0; i < m.getAllInfo().getReady().size(); i++) {
            m.getAllInfo().setReady(i, false);
        }
        broadcast();

    }
}