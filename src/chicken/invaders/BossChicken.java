package chicken.invaders;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class BossChicken extends Item {
    private final int maxHealth = 50;
    private int health = maxHealth;

    private Plane plane;
    private GamePane pane;
    private AnimationTimer moveTimer;
    private Timeline eggTimer;
    private Random rand = new Random();

    private Rectangle healthBarBack;
    private Rectangle healthBarFront;

    public BossChicken(GamePane pane, Plane plane) {
        super("file:images/boss.png", 300, 50);
        this.plane = plane;
        this.pane = pane;
        pane.getChildren().add(this.shape);

        initializeHealthBar();
        startMovement();
        startEggAttack();
    }

    private void initializeHealthBar() {
        double width = 100;
        double height = 10;

        healthBarBack = new Rectangle(width, height);
        healthBarBack.setFill(Color.DARKRED);
        healthBarBack.setX(shape.getX());
        healthBarBack.setY(shape.getY() - 20);

        healthBarFront = new Rectangle(width, height);
        healthBarFront.setFill(Color.LIMEGREEN);
        healthBarFront.setX(shape.getX());
        healthBarFront.setY(shape.getY() - 20);

        pane.getChildren().addAll(healthBarBack, healthBarFront);
    }

    public void onHit() {
        SoundManager.playSound("boss_hit");
    }

    public void onDeath() {
        SoundManager.playSound("boss_die");
    }

    public void takeDamage() {
        onHit();
        health--;
        updateHealthBar();

        if (health <= 0) {
            die();
        }
    }

    private void updateHealthBar() {
        double healthPercent = (double) health / maxHealth;
        healthBarFront.setWidth(healthPercent * 100);
    }

    public void die() {
        onDeath();
        this.shape.setVisible(false);

        if (moveTimer != null) moveTimer.stop();
        if (eggTimer != null) eggTimer.stop();

        pane.getChildren().removeAll(healthBarBack, healthBarFront);

        pane.addScore(500);
        plane.win();
    }

    private void startMovement() {
        moveTimer = new AnimationTimer() {
            private double dx = 2;
            private double dy = 2;

            @Override
            public void handle(long now) {
                double newX = shape.getX() + dx;
                double newY = shape.getY() + dy;

                if (newX < 0 || newX > pane.getWidth() - shape.getImage().getWidth()) dx *= -1;
                if (newY < 0 || newY > pane.getHeight() - shape.getImage().getHeight()) dy *= -1;

                shape.setX(shape.getX() + dx);
                shape.setY(shape.getY() + dy);

                // Update health bar position
                healthBarBack.setX(shape.getX());
                healthBarBack.setY(shape.getY() - 20);
                healthBarFront.setX(shape.getX());
                healthBarFront.setY(shape.getY() - 20);
            }
        };
        moveTimer.start();
    }

    private void startEggAttack() {
        eggTimer = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(60 * i);
                BossEgg newBossEgg = new BossEgg(shape.getX() + 40, shape.getY() + 40, pane, plane, angle);
                pane.getBossEggs().add(newBossEgg);
            }
        }));

        eggTimer.setCycleCount(Timeline.INDEFINITE);
        eggTimer.play();
    }
}
