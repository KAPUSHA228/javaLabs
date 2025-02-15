package org.example.lab1new;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloController {

    private boolean isFollowing = false;
    private boolean isRunning1 = false;
    private boolean isRunning2 = false;
    private boolean isPaused = false;
    private int shoot = 0;
    private int score = 0;
    @FXML
    private Pane parentWindow;
    @FXML
    private Label shoots;
    @FXML
    private Label hits;
    @FXML
    private Polygon shooting;
    @FXML
    private Pane pane;
    @FXML
    private Circle circle1;
    @FXML
    private Circle circle2;

    @FXML
    protected void toStartGame() {
        isFollowing = true;
        isPaused = false;
        pane.setOnMouseMoved(this::handleMouseMove);
        new Thread(this::moveCircle1).start();
        new Thread(this::moveCircle2).start();

    }
    private void moveCircle1() {
        byte direction = 1;
        int speed = 5;
        if(!isRunning1){
            isRunning1=true;

            while (isFollowing&&!isPaused) {
                double newY = circle1.getCenterY() + speed * direction;

                if (newY <= 0 || newY >= parentWindow.getHeight() - 30) {
                    direction *=-1;
                }

                Platform.runLater(()->circle1.setCenterY(newY));
                try {
                    Thread.sleep(16); // ~60 FPS (1000ms / 60 = 16ms)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }isRunning1 = false;
        }

    }
    private void moveCircle2() {
        byte direction = 1;
        int speed = 2;
        if(!isRunning2){
            isRunning2=true;

            while (isFollowing&&!isPaused) {
                double newY = circle2.getCenterY() + speed * direction;

                if (newY <= 0 || newY >= parentWindow.getHeight() - 50) {
                    direction *=-1;
                }

                Platform.runLater(()->circle2.setCenterY(newY));
                try {
                    Thread.sleep(16); // ~60 FPS (1000ms / 60 = 16ms)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }isRunning2 = false;
        }}
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
        if (isFollowing&&!isPaused) {
            shoot++;
            shoots.setText(String.valueOf(shoot));
            Circle bullet = new Circle(5);

            double initialX = pane.getLayoutX() + shooting.getLayoutX()+20;
            double initialY = shooting.getLayoutY()+10;

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

        while (isBulletMoving.get()&&!isPaused) {
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
                        System.out.println(newX+" "+newY+" "+bulletRadius+" "+goalX1+" "+goalY1+" " +r1);
                        score+=2;
                        hits.setText(String.valueOf(score));
                    });
                } else {
                    if (checkCollision(newX, newY, bulletRadius, goalX2, goalY2, r2)) {
                        isBulletMoving.set(false);
                        Platform.runLater(() -> {
                            parentWindow.getChildren().remove(bullet);
                            System.out.println("bullet remove 3");
                            System.out.println(newX+" "+newY+" " +bulletRadius+" " + goalX2+" " + goalY2 +" " +r2);

                            score ++;
                            hits.setText(String.valueOf(score));
                        });
                    } else {
                        Platform.runLater(() -> bullet.setCenterX(newX));
                    }
                }
            }

            try {
                Thread.sleep(16); // ~60 FPS (1000ms / 60 = 16ms)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    protected void togglePause() {
        isPaused = !isPaused;

        if (isPaused) {
            System.out.println("Go on");
            pane.setOnMouseMoved(null);
        } else {
            System.out.println("On pause");
            pane.setOnMouseMoved(this::handleMouseMove);

            if (isFollowing) {
                new Thread(this::moveCircle1).start();
                new Thread(this::moveCircle2).start();
            }
        }
    }
    private boolean checkCollision(double x1, double y1, double r1, double x2, double y2, double r2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= r1 + r2;
    }
    @FXML
    protected void toEndGame() {
        isFollowing=false;
        isPaused = false;
        pane.setOnMouseMoved(null);
        shoot=0;
        score=0;
        shoots.setText(String.valueOf(shoot));
        hits.setText(String.valueOf(score));
        isRunning1=false;
        isRunning2=false;
        circle1.setCenterY(0);
        circle2.setCenterY(0);
    }
}