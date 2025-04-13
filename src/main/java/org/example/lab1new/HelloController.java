package org.example.lab1new;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloController implements IObserver {


    private byte direction1;
    private byte direction2;
    Model m = BModel.build();
    ClientConnect cc;
    private static final int port = 3124;
    private Socket cs;
    InetAddress ip = null;
    @FXML
    public Label id;
    @FXML
    public Pane statMenu;
    @FXML
    private Button connecting;
    @FXML
    private Pane parentWindow;
    @FXML
    private Polygon shooting;
    @FXML
    private Pane pane;
    @FXML
    private Circle circle1;
    @FXML
    private Circle circle2;

    @Override
    public void event() {
        Platform.runLater(() -> {
            if (m.getAllInfo().isGameStarted()&&!m.getAllInfo().isPaused()) {
                pane.setOnMouseMoved(this::handleMouseMove);
            } else {
                pane.setOnMouseMoved(null);
            }
            statMenu.getChildren().clear();
            double startX = 10;
            double spacing = 10;
            for (int i = 0; i < m.getAllInfo().getScores().size(); i++) {
                VBox vbox = new VBox();
                String id = String.valueOf(i);
                String score = String.valueOf(m.getAllInfo().getScoreI(i));
                String shot = String.valueOf(m.getAllInfo().getShotI(i));
                vbox.setStyle("-fx-border-color: black; -fx-padding: 10;");
                vbox.setPrefHeight(82.0);
                vbox.setPrefWidth(120.00);
                vbox.setLayoutX(startX + (vbox.getPrefWidth() + spacing) * i);
                Label l1 = new Label("Номер игрока: " + id);
                Label l2 = new Label("Счет игрока: " + score);
                Label l3 = new Label("Выстрелов: " + shot);
                vbox.getChildren().addAll(l1, l2, l3);
                statMenu.getChildren().add(vbox); // Добавляем Label в контейнер
            }
            circle1.setCenterY(m.getAllInfo().getC1().getCenterY());
            circle2.setCenterY(m.getAllInfo().getC2().getCenterY());
        });

    }

    @FXML
    public void initialize() {
        m.addServers(this);
    }

    @FXML
    public void connect() {
        try {
            System.out.println(parentWindow.getWidth() + " SUKA " + parentWindow.getHeight());
            connecting.setDisable(true);
            connecting.setVisible(false);
            ip = InetAddress.getLocalHost();
            cs = new Socket(ip, port);
            cc = new ClientConnect(cs, false);
            cc.sendAction(new ActionMsg(ActionType.UPDMODEL));
            m.setInfo(cc.getInfo());
            id.setText(String.valueOf(cc.getID()));
            System.out.println("IGET" + m.getAllInfo().getScoreI(0));
        } catch (IOException e) {
            System.out.println("Error2");
        }

    }

    @FXML
    protected void toStartGame() {
        cc.sendAction(new ActionMsg(ActionType.START));
        direction1 = 1;
        direction2 = 1;
        pane.setOnMouseMoved(this::handleMouseMove);
    }

    private void handleMouseMove(MouseEvent event) {
        double mouseX = Math.max(0, Math.min(event.getX(), pane.getWidth()));
        double mouseY = Math.max(0, Math.min(event.getY(), pane.getHeight()));


        List<Double> points = shooting.getPoints();

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (int i = 0; i < points.size(); i += 2) {
            double x = points.get(i);
            double y = points.get(i + 1);

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }

        double offsetX = minX;
        double offsetY = minY;

        double polygonWidth = maxX - minX;
        double polygonHeight = maxY - minY;

        double newX = mouseX - (polygonWidth / 2 + offsetX);
        double newY = mouseY - (polygonHeight / 2 + offsetY);
        double paneWidth = pane.getWidth();
        double paneHeight = pane.getHeight();
        newX = Math.max(0, Math.min(newX, paneWidth - polygonWidth));
        newY = Math.max(0, Math.min(newY, paneHeight - polygonHeight));

        shooting.setLayoutX(newX);
        shooting.setLayoutY(newY);
    }

    @FXML
    protected void handlePolygonClick() {
        if (m.getAllInfo().isGameStarted() && !m.getAllInfo().isPaused()) {
            cc.sendAction(new ActionMsg(ActionType.UPDSH));
            Circle bullet = new Circle(5);

            double initialX = pane.getLayoutX() + shooting.getLayoutX() + 20;
            double initialY = shooting.getLayoutY() + 10;

            if (initialX < 0 || initialX > parentWindow.getWidth()) {
                parentWindow.getChildren().remove(bullet);
                return;
            }

            bullet.setCenterX(initialX);
            bullet.setCenterY(initialY);
            bullet.setFill(Color.BLUEVIOLET);
            parentWindow.getChildren().add(bullet);
            new Thread(() -> moveBullet(bullet)).start();
        }
    }

    private void moveBullet(Circle bullet) {
        int speed = 5;
        double bulletRadius = bullet.getRadius();
        AtomicBoolean isBulletMoving = new AtomicBoolean(true);

        while (isBulletMoving.get() && !m.getAllInfo().isPaused()) {
            double newX = bullet.getCenterX() + speed;
            double newY = bullet.getCenterY();

            double goalX1 = circle1.getCenterX();
            double goalY1 = circle1.getCenterY();
            double r1 = circle1.getRadius();
            double goalX2 = circle2.getCenterX();
            double goalY2 = circle2.getCenterY();
            double r2 = circle2.getRadius();


            if (newX <= 0 || newX >= parentWindow.getWidth() - 100) {
                isBulletMoving.set(false);
                Platform.runLater(() -> {
                    parentWindow.getChildren().remove(bullet);
                    System.out.println("bullet remove 1");
                });
            } else {
                if (checkCollision(newX, newY, bulletRadius, goalX1, goalY1, r1)) {
                    isBulletMoving.set(false);
                    Platform.runLater(() -> {
                        parentWindow.getChildren().remove(bullet);
                        System.out.println("bullet remove 2");
                        System.out.println(newX + " " + newY + " " + bulletRadius + " " + goalX1 + " " + goalY1 + " " + r1);
                        cc.sendAction(new ActionMsg(ActionType.UPDSC2));
                    });

                } else {
                    if (checkCollision(newX, newY, bulletRadius, goalX2, goalY2, r2)) {
                        isBulletMoving.set(false);
                        Platform.runLater(() -> {
                            parentWindow.getChildren().remove(bullet);
                            System.out.println("bullet remove 3");
                            System.out.println(newX + " " + newY + " " + bulletRadius + " " + goalX2 + " " + goalY2 + " " + r2);
                            cc.sendAction(new ActionMsg(ActionType.UPDSC1));
                        });
                    } else {
                        Platform.runLater(() -> bullet.setCenterX(newX));
                    }
                }
            }
            try {
                Thread.sleep(16); // ~60 FPS (1000ms / 60 = 16ms)
            } catch (InterruptedException e) {
                System.out.println("Sleep error1");
            }
        }
    }

    @FXML
    protected void togglePause() {
        cc.sendAction(new ActionMsg(ActionType.STOP));
    }

    private boolean checkCollision(double x1, double y1, double r1, double x2, double y2, double r2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= r1 + r2;
    }

    @FXML
    protected void toEndGame() {
        circle1.setCenterY(150);
        circle2.setCenterY(150);
        pane.setOnMouseMoved(null);
        m.getAllInfo().ResetStatistic();
        cc.sendAction(new ActionMsg(ActionType.END));
        System.out.println(m.getAllInfo().getScores());
        System.out.println(m.getAllInfo().getShots());
    }
}