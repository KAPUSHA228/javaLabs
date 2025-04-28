package org.example.lab1new;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloController implements IObserver {
    private final Model m = BModel.build();
    private ClientConnect cc;
    private final Gson json = new Gson();
    boolean showWinner = false;
    private static final int port = 3124;
    @FXML
    public GridPane database;
    @FXML
    public TextField naming;
    @FXML
    private Label id;
    @FXML
    private Button preparing;
    @FXML
    private Pane statMenu;
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
            if ((cc != null) && !m.getAllInfo().getReadyI(cc.getID())) {
                preparing.setDisable(false);
                preparing.setVisible(true);
            }
            if (m.getAllInfo().isGameStarted() && !m.getAllInfo().isPaused()) {
                pane.setOnMouseMoved(this::handleMouseMove);
            } else {
                pane.setOnMouseMoved(null);
            }
            if (m.getAllInfo().getWinnerId() != -1 && !showWinner) {
                showWinner = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("GAME OVER");
                alert.setContentText("WINNER IS " + m.getAllInfo().getWinnerId());
                alert.showAndWait();
                cc.sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.END))));
            }

            statMenu.getChildren().clear();
            double startX = 10;
            double spacing = 10;
            for (int i = 0; i < m.getAllInfo().getNames().size(); i++) {
                VBox vbox = new VBox();
                String id = String.valueOf(i);
                String score = String.valueOf(m.getAllInfo().getScoreI(i));
                String shot = String.valueOf(m.getAllInfo().getShotI(i));
                String name = String.valueOf(m.getAllInfo().getNameI(i));
                vbox.setStyle("-fx-border-color: black; -fx-padding: 10;");
                vbox.setPrefHeight(82.0);
                vbox.setPrefWidth(125.00);
                vbox.setLayoutX(startX + (vbox.getPrefWidth() + spacing) * i);
                Label l1 = new Label("Номер игрока: " + id);
                Label l2 = new Label("Имя игрока: " + name);
                Label l3 = new Label("Счет игрока: " + score);
                Label l4 = new Label("Выстрелов: " + shot);
                vbox.getChildren().addAll(l1, l2, l3, l4);
                statMenu.getChildren().add(vbox);
            }
            circle1.setCenterY(m.getAllInfo().getC1().getCenterY());
            circle2.setCenterY(m.getAllInfo().getC2().getCenterY());

        });
    }

    @FXML
    public void initialize() {
        preparing.setDisable(true);
        preparing.setVisible(false);
        m.addServers(this);
    }

    @FXML
    public void connect() {
        try {
            System.out.println(naming.getText());
            naming.setDisable(true);
            naming.setVisible(false);
            connecting.setDisable(true);
            connecting.setVisible(false);
            InetAddress ip = InetAddress.getLocalHost();
            Socket cs = new Socket(ip, port);
            cc = new ClientConnect(cs, false, naming.getText());
            cc.sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.UPDMODEL))));
            id.setText(String.valueOf(cc.getID()));
            if (!m.getAllInfo().isGameStarted()) {
                preparing.setDisable(false);
                preparing.setVisible(true);
            }
            System.out.println("IGET" + m.getAllInfo().getScoreI(0));
        } catch (IOException e) {
            System.err.println("Error2");
        }

    }

    @FXML
    protected void toStartGame() {
        showWinner = false;
        cc.sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.START))));
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
            cc.sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.UPDSH))));
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
                        cc.sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.UPDSC2))));
                    });

                } else {
                    if (checkCollision(newX, newY, bulletRadius, goalX2, goalY2, r2)) {
                        isBulletMoving.set(false);
                        Platform.runLater(() -> {
                            parentWindow.getChildren().remove(bullet);
                            System.out.println("bullet remove 3");
                            System.out.println(newX + " " + newY + " " + bulletRadius + " " + goalX2 + " " + goalY2 + " " + r2);
                            cc.sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.UPDSC1))));
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
        cc.sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.STOP))));
    }

    @FXML
    protected void toReady() {
        cc.sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.READY))));
        preparing.setDisable(true);
        preparing.setVisible(false);
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
        cc.sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.END))));
        System.out.println(m.getAllInfo().getScores());
        System.out.println(m.getAllInfo().getShots());
    }


    public synchronized void toAccessDB(ActionEvent actionEvent) {
        if (cc != null) {
            cc.sendMessage(new Message(MessageType.DBQuery, ""));
            ActionMsg tmp = json.fromJson(cc.getMessage().getData(), ActionMsg.class);
            ArrayList<Player> board = tmp.getLeaderBoard();
            System.out.println("FORM SZ " + board.size());

            Platform.runLater(() -> {
                if (board != null && !board.isEmpty()) {
                    database.getChildren().clear();
                    for (int i = 0; i < board.size(); i++) {
                        database.add(new Label(board.get(i).getName()), 0, i);
                        database.add(new Label(String.valueOf(board.get(i).getWins())), 1, i);
                    }
                } else {
                    System.out.println("Данные не получены или пусты");
                }
            });
        }

    }
}